package com.akine_api.interfaces.api.v1.cobro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnularCobroRequest {
    @NotBlank
    private String motivo;
}
