package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.BoxRepositoryPort;
import com.akine_api.domain.model.Box;
import com.akine_api.infrastructure.persistence.mapper.BoxEntityMapper;
import com.akine_api.infrastructure.persistence.repository.BoxJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BoxRepositoryAdapter implements BoxRepositoryPort {

    private final BoxJpaRepository repo;
    private final BoxEntityMapper mapper;

    public BoxRepositoryAdapter(BoxJpaRepository repo, BoxEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Box save(Box box) {
        return mapper.toDomain(repo.save(mapper.toEntity(box)));
    }

    @Override
    public Optional<Box> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Box> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioId(consultorioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByCodigoAndConsultorioId(String codigo, UUID consultorioId) {
        return repo.existsByCodigoAndConsultorioId(codigo, consultorioId);
    }
}
