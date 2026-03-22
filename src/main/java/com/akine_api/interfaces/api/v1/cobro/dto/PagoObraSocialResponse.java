package com.akine_api.interfaces.api.v1.cobro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PagoObraSocialResponse {
    private UUID id;
    private UUID consultorioId;
    private UUID loteId;
    private UUID financiadorId;
    private BigDecimal importeEsperado;
    private BigDecimal importeRecibido;
    private BigDecimal diferencia;
    private LocalDate fechaNotificacion;
    private LocalDate fechaImputacion;
    private UUID cajaDiariaId;
    private UUID imputadoPor;
    private Instant imputadoEn;
    private String observaciones;
    private UUID registradoPor;
    private Instant createdAt;
    private Long version;
}
