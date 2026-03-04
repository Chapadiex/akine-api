package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.EspecialidadCatalogoRepositoryPort;
import com.akine_api.domain.model.EspecialidadCatalogo;
import com.akine_api.infrastructure.persistence.mapper.EspecialidadCatalogoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.EspecialidadCatalogoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EspecialidadCatalogoRepositoryAdapter implements EspecialidadCatalogoRepositoryPort {

    private final EspecialidadCatalogoJpaRepository repo;
    private final EspecialidadCatalogoEntityMapper mapper;

    public EspecialidadCatalogoRepositoryAdapter(EspecialidadCatalogoJpaRepository repo,
                                                 EspecialidadCatalogoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public EspecialidadCatalogo save(EspecialidadCatalogo especialidadCatalogo) {
        return mapper.toDomain(repo.save(mapper.toEntity(especialidadCatalogo)));
    }

    @Override
    public Optional<EspecialidadCatalogo> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<EspecialidadCatalogo> findBySlug(String slug) {
        return repo.findBySlug(slug).map(mapper::toDomain);
    }

    @Override
    public List<EspecialidadCatalogo> findAll() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<EspecialidadCatalogo> findByIds(List<UUID> ids) {
        return repo.findAllById(ids).stream().map(mapper::toDomain).toList();
    }
}
