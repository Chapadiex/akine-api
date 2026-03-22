package com.akine_api.application.service.cobro;

import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.LoteOsConflictException;
import com.akine_api.domain.exception.PagoOsConflictException;
import com.akine_api.domain.exception.PagoOsNotFoundException;
import com.akine_api.domain.model.cobro.EstadoLoteOs;
import com.akine_api.domain.model.cobro.LoteFacturacionOs;
import com.akine_api.domain.model.cobro.PagoObraSocial;
import com.akine_api.domain.repository.cobro.PagoObraSocialRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoObraSocialService {

    private final PagoObraSocialRepositoryPort pagoRepo;
    private final LoteFacturacionOsService loteService;
    private final CajaDiariaService cajaDiariaService;
    private final MovimientoCajaService movimientoCajaService;
    private final AuditoriaEventoService auditoriaService;
    private final UserRepositoryPort userRepo;

    /**
     * Registers the OS payment notification.
     * Does NOT create a caja movement — only records the incoming amount.
     * Marks the lote as LIQUIDADO.
     */
    @Transactional
    public PagoObraSocial registrar(UUID consultorioId, UUID loteId,
                                     BigDecimal importeRecibido, LocalDate fechaNotificacion,
                                     String observaciones, String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);
        LoteFacturacionOs lote = loteService.findById(loteId);

        if (!lote.getConsultorioId().equals(consultorioId)) {
            throw new LoteOsConflictException("El lote no pertenece al consultorio indicado.");
        }
        if (lote.getEstado() != EstadoLoteOs.PRESENTADO && lote.getEstado() != EstadoLoteOs.CERRADO) {
            throw new LoteOsConflictException(
                    "Solo se puede registrar pago para lotes CERRADO o PRESENTADO. Estado: " + lote.getEstado());
        }

        BigDecimal diferencia = importeRecibido.subtract(lote.getImporteNeto());

        PagoObraSocial pago = PagoObraSocial.builder()
                .consultorioId(consultorioId)
                .loteId(loteId)
                .financiadorId(lote.getFinanciadorId())
                .importeEsperado(lote.getImporteNeto())
                .importeRecibido(importeRecibido)
                .diferencia(diferencia)
                .fechaNotificacion(fechaNotificacion)
                .observaciones(observaciones)
                .registradoPor(usuarioId)
                .build();

        PagoObraSocial saved = pagoRepo.save(pago);

        // Move lote to LIQUIDADO
        loteService.marcarLiquidado(loteId, usuarioId);

        auditoriaService.registrar(consultorioId, "PagoObraSocial", saved.getId(),
                "REGISTRO", null, "REGISTRADO", usuarioId, null);

        log.info("Pago OS registrado {} lote={} recibido={} diferencia={}",
                saved.getId(), loteId, importeRecibido, diferencia);
        return saved;
    }

    /**
     * Imputar: links the payment to an open caja and creates a MovimientoCaja INGRESO.
     * This is the only operation that impacts the caja balance.
     */
    @Transactional
    public PagoObraSocial imputar(UUID pagoId, UUID cajaDiariaId, String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);
        PagoObraSocial pago = findById(pagoId);

        if (pago.getCajaDiariaId() != null) {
            throw new PagoOsConflictException(
                    "El pago ya fue imputado a la caja " + pago.getCajaDiariaId());
        }

        // Validate caja is open
        cajaDiariaService.validarCajaAbierta(cajaDiariaId);

        pago.setCajaDiariaId(cajaDiariaId);
        pago.setImputadoPor(usuarioId);
        pago.setImputadoEn(Instant.now());
        pago.setFechaImputacion(LocalDate.now());

        PagoObraSocial saved = pagoRepo.save(pago);

        // Generate caja movement
        movimientoCajaService.generarDesde(saved, cajaDiariaId, pago.getConsultorioId(), usuarioId);

        // Update caja totals
        cajaDiariaService.sumarIngresosOs(cajaDiariaId, saved.getImporteRecibido());

        auditoriaService.registrar(pago.getConsultorioId(), "PagoObraSocial", pagoId,
                "IMPUTACION", null, cajaDiariaId.toString(), usuarioId, null);

        log.info("Pago OS {} imputado a caja {} importe={}", pagoId, cajaDiariaId, saved.getImporteRecibido());
        return saved;
    }

    @Transactional(readOnly = true)
    public PagoObraSocial findById(UUID pagoId) {
        return pagoRepo.findById(pagoId)
                .orElseThrow(() -> new PagoOsNotFoundException("Pago OS no encontrado: " + pagoId));
    }

    @Transactional(readOnly = true)
    public List<PagoObraSocial> findByConsultorioId(UUID consultorioId) {
        return pagoRepo.findByConsultorioId(consultorioId);
    }

    @Transactional(readOnly = true)
    public List<PagoObraSocial> findByLoteId(UUID loteId) {
        return pagoRepo.findByLoteId(loteId);
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }
}
