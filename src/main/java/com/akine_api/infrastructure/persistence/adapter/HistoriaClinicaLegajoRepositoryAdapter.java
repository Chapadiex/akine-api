package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.HistoriaClinicaLegajoRepositoryPort;
import com.akine_api.domain.model.HistoriaClinicaLegajo;
import com.akine_api.infrastructure.persistence.mapper.HistoriaClinicaLegajoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.HistoriaClinicaLegajoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class HistoriaClinicaLegajoRepositoryAdapter implements HistoriaClinicaLegajoRepositoryPort {

    private final HistoriaClinicaLegajoJpaRepository repo;
    private final HistoriaClinicaLegajoEntityMapper mapper;

    public HistoriaClinicaLegajoRepositoryAdapter(HistoriaClinicaLegajoJpaRepository repo,
                                                  HistoriaClinicaLegajoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public HistoriaClinicaLegajo save(HistoriaClinicaLegajo legajo) {
        return mapper.toDomain(repo.save(mapper.toEntity(legajo)));
    }

    @Override
    public Optional<HistoriaClinicaLegajo> findByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId) {
        return repo.findByConsultorioIdAndPacienteId(consultorioId, pacienteId).map(mapper::toDomain);
    }
}
