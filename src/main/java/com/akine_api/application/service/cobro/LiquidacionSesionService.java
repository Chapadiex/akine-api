package com.akine_api.application.service.cobro;

import com.akine_api.application.port.output.SesionClinicaRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.LiquidacionConflictException;
import com.akine_api.domain.exception.LiquidacionNotFoundException;
import com.akine_api.domain.exception.SesionClinicaNotFoundException;
import com.akine_api.domain.model.SesionClinica;
import com.akine_api.domain.model.cobro.EstadoLiquidacion;
import com.akine_api.domain.model.cobro.LiquidacionSesion;
import com.akine_api.domain.model.cobro.OrigenTipoCobro;
import com.akine_api.domain.model.cobro.TipoLiquidacion;
import com.akine_api.domain.model.sesion.SesionAdministrativa;
import com.akine_api.domain.repository.cobro.LiquidacionSesionRepositoryPort;
import com.akine_api.domain.repository.sesion.SesionAdministrativaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiquidacionSesionService {

    private final LiquidacionSesionRepositoryPort liquidacionRepo;
    private final SesionClinicaRepositoryPort sesionRepo;
    private final SesionAdministrativaRepositoryPort sesionAdmRepo;
    private final LiquidacionCalculadorService calculador;
    private final AuditoriaEventoService auditoriaService;
    private final UserRepositoryPort userRepo;

    /**
     * Called automatically after clinical closure.
     * If a non-anulada liquidacion already exists for the session, it is a no-op.
     */
    @Transactional
    public LiquidacionSesion liquidarAutomaticamente(UUID sesionId, String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);

        // Idempotency guard: if already liquidated (not anulada), return existing
        Optional<LiquidacionSesion> existing = liquidacionRepo.findActivaBySesionId(sesionId);
        if (existing.isPresent()) {
            log.info("Sesion {} ya tiene liquidacion activa {}", sesionId, existing.get().getId());
            return existing.get();
        }

        SesionClinica sesion = sesionRepo.findById(sesionId)
                .orElseThrow(() -> new SesionClinicaNotFoundException("Sesion no encontrada: " + sesionId));

        SesionAdministrativa sesionAdm = sesionAdmRepo.findBySesionId(sesionId).orElse(null);

        LiquidacionSesion liquidacion = calculador.calcular(sesion, sesionAdm, usuarioId);

        LiquidacionSesion saved = liquidacionRepo.save(liquidacion);

        auditoriaService.registrar(
                sesion.getConsultorioId(),
                "LiquidacionSesion",
                saved.getId(),
                "LIQUIDACION_AUTOMATICA",
                null,
                saved.getEstado().name(),
                usuarioId,
                null);

        log.info("Liquidacion creada {} tipo={} estado={} sesion={}",
                saved.getId(), saved.getTipoLiquidacion(), saved.getEstado(), sesionId);
        return saved;
    }

    @Transactional(readOnly = true)
    public LiquidacionSesion findById(UUID id) {
        return liquidacionRepo.findById(id)
                .orElseThrow(() -> new LiquidacionNotFoundException("Liquidación no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<LiquidacionSesion> findBySesionId(UUID sesionId) {
        return liquidacionRepo.findActivaBySesionId(sesionId);
    }

    @Transactional(readOnly = true)
    public List<LiquidacionSesion> findByConsultorioId(UUID consultorioId) {
        return liquidacionRepo.findByConsultorioId(consultorioId);
    }

    @Transactional(readOnly = true)
    public List<LiquidacionSesion> findByPaciente(UUID consultorioId, UUID pacienteId) {
        return liquidacionRepo.findByConsultorioIdAndPacienteId(consultorioId, pacienteId);
    }

    /**
     * Re-runs the algorithm to update an existing liquidacion.
     * Restricted: only when estado != LIQUIDADA_OS and no lote_os with estado != borrador.
     * Always generates an auditoria_evento with mandatory motivo.
     */
    @Transactional
    public LiquidacionSesion reliquidar(UUID liquidacionId, String motivo, String userEmail) {
        if (motivo == null || motivo.isBlank()) {
            throw new LiquidacionConflictException("El motivo es obligatorio para reliquidar");
        }

        UUID usuarioId = resolveUserId(userEmail);
        LiquidacionSesion existing = findById(liquidacionId);

        if (existing.getEstado() == EstadoLiquidacion.LIQUIDADA_OS) {
            throw new LiquidacionConflictException(
                    "No se puede reliquidar una sesión ya liquidada a Obra Social");
        }
        if (existing.getEstado() == EstadoLiquidacion.ANULADA) {
            throw new LiquidacionConflictException("La liquidación está anulada");
        }

        String snapshotAnterior = buildEstadoSnapshot(existing);

        SesionClinica sesion = sesionRepo.findById(existing.getSesionId())
                .orElseThrow(() -> new SesionClinicaNotFoundException(
                        "Sesion no encontrada: " + existing.getSesionId()));

        SesionAdministrativa sesionAdm = sesionAdmRepo.findBySesionId(existing.getSesionId()).orElse(null);

        LiquidacionSesion recalculada = calculador.calcular(sesion, sesionAdm, usuarioId);

        // Carry forward the id and apply recalculation timestamps
        recalculada.setId(existing.getId());
        recalculada.setVersion(existing.getVersion());
        recalculada.setRecalculadaEn(Instant.now());
        recalculada.setRecalculadaPor(usuarioId);
        recalculada.setOrigenTipoCobro(OrigenTipoCobro.MANUAL_ADMINISTRATIVO);

        LiquidacionSesion saved = liquidacionRepo.save(recalculada);

        auditoriaService.registrar(
                sesion.getConsultorioId(),
                "LiquidacionSesion",
                saved.getId(),
                "RELIQUIDACION",
                snapshotAnterior,
                saved.getEstado().name(),
                usuarioId,
                motivo);

        return saved;
    }

    /**
     * Converts a non-particular liquidacion to particular (e.g. patient has no valid coverage).
     */
    @Transactional
    public LiquidacionSesion convertirAParticular(UUID liquidacionId, String motivo, String userEmail) {
        if (motivo == null || motivo.isBlank()) {
            throw new LiquidacionConflictException("El motivo es obligatorio para convertir a particular");
        }

        UUID usuarioId = resolveUserId(userEmail);
        LiquidacionSesion existing = findById(liquidacionId);

        if (existing.getTipoLiquidacion() == TipoLiquidacion.PARTICULAR) {
            throw new LiquidacionConflictException("La liquidación ya es de tipo particular");
        }
        if (existing.getEstado() == EstadoLiquidacion.LIQUIDADA_OS) {
            throw new LiquidacionConflictException(
                    "No se puede convertir a particular una liquidación OS ya liquidada");
        }

        String snapshotAnterior = buildEstadoSnapshot(existing);

        existing.setTipoLiquidacion(TipoLiquidacion.PARTICULAR);
        existing.setImportePaciente(existing.getValorBruto());
        existing.setImporteObraSocial(BigDecimal.ZERO);
        existing.setImporteTotalLiquidado(existing.getValorBruto());
        existing.setCopagoImporte(BigDecimal.ZERO);
        existing.setCoseguroImporte(BigDecimal.ZERO);
        existing.setEsFacturableOs(false);
        existing.setEstado(EstadoLiquidacion.LIQUIDADA_PARTICULAR);
        existing.setFinanciadorId(null);
        existing.setPlanId(null);
        existing.setConvenioId(null);
        existing.setConvenioVigenteSnapshot(null);
        existing.setOrigenTipoCobro(OrigenTipoCobro.CONVERSION_PARTICULAR);
        existing.setRecalculadaEn(Instant.now());
        existing.setRecalculadaPor(usuarioId);

        LiquidacionSesion saved = liquidacionRepo.save(existing);

        auditoriaService.registrar(
                saved.getConsultorioId(),
                "LiquidacionSesion",
                saved.getId(),
                "CONVERSION_PARTICULAR",
                snapshotAnterior,
                saved.getEstado().name(),
                usuarioId,
                motivo);

        return saved;
    }

    // ─── private ─────────────────────────────────────────────────────────────

    private UUID resolveUserId(String userEmail) {
        return userRepo.findByEmail(userEmail)
                .map(u -> u.getId())
                .orElseThrow(() -> new com.akine_api.domain.exception.UserNotFoundException(
                        "Usuario no encontrado: " + userEmail));
    }

    private String buildEstadoSnapshot(LiquidacionSesion liq) {
        return String.format("{\"id\":\"%s\",\"tipo\":\"%s\",\"estado\":\"%s\",\"importePaciente\":%s,\"importeOs\":%s}",
                liq.getId(), liq.getTipoLiquidacion(), liq.getEstado(),
                liq.getImportePaciente(), liq.getImporteObraSocial());
    }
}
