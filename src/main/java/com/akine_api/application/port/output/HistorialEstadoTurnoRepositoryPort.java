package com.akine_api.application.port.output;

import com.akine_api.domain.model.HistorialEstadoTurno;

import java.util.List;
import java.util.UUID;

public interface HistorialEstadoTurnoRepositoryPort {
    HistorialEstadoTurno save(HistorialEstadoTurno historial);
    List<HistorialEstadoTurno> findByTurnoId(UUID turnoId);
}
