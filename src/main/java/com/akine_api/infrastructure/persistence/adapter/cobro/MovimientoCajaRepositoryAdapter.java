package com.akine_api.infrastructure.persistence.adapter.cobro;

import com.akine_api.domain.model.cobro.MovimientoCaja;
import com.akine_api.domain.repository.cobro.MovimientoCajaRepositoryPort;
import com.akine_api.infrastructure.persistence.mapper.cobro.MovimientoCajaEntityMapper;
import com.akine_api.infrastructure.persistence.repository.cobro.MovimientoCajaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MovimientoCajaRepositoryAdapter implements MovimientoCajaRepositoryPort {

    private final MovimientoCajaJpaRepository repo;
    private final MovimientoCajaEntityMapper mapper;

    public MovimientoCajaRepositoryAdapter(MovimientoCajaJpaRepository repo, MovimientoCajaEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public MovimientoCaja save(MovimientoCaja movimiento) {
        return mapper.toDomain(repo.save(mapper.toEntity(movimiento)));
    }

    @Override
    public Optional<MovimientoCaja> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<MovimientoCaja> findByCajaDiariaId(UUID cajaDiariaId) {
        return repo.findByCajaDiariaId(cajaDiariaId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
