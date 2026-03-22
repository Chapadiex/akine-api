package com.akine_api.interfaces.api.v1.cobro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class GenerarLoteOsRequest {

    @NotNull
    private UUID financiadorId;

    private UUID planId;

    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "El período debe tener formato YYYY-MM")
    private String periodo;
}
