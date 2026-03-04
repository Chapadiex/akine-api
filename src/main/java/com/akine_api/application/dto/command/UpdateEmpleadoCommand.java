package com.akine_api.application.dto.command;

import java.util.UUID;

public record UpdateEmpleadoCommand(
        UUID consultorioId,
        UUID empleadoId,
        String nombre,
        String apellido,
        String dni,
        String cargo,
        String nroLegajo,
        String email,
        String telefono,
        String notasInternas
) {}
