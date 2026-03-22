package com.akine_api.interfaces.api.v1.cobro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class LoteFacturacionOsResponse {
    private UUID id;
    private UUID consultorioId;
    private UUID financiadorId;
    private UUID planId;
    private String periodo;
    private String estado;
    private int cantidadSesiones;
    private BigDecimal importeTotalOs;
    private BigDecimal importeNeto;
    private String observaciones;
    private Instant cerradoEn;
    private UUID cerradoPor;
    private Instant presentadoEn;
    private Instant createdAt;
    private Long version;
    private List<LoteFacturacionOsDetalleResponse> detalles;
}
