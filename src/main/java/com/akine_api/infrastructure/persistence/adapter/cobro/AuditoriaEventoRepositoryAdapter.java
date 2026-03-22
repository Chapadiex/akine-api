package com.akine_api.infrastructure.persistence.adapter.cobro;

import com.akine_api.domain.model.cobro.AuditoriaEvento;
import com.akine_api.domain.repository.cobro.AuditoriaEventoRepositoryPort;
import com.akine_api.infrastructure.persistence.mapper.cobro.AuditoriaEventoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.cobro.AuditoriaEventoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AuditoriaEventoRepositoryAdapter implements AuditoriaEventoRepositoryPort {

    private final AuditoriaEventoJpaRepository repo;
    private final AuditoriaEventoEntityMapper mapper;

    public AuditoriaEventoRepositoryAdapter(AuditoriaEventoJpaRepository repo, AuditoriaEventoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public AuditoriaEvento save(AuditoriaEvento evento) {
        return mapper.toDomain(repo.save(mapper.toEntity(evento)));
    }

    @Override
    public List<AuditoriaEvento> findByEntidadId(String entidad, UUID entidadId) {
        return repo.findByEntidadAndEntidadId(entidad, entidadId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
