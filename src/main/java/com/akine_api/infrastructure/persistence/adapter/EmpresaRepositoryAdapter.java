package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.EmpresaRepositoryPort;
import com.akine_api.domain.model.Empresa;
import com.akine_api.infrastructure.persistence.mapper.EmpresaEntityMapper;
import com.akine_api.infrastructure.persistence.repository.EmpresaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EmpresaRepositoryAdapter implements EmpresaRepositoryPort {

    private final EmpresaJpaRepository repo;
    private final EmpresaEntityMapper mapper;

    public EmpresaRepositoryAdapter(EmpresaJpaRepository repo, EmpresaEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Empresa save(Empresa empresa) {
        return mapper.toDomain(repo.save(mapper.toEntity(empresa)));
    }

    @Override
    public Optional<Empresa> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Empresa> findByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return repo.findAllById(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByCuit(String cuit) {
        return repo.existsByCuit(cuit);
    }
}
