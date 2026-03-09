package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.SesionExamenFisicoRepositoryPort;
import com.akine_api.domain.model.SesionExamenFisico;
import com.akine_api.infrastructure.persistence.mapper.SesionExamenFisicoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.SesionExamenFisicoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SesionExamenFisicoRepositoryAdapter implements SesionExamenFisicoRepositoryPort {

    private final SesionExamenFisicoJpaRepository repo;
    private final SesionExamenFisicoEntityMapper mapper;

    public SesionExamenFisicoRepositoryAdapter(SesionExamenFisicoJpaRepository repo,
                                               SesionExamenFisicoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public SesionExamenFisico save(SesionExamenFisico examenFisico) {
        return mapper.toDomain(repo.save(mapper.toEntity(examenFisico)));
    }

    @Override
    public Optional<SesionExamenFisico> findBySesionId(UUID sesionId) {
        return repo.findBySesionId(sesionId).map(mapper::toDomain);
    }
}
