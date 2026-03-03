package com.akine_api.interfaces.api.v1.turno.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTurnoRequest(
        UUID profesionalId,
        UUID boxId,
        UUID pacienteId,
        @NotNull(message = "La fecha y hora de inicio es obligatoria")
        LocalDateTime fechaHoraInicio,
        @NotNull(message = "La duración es obligatoria")
        @Min(value = 1, message = "La duración debe ser mayor a 0")
        Integer duracionMinutos,
        @Size(max = 500, message = "El motivo no puede superar 500 caracteres")
        String motivoConsulta,
        @Size(max = 1000, message = "Las notas no pueden superar 1000 caracteres")
        String notas,
        String tipoConsulta,
        @Size(max = 50, message = "El teléfono no puede superar 50 caracteres")
        String telefonoContacto
) {}
