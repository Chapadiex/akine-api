package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.ProfesionalConsultorioRepositoryPort;
import com.akine_api.domain.model.ProfesionalConsultorio;
import com.akine_api.infrastructure.persistence.mapper.ProfesionalConsultorioEntityMapper;
import com.akine_api.infrastructure.persistence.repository.ProfesionalConsultorioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProfesionalConsultorioRepositoryAdapter implements ProfesionalConsultorioRepositoryPort {

    private final ProfesionalConsultorioJpaRepository repo;
    private final ProfesionalConsultorioEntityMapper mapper;

    public ProfesionalConsultorioRepositoryAdapter(ProfesionalConsultorioJpaRepository repo,
                                                   ProfesionalConsultorioEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<ProfesionalConsultorio> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioId(consultorioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ProfesionalConsultorio> findByProfesionalId(UUID profesionalId) {
        return repo.findByProfesionalId(profesionalId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ProfesionalConsultorio> findByProfesionalIdAndConsultorioId(UUID profesionalId, UUID consultorioId) {
        return repo.findByProfesionalIdAndConsultorioId(profesionalId, consultorioId).map(mapper::toDomain);
    }

    @Override
    public ProfesionalConsultorio save(ProfesionalConsultorio pc) {
        return mapper.toDomain(repo.save(mapper.toEntity(pc)));
    }

    @Override
    public boolean existsByProfesionalIdAndConsultorioId(UUID profesionalId, UUID consultorioId) {
        return repo.existsByProfesionalIdAndConsultorioId(profesionalId, consultorioId);
    }
}
