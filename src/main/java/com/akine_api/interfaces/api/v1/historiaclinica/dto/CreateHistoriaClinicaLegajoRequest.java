package com.akine_api.interfaces.api.v1.historiaclinica.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateHistoriaClinicaLegajoRequest(
        UUID profesionalId,
        LocalDateTime fechaAtencion,
        @Size(max = 500) String motivoConsulta,
        @Size(max = 1000) String resumenClinico,
        @Size(max = 2000) String subjetivo,
        @Size(max = 2000) String objetivo,
        @Size(max = 2000) String evaluacion,
        @Size(max = 2000) String plan,
        @Size(max = 100) String casoCodigo,
        @Size(max = 500) String casoDescripcion,
        LocalDate casoFechaInicio,
        @Size(max = 1000) String casoNotas,
        @Valid List<HistoriaClinicaAntecedenteItemRequest> antecedentes
) {}
