package com.akine_api.interfaces.api.v1.profesional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfesionalRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100)
        String apellido,

        @Pattern(regexp = "^$|^[0-9]{7,10}$", message = "Formato de DNI invalido")
        String nroDocumento,

        @NotBlank(message = "La matricula es obligatoria")
        @Size(max = 50)
        String matricula,

        @Size(max = 150)
        String especialidad,

        @Size(max = 1000)
        String especialidades,

        @Size(max = 255)
        String email,

        @Size(max = 30)
        String telefono,

        @Size(max = 255)
        String domicilio,

        @Size(max = 500)
        String fotoPerfilUrl
) {}
