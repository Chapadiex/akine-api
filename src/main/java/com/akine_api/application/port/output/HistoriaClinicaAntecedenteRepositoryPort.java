package com.akine_api.application.port.output;

import com.akine_api.domain.model.HistoriaClinicaAntecedente;

import java.util.List;
import java.util.UUID;

public interface HistoriaClinicaAntecedenteRepositoryPort {
    List<HistoriaClinicaAntecedente> saveAll(List<HistoriaClinicaAntecedente> antecedentes);
    List<HistoriaClinicaAntecedente> findByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId);
    void deleteByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId);
}
