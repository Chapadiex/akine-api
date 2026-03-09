package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.SesionEvaluacionRepositoryPort;
import com.akine_api.domain.model.SesionEvaluacion;
import com.akine_api.infrastructure.persistence.mapper.SesionEvaluacionEntityMapper;
import com.akine_api.infrastructure.persistence.repository.SesionEvaluacionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SesionEvaluacionRepositoryAdapter implements SesionEvaluacionRepositoryPort {

    private final SesionEvaluacionJpaRepository repo;
    private final SesionEvaluacionEntityMapper mapper;

    public SesionEvaluacionRepositoryAdapter(SesionEvaluacionJpaRepository repo,
                                             SesionEvaluacionEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public SesionEvaluacion save(SesionEvaluacion evaluacion) {
        return mapper.toDomain(repo.save(mapper.toEntity(evaluacion)));
    }

    @Override
    public Optional<SesionEvaluacion> findBySesionId(UUID sesionId) {
        return repo.findBySesionId(sesionId).map(mapper::toDomain);
    }
}
