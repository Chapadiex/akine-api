package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.EmpleadoRepositoryPort;
import com.akine_api.domain.model.Empleado;
import com.akine_api.infrastructure.persistence.mapper.EmpleadoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.EmpleadoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EmpleadoRepositoryAdapter implements EmpleadoRepositoryPort {

    private final EmpleadoJpaRepository repo;
    private final EmpleadoEntityMapper mapper;

    public EmpleadoRepositoryAdapter(EmpleadoJpaRepository repo, EmpleadoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Empleado save(Empleado empleado) {
        return mapper.toDomain(repo.save(mapper.toEntity(empleado)));
    }

    @Override
    public Optional<Empleado> findByConsultorioIdAndId(UUID consultorioId, UUID id) {
        return repo.findByConsultorioIdAndId(consultorioId, id).map(mapper::toDomain);
    }

    @Override
    public Optional<Empleado> findByUserId(UUID userId) {
        return repo.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public List<Empleado> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioId(consultorioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByConsultorioIdAndEmail(UUID consultorioId, String email) {
        return repo.existsByConsultorioIdAndEmail(consultorioId, email);
    }

    @Override
    public boolean existsByConsultorioIdAndEmailAndIdNot(UUID consultorioId, String email, UUID id) {
        return repo.existsByConsultorioIdAndEmailAndIdNot(consultorioId, email, id);
    }

    @Override
    public boolean existsByConsultorioIdAndDniAndIdNot(UUID consultorioId, String dni, UUID id) {
        return repo.existsByConsultorioIdAndDniAndIdNot(consultorioId, dni, id);
    }
}
