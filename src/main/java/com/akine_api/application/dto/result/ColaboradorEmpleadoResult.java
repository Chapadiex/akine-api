package com.akine_api.application.dto.result;

import com.akine_api.domain.model.ColaboradorCuentaStatus;
import com.akine_api.domain.model.ColaboradorEstado;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ColaboradorEmpleadoResult(
        UUID id,
        UUID consultorioId,
        UUID userId,
        String nombre,
        String apellido,
        String dni,
        LocalDate fechaNacimiento,
        String cargo,
        String email,
        String telefono,
        String direccion,
        String notasInternas,
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
