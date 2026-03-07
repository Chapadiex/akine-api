package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.AdjuntoClinicoRepositoryPort;
import com.akine_api.domain.model.AdjuntoClinico;
import com.akine_api.infrastructure.persistence.mapper.AdjuntoClinicoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.AdjuntoClinicoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AdjuntoClinicoRepositoryAdapter implements AdjuntoClinicoRepositoryPort {

    private final AdjuntoClinicoJpaRepository repo;
    private final AdjuntoClinicoEntityMapper mapper;

    public AdjuntoClinicoRepositoryAdapter(AdjuntoClinicoJpaRepository repo,
                                           AdjuntoClinicoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public AdjuntoClinico save(AdjuntoClinico adjuntoClinico) {
        return mapper.toDomain(repo.save(mapper.toEntity(adjuntoClinico)));
    }

    @Override
    public Optional<AdjuntoClinico> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AdjuntoClinico> findBySesionId(UUID sesionId) {
        return repo.findBySesionIdOrderByCreatedAtAsc(sesionId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AdjuntoClinico> findBySesionIds(List<UUID> sesionIds) {
        if (sesionIds == null || sesionIds.isEmpty()) {
            return List.of();
        }
        return repo.findBySesionIdInOrderByCreatedAtAsc(sesionIds).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
