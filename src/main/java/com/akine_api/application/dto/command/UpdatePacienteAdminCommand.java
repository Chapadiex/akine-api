package com.akine_api.application.dto.command;

import java.time.LocalDate;
import java.util.List;

public record UpdatePacienteAdminCommand(
        String nombre,
        String apellido,
        String telefono,
        String email,
        LocalDate fechaNacimiento,
        String sexo,
        String domicilio,
        String nacionalidad,
        String estadoCivil,
        List<String> profesiones,
        String obraSocialNombre,
        String obraSocialPlan,
        String obraSocialNroAfiliado
) {}
