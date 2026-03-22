package com.akine_api.interfaces.api.v1.cobro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CobroPacienteRequest {
    @NotNull
    private UUID cajaDiariaId;
    @NotNull
    private UUID pacienteId;
    private UUID sesionId;
    @NotNull
    @Positive
    private BigDecimal importeTotal;
    @NotEmpty
    @Valid
    private List<CobroPacienteDetalleRequest> detalles;
    private String observaciones;
}
