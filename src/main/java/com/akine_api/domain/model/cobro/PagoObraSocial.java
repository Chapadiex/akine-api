package com.akine_api.domain.model.cobro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoObraSocial {

    private UUID id;
    private UUID consultorioId;
    private UUID loteId;
    private UUID financiadorId;

    private BigDecimal importeEsperado;
    private BigDecimal importeRecibido;
    private BigDecimal diferencia;

    private LocalDate fechaNotificacion;
    private LocalDate fechaImputacion;

    /** FK a caja diaria — solo se completa cuando se imputa */
    private UUID cajaDiariaId;
    private UUID imputadoPor;
    private Instant imputadoEn;

    private String observaciones;
    private UUID registradoPor;

    private Instant createdAt;
    private Instant updatedAt;
    private Long version;
}
