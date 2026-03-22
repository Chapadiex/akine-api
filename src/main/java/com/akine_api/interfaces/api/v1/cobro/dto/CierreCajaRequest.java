package com.akine_api.interfaces.api.v1.cobro.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CierreCajaRequest {
    @NotNull
    private BigDecimal saldoReal;
    private String observaciones;
}
