package com.akine_api.application.dto.command;

import java.time.LocalDate;

public record CreateMyPacienteCommand(
        String dni,
        String nombre,
        String apellido,
        String telefono,
        String email,
        LocalDate fechaNacimiento,
        String sexo,
        String domicilio,
        String nacionalidad,
        String estadoCivil,
        String profesion,
        String obraSocialNombre,
        String obraSocialPlan,
        String obraSocialNroAfiliado
) {}
