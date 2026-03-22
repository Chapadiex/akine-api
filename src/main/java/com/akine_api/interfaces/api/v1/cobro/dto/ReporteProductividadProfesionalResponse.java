package com.akine_api.interfaces.api.v1.cobro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ReporteProductividadProfesionalResponse {
    private UUID profesionalId;
    private long cantidadSesiones;
    private BigDecimal importeTotalLiquidado;
    private BigDecimal importeObraSocial;
    private BigDecimal importePaciente;
}
