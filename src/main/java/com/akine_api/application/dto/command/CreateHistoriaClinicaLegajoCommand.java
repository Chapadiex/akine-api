package com.akine_api.application.dto.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateHistoriaClinicaLegajoCommand(
        UUID consultorioId,
        UUID pacienteId,
        UUID profesionalId,
        LocalDateTime fechaAtencion,
        String motivoConsulta,
        String resumenClinico,
        String subjetivo,
        String objetivo,
        String evaluacion,
        String plan,
        String casoCodigo,
        String casoDescripcion,
        LocalDate casoFechaInicio,
        String casoNotas,
        List<HistoriaClinicaAntecedenteItemCommand> antecedentes,
        UUID actorUserId
) {}
