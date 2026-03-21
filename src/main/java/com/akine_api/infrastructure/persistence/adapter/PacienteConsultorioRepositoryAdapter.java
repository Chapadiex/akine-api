package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.PacienteConsultorioRepositoryPort;
import com.akine_api.domain.model.PacienteConsultorio;
import com.akine_api.infrastructure.persistence.mapper.PacienteConsultorioEntityMapper;
import com.akine_api.infrastructure.persistence.repository.PacienteConsultorioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PacienteConsultorioRepositoryAdapter implements PacienteConsultorioRepositoryPort {

    private final PacienteConsultorioJpaRepository jpaRepository;
    private final PacienteConsultorioEntityMapper mapper;

    public PacienteConsultorioRepositoryAdapter(PacienteConsultorioJpaRepository jpaRepository,
                                                PacienteConsultorioEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public PacienteConsultorio save(PacienteConsultorio pacienteConsultorio) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(pacienteConsultorio)));
    }

    @Override
    public boolean existsByPacienteIdAndConsultorioId(UUID pacienteId, UUID consultorioId) {
        return jpaRepository.existsByPacienteIdAndConsultorioId(pacienteId, consultorioId);
    }

    @Override
    public List<UUID> findPacienteIdsByConsultorioId(UUID consultorioId) {
        return jpaRepository.findPacienteIdsByConsultorioId(consultorioId);
    }

    @Override
    public List<UUID> findPacienteIdsByConsultorioIdAndPacienteIds(UUID consultorioId, List<UUID> pacienteIds) {
        if (pacienteIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findPacienteIdsByConsultorioIdAndPacienteIds(consultorioId, pacienteIds);
    }

    @Override
    public long countByConsultorioId(UUID consultorioId) {
        return jpaRepository.countByConsultorioId(consultorioId);
    }
}
