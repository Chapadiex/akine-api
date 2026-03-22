package com.akine_api.application.service.cobro;

import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.LoteOsConflictException;
import com.akine_api.domain.exception.LoteOsNotFoundException;
import com.akine_api.domain.model.cobro.*;
import com.akine_api.domain.repository.cobro.LiquidacionSesionRepositoryPort;
import com.akine_api.domain.repository.cobro.LoteFacturacionOsRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoteFacturacionOsService {

    private final LoteFacturacionOsRepositoryPort loteRepo;
    private final LiquidacionSesionRepositoryPort liquidacionRepo;
    private final AuditoriaEventoService auditoriaService;
    private final UserRepositoryPort userRepo;

    /**
     * Generates a new BORRADOR lote with all facturable-OS liquidaciones for the
     * given consultorio/financiador that are not yet in any active lote.
     * periodo: YYYY-MM (used as label, no date filtering — all pending sessions are included).
     */
    @Transactional
    public LoteFacturacionOs generarLote(UUID consultorioId, UUID financiadorId,
                                          UUID planId, String periodo, String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);

        // Find all facturable liquidaciones for this financiador
        List<LiquidacionSesion> candidatas = liquidacionRepo
                .findFacturablesByConsultorioAndFinanciador(consultorioId, financiadorId);

        if (candidatas.isEmpty()) {
            throw new LoteOsConflictException(
                    "No hay liquidaciones facturables pendientes para el financiador indicado.");
        }

        // Exclude those already in an active lote
        Set<UUID> yaEnLote = Set.copyOf(loteRepo.findLiquidacionIdsEnLotesActivos(consultorioId));
        List<LiquidacionSesion> sesionesAIncluir = candidatas.stream()
                .filter(l -> !yaEnLote.contains(l.getId()))
                .toList();

        if (sesionesAIncluir.isEmpty()) {
            throw new LoteOsConflictException(
                    "Todas las liquidaciones facturables ya están incluidas en un lote activo.");
        }

        // Build detalles
        List<LoteFacturacionOsDetalle> detalles = sesionesAIncluir.stream()
                .map(l -> LoteFacturacionOsDetalle.builder()
                        .liquidacionSesionId(l.getId())
                        .sesionId(l.getSesionId())
                        .pacienteId(l.getPacienteId())
                        .importeOs(l.getImporteObraSocial())
                        .build())
                .toList();

        BigDecimal totalOs = sesionesAIncluir.stream()
                .map(LiquidacionSesion::getImporteObraSocial)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LoteFacturacionOs lote = LoteFacturacionOs.builder()
                .consultorioId(consultorioId)
                .financiadorId(financiadorId)
                .planId(planId)
                .periodo(periodo)
                .estado(EstadoLoteOs.BORRADOR)
                .cantidadSesiones(sesionesAIncluir.size())
                .importeTotalOs(totalOs)
                .importeNeto(totalOs)
                .detalles(detalles)
                .creadoPor(usuarioId)
                .build();

        LoteFacturacionOs saved = loteRepo.save(lote);
        log.info("Lote OS generado {} financiador={} sesiones={} total={}",
                saved.getId(), financiadorId, sesionesAIncluir.size(), totalOs);

        auditoriaService.registrar(consultorioId, "LoteFacturacionOs", saved.getId(),
                "GENERACION", null, EstadoLoteOs.BORRADOR.name(), usuarioId, null);
        return saved;
    }

    /**
     * Moves lote from BORRADOR → CERRADO.
     * Once closed, no more detalles can be added and it's ready for OS presentation.
     */
    @Transactional
    public LoteFacturacionOs cerrarLote(UUID loteId, String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);
        LoteFacturacionOs lote = findById(loteId);

        if (lote.getEstado() != EstadoLoteOs.BORRADOR) {
            throw new LoteOsConflictException(
                    "Solo se puede cerrar un lote en estado BORRADOR. Estado actual: " + lote.getEstado());
        }
        if (lote.getCantidadSesiones() == 0) {
            throw new LoteOsConflictException("No se puede cerrar un lote sin sesiones.");
        }

        String estadoAnterior = lote.getEstado().name();
        lote.setEstado(EstadoLoteOs.CERRADO);
        lote.setCerradoEn(Instant.now());
        lote.setCerradoPor(usuarioId);

        LoteFacturacionOs saved = loteRepo.save(lote);
        auditoriaService.registrar(lote.getConsultorioId(), "LoteFacturacionOs", loteId,
                "CIERRE", estadoAnterior, EstadoLoteOs.CERRADO.name(), usuarioId, null);
        return saved;
    }

    /**
     * Moves lote from CERRADO → PRESENTADO (sent to OS).
     */
    @Transactional
    public LoteFacturacionOs marcarPresentado(UUID loteId, String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);
        LoteFacturacionOs lote = findById(loteId);

        if (lote.getEstado() != EstadoLoteOs.CERRADO) {
            throw new LoteOsConflictException(
                    "Solo se puede presentar un lote CERRADO. Estado actual: " + lote.getEstado());
        }

        String estadoAnterior = lote.getEstado().name();
        lote.setEstado(EstadoLoteOs.PRESENTADO);
        lote.setPresentadoEn(Instant.now());
        lote.setPresentadoPor(usuarioId);

        LoteFacturacionOs saved = loteRepo.save(lote);
        auditoriaService.registrar(lote.getConsultorioId(), "LoteFacturacionOs", loteId,
                "PRESENTACION", estadoAnterior, EstadoLoteOs.PRESENTADO.name(), usuarioId, null);
        return saved;
    }

    /**
     * Moves lote from PRESENTADO/CERRADO → LIQUIDADO (called by PagoObraSocialService).
     */
    @Transactional
    public LoteFacturacionOs marcarLiquidado(UUID loteId, UUID usuarioId) {
        LoteFacturacionOs lote = findById(loteId);
        if (lote.getEstado() == EstadoLoteOs.LIQUIDADO) {
            return lote; // idempotent
        }
        String estadoAnterior = lote.getEstado().name();
        lote.setEstado(EstadoLoteOs.LIQUIDADO);
        LoteFacturacionOs saved = loteRepo.save(lote);
        auditoriaService.registrar(lote.getConsultorioId(), "LoteFacturacionOs", loteId,
                "LIQUIDACION", estadoAnterior, EstadoLoteOs.LIQUIDADO.name(), usuarioId, null);
        return saved;
    }

    @Transactional(readOnly = true)
    public LoteFacturacionOs findById(UUID loteId) {
        return loteRepo.findById(loteId)
                .orElseThrow(() -> new LoteOsNotFoundException("Lote OS no encontrado: " + loteId));
    }

    @Transactional(readOnly = true)
    public List<LoteFacturacionOs> findByConsultorioId(UUID consultorioId) {
        return loteRepo.findByConsultorioId(consultorioId);
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }
}
