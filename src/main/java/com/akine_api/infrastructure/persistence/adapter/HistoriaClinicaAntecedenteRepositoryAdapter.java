package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.HistoriaClinicaAntecedenteRepositoryPort;
import com.akine_api.domain.model.HistoriaClinicaAntecedente;
import com.akine_api.infrastructure.persistence.mapper.HistoriaClinicaAntecedenteEntityMapper;
import com.akine_api.infrastructure.persistence.repository.HistoriaClinicaAntecedenteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class HistoriaClinicaAntecedenteRepositoryAdapter implements HistoriaClinicaAntecedenteRepositoryPort {

    private final HistoriaClinicaAntecedenteJpaRepository repo;
    private final HistoriaClinicaAntecedenteEntityMapper mapper;

    public HistoriaClinicaAntecedenteRepositoryAdapter(HistoriaClinicaAntecedenteJpaRepository repo,
                                                       HistoriaClinicaAntecedenteEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<HistoriaClinicaAntecedente> saveAll(List<HistoriaClinicaAntecedente> antecedentes) {
        return repo.saveAll(antecedentes.stream().map(mapper::toEntity).toList()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<HistoriaClinicaAntecedente> findByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId) {
        return repo.findByConsultorioIdAndPacienteIdOrderByCriticalDescUpdatedAtDesc(consultorioId, pacienteId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId) {
        repo.deleteByConsultorioIdAndPacienteId(consultorioId, pacienteId);
    }
}
