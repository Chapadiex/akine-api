package com.akine_api.application.port.output;

import com.akine_api.domain.model.PlanTratamientoDetalle;

import java.util.List;
import java.util.UUID;

public interface PlanTratamientoDetalleRepositoryPort {
    List<PlanTratamientoDetalle> saveAll(List<PlanTratamientoDetalle> detalles);
    List<PlanTratamientoDetalle> findByPlanTerapeuticoId(UUID planTerapeuticoId);
}
