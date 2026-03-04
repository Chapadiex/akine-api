package com.akine_api.interfaces.api.v1.colaborador.dto;

import com.akine_api.domain.model.ColaboradorCuentaStatus;
import com.akine_api.domain.model.ColaboradorEstado;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ColaboradorProfesionalResponse(
        UUID id,
        UUID consultorioId,
        UUID userId,
        String nombre,
        String apellido,
        String nroDocumento,
        String matricula,
        List<String> especialidades,
        String email,
        String telefono,
        String domicilio,
        String fotoPerfilUrl,
        LocalDate fechaAlta,
        LocalDate fechaBaja,
        String motivoBaja,
        boolean activo,
        ColaboradorEstado estadoColaborador,
        ColaboradorCuentaStatus cuentaStatus,
        Instant ultimoEnvioActivacionAt,
        Instant createdAt,
        Instant updatedAt
) {}
