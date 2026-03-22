package com.akine_api.application.service.cobro;

import com.akine_api.domain.exception.CobroNotFoundException;
import com.akine_api.domain.model.cobro.*;
import com.akine_api.domain.model.cobro.PagoObraSocial;
import com.akine_api.domain.repository.cobro.MovimientoCajaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovimientoCajaService {

    private final MovimientoCajaRepositoryPort repositoryPort;

    /**
     * Generates an INGRESO movement for a patient payment.
     * Called from CobroPacienteService within the same transaction.
     */
    @Transactional
    public MovimientoCaja generarDesde(CobroPaciente cobro) {
        MovimientoCaja movimiento = MovimientoCaja.builder()
                .consultorioId(cobro.getConsultorioId())
                .cajaDiariaId(cobro.getCajaDiariaId())
                .tipoMovimiento(TipoMovimiento.INGRESO)
                .origenMovimiento(OrigenMovimiento.COBRO_PACIENTE)
                .origenId(cobro.getId())
                .fechaHora(Instant.now())
                .descripcion("Cobro paciente #" + cobro.getId())
                .importe(cobro.getImporteTotal())
                .signo("PLUS")
                .medioPago(cobro.getDetalles() != null && cobro.getDetalles().size() == 1
                        ? cobro.getDetalles().get(0).getMedioPago()
                        : null)
                .esAnulable(true)
                .usuarioId(cobro.getCobradoPor())
                .anulado(false)
                .build();
        return repositoryPort.save(movimiento);
    }

    /**
     * Marks original movement as anulado and creates a reversal EGRESO movement.
     * Called from CobroPacienteService.anular() within the same transaction.
     */
    @Transactional
    public MovimientoCaja anularMovimientoDe(UUID cobroPacienteId, UUID cajaDiariaId,
                                              UUID consultorioId, UUID usuarioId, String motivo) {
        List<MovimientoCaja> movimientos = repositoryPort.findByCajaDiariaId(cajaDiariaId);
        MovimientoCaja original = movimientos.stream()
                .filter(m -> OrigenMovimiento.COBRO_PACIENTE.equals(m.getOrigenMovimiento())
                        && cobroPacienteId.equals(m.getOrigenId())
                        && !Boolean.TRUE.equals(m.getAnulado()))
                .findFirst()
                .orElseThrow(() -> new CobroNotFoundException(
                        "No se encontró movimiento activo para cobro " + cobroPacienteId));

        original.setAnulado(true);
        original.setAnuladoPor(usuarioId);
        original.setAnuladoEn(Instant.now());
        original.setMotivoAnulacion(motivo);
        repositoryPort.save(original);

        MovimientoCaja reversal = MovimientoCaja.builder()
                .consultorioId(consultorioId)
                .cajaDiariaId(cajaDiariaId)
                .tipoMovimiento(TipoMovimiento.EGRESO)
                .origenMovimiento(OrigenMovimiento.COBRO_PACIENTE)
                .origenId(cobroPacienteId)
                .fechaHora(Instant.now())
                .descripcion("Anulación cobro paciente #" + cobroPacienteId)
                .importe(original.getImporte())
                .signo("MINUS")
                .medioPago(original.getMedioPago())
                .esAnulable(false)
                .usuarioId(usuarioId)
                .anulado(false)
                .build();
        return repositoryPort.save(reversal);
    }

    /**
     * Generates an INGRESO movement for an OS payment imputado to a caja.
     * Called from PagoObraSocialService.imputar() within the same transaction.
     */
    @Transactional
    public MovimientoCaja generarDesde(PagoObraSocial pago, UUID cajaDiariaId, UUID consultorioId, UUID usuarioId) {
        MovimientoCaja movimiento = MovimientoCaja.builder()
                .consultorioId(consultorioId)
                .cajaDiariaId(cajaDiariaId)
                .tipoMovimiento(TipoMovimiento.INGRESO)
                .origenMovimiento(OrigenMovimiento.PAGO_OS)
                .origenId(pago.getId())
                .fechaHora(Instant.now())
                .descripcion("Pago OS lote #" + pago.getLoteId())
                .importe(pago.getImporteRecibido())
                .signo("PLUS")
                .medioPago(null)
                .esAnulable(false)
                .usuarioId(usuarioId)
                .anulado(false)
                .build();
        return repositoryPort.save(movimiento);
    }

    @Transactional(readOnly = true)
    public List<MovimientoCaja> findByCajaDiariaId(UUID cajaDiariaId) {
        return repositoryPort.findByCajaDiariaId(cajaDiariaId);
    }
}
