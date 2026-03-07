package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.SesionClinicaRepositoryPort;
import com.akine_api.domain.model.SesionClinica;
import com.akine_api.infrastructure.persistence.mapper.SesionClinicaEntityMapper;
import com.akine_api.infrastructure.persistence.repository.SesionClinicaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SesionClinicaRepositoryAdapter implements SesionClinicaRepositoryPort {

    private final SesionClinicaJpaRepository repo;
    private final SesionClinicaEntityMapper mapper;

    public SesionClinicaRepositoryAdapter(SesionClinicaJpaRepository repo,
                                          SesionClinicaEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public SesionClinica save(SesionClinica sesionClinica) {
        return mapper.toDomain(repo.save(mapper.toEntity(sesionClinica)));
    }

    @Override
    public Optional<SesionClinica> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<SesionClinica> findByTurnoId(UUID turnoId) {
        return repo.findByTurnoId(turnoId).map(mapper::toDomain);
    }

    @Override
    public List<SesionClinica> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioIdOrderByFechaAtencionDesc(consultorioId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<SesionClinica> findByPacienteIdAndConsultorioId(UUID pacienteId, UUID consultorioId) {
        return repo.findByPacienteIdAndConsultorioIdOrderByFechaAtencionDesc(pacienteId, consultorioId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
