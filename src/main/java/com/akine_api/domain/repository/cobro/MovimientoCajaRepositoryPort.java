package com.akine_api.domain.repository.cobro;

import com.akine_api.domain.model.cobro.MovimientoCaja;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovimientoCajaRepositoryPort {
    MovimientoCaja save(MovimientoCaja movimiento);
    Optional<MovimientoCaja> findById(UUID id);
    List<MovimientoCaja> findByCajaDiariaId(UUID cajaDiariaId);
}
