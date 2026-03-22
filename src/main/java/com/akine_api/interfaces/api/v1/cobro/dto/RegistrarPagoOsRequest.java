package com.akine_api.interfaces.api.v1.cobro.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RegistrarPagoOsRequest {

    @NotNull
    private UUID loteId;

    @NotNull
    @Positive
    private BigDecimal importeRecibido;

    @NotNull
    private LocalDate fechaNotificacion;

    private String observaciones;
}
