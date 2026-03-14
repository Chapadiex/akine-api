package com.akine_api.interfaces.api.v1.paciente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record PacienteAdminUpdateRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 100) String apellido,
        @NotBlank @Size(max = 30) String telefono,
        @Email @Size(max = 255) String email,
        LocalDate fechaNacimiento,
        @Size(max = 30) String sexo,
        @Size(max = 255) String domicilio,
        @Size(max = 100) String nacionalidad,
        @Size(max = 50) String estadoCivil,
        List<String> profesiones,
        @Size(max = 150) String obraSocialNombre,
        @Size(max = 100) String obraSocialPlan,
        @Size(max = 100) String obraSocialNroAfiliado
) {}
