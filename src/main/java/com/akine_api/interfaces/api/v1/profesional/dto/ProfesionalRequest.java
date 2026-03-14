package com.akine_api.interfaces.api.v1.profesional.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfesionalRequest(
        @NotBlank
        @Size(max = 100)
        String nombre,

        @NotBlank
        @Size(max = 100)
        String apellido,

        @NotBlank
        @Pattern(regexp = "^[0-9]{7,10}$", message = "El DNI debe tener entre 7 y 10 dígitos")
        String nroDocumento,

        @Size(max = 50)
        String matricula,

        @Size(max = 150)
        String especialidad,

        @Size(max = 1000)
        String especialidades,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @Size(max = 30)
        String telefono,

        @Size(max = 255)
        String domicilio,

        @Size(max = 500)
        String fotoPerfilUrl
) {}
