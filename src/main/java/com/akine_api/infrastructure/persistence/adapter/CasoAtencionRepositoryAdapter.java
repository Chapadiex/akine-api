package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.CasoAtencionRepositoryPort;
import com.akine_api.domain.model.CasoAtencion;
import com.akine_api.domain.model.CasoAtencionEstado;
import com.akine_api.infrastructure.persistence.mapper.CasoAtencionEntityMapper;
import com.akine_api.infrastructure.persistence.repository.CasoAtencionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CasoAtencionRepositoryAdapter implements CasoAtencionRepositoryPort {

    private final CasoAtencionJpaRepository repo;
    private final CasoAtencionEntityMapper mapper;

    public CasoAtencionRepositoryAdapter(CasoAtencionJpaRepository repo, CasoAtencionEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public CasoAtencion save(CasoAtencion caso) {
        return mapper.toDomain(repo.save(mapper.toEntity(caso)));
    }

    @Override
    public Optional<CasoAtencion> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<CasoAtencion> findByIdAndConsultorioId(UUID id, UUID consultorioId) {
        return repo.findByIdAndConsultorioId(id, consultorioId).map(mapper::toDomain);
    }

    @Override
    public List<CasoAtencion> findByLegajoId(UUID legajoId) {
        return repo.findByLegajoIdOrderByFechaAperturaDesc(legajoId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<CasoAtencion> findByPacienteIdAndConsultorioId(UUID pacienteId, UUID consultorioId) {
        return repo.findByPacienteIdAndConsultorioIdOrderByFechaAperturaDesc(pacienteId, consultorioId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<CasoAtencion> findByPacienteIdAndConsultorioIdAndEstadoIn(UUID pacienteId,
                                                                           UUID consultorioId,
                                                                           List<CasoAtencionEstado> estados) {
        return repo.findByPacienteIdAndConsultorioIdAndEstadoInOrderByFechaAperturaDesc(pacienteId, consultorioId, estados)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return repo.existsById(id);
    }
}
