package com.akine_api.infrastructure.persistence.adapter.cobro;

import com.akine_api.domain.model.cobro.CobroPaciente;
import com.akine_api.domain.repository.cobro.CobroPacienteRepositoryPort;
import com.akine_api.infrastructure.persistence.mapper.cobro.CobroPacienteEntityMapper;
import com.akine_api.infrastructure.persistence.repository.cobro.CobroPacienteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CobroPacienteRepositoryAdapter implements CobroPacienteRepositoryPort {

    private final CobroPacienteJpaRepository repo;
    private final CobroPacienteEntityMapper mapper;

    public CobroPacienteRepositoryAdapter(CobroPacienteJpaRepository repo, CobroPacienteEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public CobroPaciente save(CobroPaciente cobro) {
        return mapper.toDomain(repo.save(mapper.toEntity(cobro)));
    }

    @Override
    public Optional<CobroPaciente> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CobroPaciente> findByCajaDiariaId(UUID cajaDiariaId) {
        return repo.findByCajaDiariaId(cajaDiariaId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<CobroPaciente> findByPacienteId(UUID pacienteId, UUID consultorioId) {
        return repo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<CobroPaciente> findBySesionId(UUID sesionId) {
        return repo.findBySesionId(sesionId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
