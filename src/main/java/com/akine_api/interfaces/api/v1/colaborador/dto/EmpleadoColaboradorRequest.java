package com.akine_api.interfaces.api.v1.colaborador.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmpleadoColaboradorRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        String dni,
        @NotBlank String cargo,
        String nroLegajo,
        @NotBlank @Email String email,
        String telefono,
        String notasInternas
) {}
