package com.akine_api.interfaces.api.v1.colaborador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ProfesionalColaboradorRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        String nroDocumento,
        @NotBlank String matricula,
        @NotEmpty List<String> especialidades,
        String email,
        String telefono,
        String domicilio,
        String fotoPerfilUrl,
        String modoAlta
) {}
