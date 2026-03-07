package com.akine_api.application.dto.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateEmpleadoCommand(
        UUID consultorioId,
        String nombre,
        String apellido,
        String dni,
        LocalDate fechaNacimiento,
        String cargo,
        String email,
        String telefono,
        String direccion,
        String notasInternas
) {}
