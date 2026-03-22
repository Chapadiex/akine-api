package com.akine_api.interfaces.api.v1.cobro.dto;

import com.akine_api.domain.model.cobro.MedioPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CobroPacienteDetalleRequest {
    @NotNull
    private MedioPago medioPago;
    @NotNull
    @Positive
    private BigDecimal importe;
    private String referenciaOperacion;
    private Integer cuotas;
    private String banco;
    private String marcaTarjeta;
}
