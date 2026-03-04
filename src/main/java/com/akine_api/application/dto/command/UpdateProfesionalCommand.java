package com.akine_api.application.dto.command;

import java.util.UUID;

public record UpdateProfesionalCommand(
        UUID id,
        UUID consultorioId,
        String nombre,
        String apellido,
        String nroDocumento,
        String matricula,
        String especialidad,
        String especialidades,
        String email,
        String telefono,
        String domicilio,
        String fotoPerfilUrl
) {}
