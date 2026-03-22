package com.akine_api.domain.model.cobro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoteFacturacionOs {

    private UUID id;
    private UUID consultorioId;
    private UUID financiadorId;
    private UUID planId;
    private UUID convenioId;

    /** Período de facturación en formato YYYY-MM */
    private String periodo;

    private EstadoLoteOs estado;

    private int cantidadSesiones;
    private BigDecimal importeTotalOs;
    private BigDecimal importeNeto;

    private String observaciones;

    private Instant cerradoEn;
    private UUID cerradoPor;
    private Instant presentadoEn;
    private UUID presentadoPor;

    private UUID creadoPor;

    private List<LoteFacturacionOsDetalle> detalles;

    private Instant createdAt;
    private Instant updatedAt;
    private Long version;
}
