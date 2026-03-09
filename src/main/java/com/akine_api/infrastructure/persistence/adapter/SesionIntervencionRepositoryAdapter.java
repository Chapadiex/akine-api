package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.SesionIntervencionRepositoryPort;
import com.akine_api.domain.model.SesionIntervencion;
import com.akine_api.infrastructure.persistence.mapper.SesionIntervencionEntityMapper;
import com.akine_api.infrastructure.persistence.repository.SesionIntervencionJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class SesionIntervencionRepositoryAdapter implements SesionIntervencionRepositoryPort {

    private final SesionIntervencionJpaRepository repo;
    private final SesionIntervencionEntityMapper mapper;

    public SesionIntervencionRepositoryAdapter(SesionIntervencionJpaRepository repo,
                                               SesionIntervencionEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public SesionIntervencion save(SesionIntervencion intervencion) {
        return mapper.toDomain(repo.save(mapper.toEntity(intervencion)));
    }

    @Override
    public List<SesionIntervencion> saveAll(List<SesionIntervencion> intervenciones) {
        return mapper.toDomainList(repo.saveAll(mapper.toEntityList(intervenciones)));
    }

    @Override
    public List<SesionIntervencion> findBySesionId(UUID sesionId) {
        return mapper.toDomainList(repo.findBySesionId(sesionId));
    }

    @Override
    @Transactional
    public void deleteBySesionId(UUID sesionId) {
        repo.deleteBySesionId(sesionId);
    }
}
