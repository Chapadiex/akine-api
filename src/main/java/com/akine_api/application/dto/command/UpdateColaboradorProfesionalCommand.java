package com.akine_api.application.dto.command;

import java.util.List;
import java.util.UUID;

public record UpdateColaboradorProfesionalCommand(
        UUID consultorioId,
        UUID profesionalId,
        String nombre,
        String apellido,
        String nroDocumento,
        String matricula,
        List<String> especialidades,
        String email,
        String telefono,
        String domicilio,
        String fotoPerfilUrl
) {}
