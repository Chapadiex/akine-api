package com.akine_api.interfaces.api.v1.cobro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConvertirParticularRequest {
    @NotBlank(message = "El motivo es obligatorio para convertir a particular")
    private String motivo;
}
