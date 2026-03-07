package com.akine_api.application.dto.command;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateEmpleadoCommand(
        UUID consultorioId,
        UUID empleadoId,
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
