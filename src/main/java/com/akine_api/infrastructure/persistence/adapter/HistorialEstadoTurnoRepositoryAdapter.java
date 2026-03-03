package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.HistorialEstadoTurnoRepositoryPort;
import com.akine_api.domain.model.HistorialEstadoTurno;
import com.akine_api.infrastructure.persistence.mapper.HistorialEstadoTurnoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.HistorialEstadoTurnoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class HistorialEstadoTurnoRepositoryAdapter implements HistorialEstadoTurnoRepositoryPort {

    private final HistorialEstadoTurnoJpaRepository repo;
    private final HistorialEstadoTurnoEntityMapper mapper;

    public HistorialEstadoTurnoRepositoryAdapter(HistorialEstadoTurnoJpaRepository repo,
                                                  HistorialEstadoTurnoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public HistorialEstadoTurno save(HistorialEstadoTurno historial) {
        return mapper.toDomain(repo.save(mapper.toEntity(historial)));
    }

    @Override
    public List<HistorialEstadoTurno> findByTurnoId(UUID turnoId) {
        return repo.findByTurnoIdOrderByCreatedAtAsc(turnoId).stream()
                .map(mapper::toDomain).toList();
    }
}
