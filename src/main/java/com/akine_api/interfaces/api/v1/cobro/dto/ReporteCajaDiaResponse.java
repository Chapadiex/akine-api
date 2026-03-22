package com.akine_api.interfaces.api.v1.cobro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class ReporteCajaDiaResponse {
    private UUID cajaId;
    private UUID consultorioId;
    private LocalDate fechaOperativa;
    private String turnoCaja;
    private String estado;
    private BigDecimal saldoInicial;
    private BigDecimal totalIngresosPaciente;
    private BigDecimal totalIngresosOs;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal saldoTeorico;
    private BigDecimal saldoReal;
    private BigDecimal diferencia;
    private List<MovimientoCajaResponse> movimientos;
}
