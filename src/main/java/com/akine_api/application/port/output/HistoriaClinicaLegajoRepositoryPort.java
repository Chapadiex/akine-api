package com.akine_api.application.port.output;

import com.akine_api.domain.model.HistoriaClinicaLegajo;

import java.util.Optional;
import java.util.UUID;

public interface HistoriaClinicaLegajoRepositoryPort {
    HistoriaClinicaLegajo save(HistoriaClinicaLegajo legajo);
    Optional<HistoriaClinicaLegajo> findByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId);
}
