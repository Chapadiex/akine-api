package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.ConsultorioEspecialidadRepositoryPort;
import com.akine_api.domain.model.ConsultorioEspecialidad;
import com.akine_api.infrastructure.persistence.mapper.ConsultorioEspecialidadEntityMapper;
import com.akine_api.infrastructure.persistence.repository.ConsultorioEspecialidadJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConsultorioEspecialidadRepositoryAdapter implements ConsultorioEspecialidadRepositoryPort {

    private final ConsultorioEspecialidadJpaRepository repo;
    private final ConsultorioEspecialidadEntityMapper mapper;

    public ConsultorioEspecialidadRepositoryAdapter(ConsultorioEspecialidadJpaRepository repo,
                                                    ConsultorioEspecialidadEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public ConsultorioEspecialidad save(ConsultorioEspecialidad consultorioEspecialidad) {
        return mapper.toDomain(repo.save(mapper.toEntity(consultorioEspecialidad)));
    }

    @Override
    public Optional<ConsultorioEspecialidad> findByConsultorioIdAndEspecialidadId(UUID consultorioId, UUID especialidadId) {
        return repo.findByConsultorioIdAndEspecialidadId(consultorioId, especialidadId).map(mapper::toDomain);
    }

    @Override
    public List<ConsultorioEspecialidad> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioId(consultorioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ConsultorioEspecialidad> findByConsultorioIdAndActivo(UUID consultorioId, boolean activo) {
        return repo.findByConsultorioIdAndActivo(consultorioId, activo).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ConsultorioEspecialidad> findByConsultorioIdAndNombreContaining(UUID consultorioId, String normalizedSearch, boolean includeInactive) {
        return repo.searchByConsultorioAndNombre(consultorioId, normalizedSearch, includeInactive).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
