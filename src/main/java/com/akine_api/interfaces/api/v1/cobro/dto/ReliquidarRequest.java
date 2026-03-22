package com.akine_api.interfaces.api.v1.cobro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReliquidarRequest {
    @NotBlank(message = "El motivo es obligatorio para reliquidar")
    private String motivo;
}
