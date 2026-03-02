package com.akine_api.interfaces.api.v1.profesional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfesionalRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100)
        String apellido,

        @NotBlank(message = "La matrícula es obligatoria")
        @Size(max = 50)
        String matricula,

        @Size(max = 150)
        String especialidad,

        @Size(max = 255)
        String email,

        @Size(max = 30)
        String telefono
) {}
