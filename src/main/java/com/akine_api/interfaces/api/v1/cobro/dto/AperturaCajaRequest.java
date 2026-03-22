package com.akine_api.interfaces.api.v1.cobro.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AperturaCajaRequest {
    @NotNull
    private LocalDate fechaOperativa;
    private String turnoCaja;
    private BigDecimal saldoInicial;
    private String observaciones;
}
