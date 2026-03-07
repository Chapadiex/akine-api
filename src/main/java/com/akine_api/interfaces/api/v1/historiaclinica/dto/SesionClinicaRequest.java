package com.akine_api.interfaces.api.v1.historiaclinica.dto;

import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record SesionClinicaRequest(
        @NotNull UUID profesionalId,
        UUID turnoId,
        UUID boxId,
        @NotNull LocalDateTime fechaAtencion,
        @NotNull HistoriaClinicaTipoAtencion tipoAtencion,
        @Size(max = 500) String motivoConsulta,
        @Size(max = 1000) String resumenClinico,
        @Size(max = 2000) String subjetivo,
        @Size(max = 2000) String objetivo,
        @Size(max = 2000) String evaluacion,
        @Size(max = 2000) String plan
) {}
