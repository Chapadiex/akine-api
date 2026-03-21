package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.domain.model.Profesional;
import com.akine_api.infrastructure.persistence.mapper.ProfesionalEntityMapper;
import com.akine_api.infrastructure.persistence.repository.ProfesionalJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProfesionalRepositoryAdapter implements ProfesionalRepositoryPort {

    private final ProfesionalJpaRepository repo;
    private final ProfesionalEntityMapper mapper;

    public ProfesionalRepositoryAdapter(ProfesionalJpaRepository repo, ProfesionalEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Profesional save(Profesional profesional) {
        return mapper.toDomain(repo.save(mapper.toEntity(profesional)));
    }

    @Override
    public Optional<Profesional> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Profesional> findByUserId(UUID userId) {
        return repo.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public Optional<Profesional> findByEmail(String email) {
        return repo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<Profesional> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioId(consultorioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Profesional> findAll() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByMatriculaAndConsultorioId(String matricula, UUID consultorioId) {
        return repo.existsByMatriculaAndConsultorioId(matricula, consultorioId);
    }

    @Override
    public boolean existsByMatriculaAndConsultorioIdAndIdNot(String matricula, UUID consultorioId, UUID excludeId) {
        return repo.existsByMatriculaAndConsultorioIdAndIdNot(matricula, consultorioId, excludeId);
    }

    @Override
    public boolean existsByNroDocumento(String nroDocumento) {
        return repo.existsByNroDocumento(nroDocumento);
    }

    @Override
    public boolean existsByNroDocumentoAndIdNot(String nroDocumento, UUID excludeId) {
        return repo.existsByNroDocumentoAndIdNot(nroDocumento, excludeId);
    }

    @Override
    public long countByConsultorioId(UUID consultorioId) {
        return repo.countByConsultorioId(consultorioId);
    }
}
