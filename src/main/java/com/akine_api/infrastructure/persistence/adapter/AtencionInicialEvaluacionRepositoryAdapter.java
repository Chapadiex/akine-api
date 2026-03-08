package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.AtencionInicialEvaluacionRepositoryPort;
import com.akine_api.domain.model.AtencionInicialEvaluacion;
import com.akine_api.infrastructure.persistence.mapper.AtencionInicialEvaluacionEntityMapper;
import com.akine_api.infrastructure.persistence.repository.AtencionInicialEvaluacionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AtencionInicialEvaluacionRepositoryAdapter implements AtencionInicialEvaluacionRepositoryPort {

    private final AtencionInicialEvaluacionJpaRepository repo;
    private final AtencionInicialEvaluacionEntityMapper mapper;

    public AtencionInicialEvaluacionRepositoryAdapter(AtencionInicialEvaluacionJpaRepository repo,
                                                      AtencionInicialEvaluacionEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public AtencionInicialEvaluacion save(AtencionInicialEvaluacion evaluacion) {
        return mapper.toDomain(repo.save(mapper.toEntity(evaluacion)));
    }

    @Override
    public Optional<AtencionInicialEvaluacion> findByAtencionInicialId(UUID atencionInicialId) {
        return repo.findByAtencionInicialId(atencionInicialId).map(mapper::toDomain);
    }
}
