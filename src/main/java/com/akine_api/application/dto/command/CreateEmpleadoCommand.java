package com.akine_api.application.dto.command;

import java.util.UUID;

public record CreateEmpleadoCommand(
        UUID consultorioId,
        String nombre,
        String apellido,
        String dni,
        String cargo,
        String nroLegajo,
        String email,
        String telefono,
        String notasInternas
) {}
