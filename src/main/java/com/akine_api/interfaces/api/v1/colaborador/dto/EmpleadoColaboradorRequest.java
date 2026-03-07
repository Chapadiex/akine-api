package com.akine_api.interfaces.api.v1.colaborador.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record EmpleadoColaboradorRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank String dni,
        @NotNull @Past LocalDate fechaNacimiento,
        @NotBlank String cargo,
        @NotBlank @Email String email,
        @NotBlank String telefono,
        @NotBlank String direccion,
        String notasInternas
) {}
