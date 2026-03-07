package com.akine_api.interfaces.api.v1.especialidad.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspecialidadUpdateRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
        String nombre
) {}
