package com.akine_api.application.service.cobro;

import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.CobroNotFoundException;
import com.akine_api.domain.exception.CobroValidationException;
import com.akine_api.domain.model.cobro.*;
import com.akine_api.domain.repository.cobro.CobroPacienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CobroPacienteService {

    private final CobroPacienteRepositoryPort repositoryPort;
    private final CajaDiariaService cajaDiariaService;
    private final MovimientoCajaService movimientoCajaService;
    private final AuditoriaEventoService auditoriaService;
    private final UserRepositoryPort userRepo;

    @Transactional
    public CobroPaciente cobrar(UUID consultorioId, UUID cajaDiariaId, UUID pacienteId,
                                 UUID sesionId, BigDecimal importeTotal,
                                 List<CobroPacienteDetalle> detalles,
                                 String observaciones, String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);

        // 1. Validar caja abierta
        cajaDiariaService.validarCajaAbierta(cajaDiariaId);

        // 2. Validar suma de detalles == importeTotal
        if (detalles == null || detalles.isEmpty()) {
            throw new CobroValidationException("Se requiere al menos un detalle de pago");
        }
        BigDecimal sumaDetalles = detalles.stream()
                .map(CobroPacienteDetalle::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sumaDetalles.compareTo(importeTotal) != 0) {
            throw new CobroValidationException(
                    "La suma de los detalles (" + sumaDetalles
                    + ") no coincide con el importe total (" + importeTotal + ")");
        }

        // 3. Persistir cobro
        CobroPaciente cobro = CobroPaciente.builder()
                .consultorioId(consultorioId)
                .cajaDiariaId(cajaDiariaId)
                .pacienteId(pacienteId)
                .sesionId(sesionId)
                .estado(EstadoCobroPaciente.COBRADO_TOTAL)
                .fechaCobro(LocalDate.now())
                .importeTotal(importeTotal)
                .esPagoMixto(detalles.size() > 1)
                .reciboEmitido(false)
                .observaciones(observaciones)
                .cobradoPor(usuarioId)
                .detalles(detalles)
                .build();

        CobroPaciente saved = repositoryPort.save(cobro);

        // 4. Generar movimiento de caja
        movimientoCajaService.generarDesde(saved);

        // 5. Actualizar totales de caja
        cajaDiariaService.sumarIngresoPaciente(cajaDiariaId, importeTotal);

        auditoriaService.registrar(consultorioId, "CobroPaciente", saved.getId(),
                "COBRO", null, EstadoCobroPaciente.COBRADO_TOTAL.name(), usuarioId, null);

        return saved;
    }

    @Transactional
    public CobroPaciente anular(UUID cobroId, String motivo, String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);
        CobroPaciente cobro = findById(cobroId);

        if (EstadoCobroPaciente.ANULADO.equals(cobro.getEstado())) {
            throw new CobroValidationException("El cobro " + cobroId + " ya está anulado");
        }

        String estadoAnterior = cobro.getEstado().name();

        cobro.setEstado(EstadoCobroPaciente.ANULADO);
        cobro.setAnuladoPor(usuarioId);
        cobro.setAnuladoEn(Instant.now());
        cobro.setMotivoAnulacion(motivo);

        CobroPaciente saved = repositoryPort.save(cobro);

        // Movimiento inverso en caja
        movimientoCajaService.anularMovimientoDe(
                cobroId, cobro.getCajaDiariaId(), cobro.getConsultorioId(), usuarioId, motivo);

        // Restar del total de caja
        cajaDiariaService.restarIngresoPaciente(cobro.getCajaDiariaId(), cobro.getImporteTotal());

        auditoriaService.registrar(cobro.getConsultorioId(), "CobroPaciente", cobroId,
                "ANULACION", estadoAnterior, EstadoCobroPaciente.ANULADO.name(), usuarioId, motivo);

        return saved;
    }

    @Transactional(readOnly = true)
    public CobroPaciente findById(UUID cobroId) {
        return repositoryPort.findById(cobroId)
                .orElseThrow(() -> new CobroNotFoundException("Cobro no encontrado: " + cobroId));
    }

    @Transactional(readOnly = true)
    public List<CobroPaciente> findByCajaDiariaId(UUID cajaDiariaId) {
        return repositoryPort.findByCajaDiariaId(cajaDiariaId);
    }

    @Transactional(readOnly = true)
    public List<CobroPaciente> findByPacienteId(UUID pacienteId, UUID consultorioId) {
        return repositoryPort.findByPacienteId(pacienteId, consultorioId);
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }
}
