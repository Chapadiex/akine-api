package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.PlanTratamientoDetalleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlanTratamientoDetalleJpaRepository extends JpaRepository<PlanTratamientoDetalleEntity, UUID> {
    List<PlanTratamientoDetalleEntity> findByPlanTerapeuticoIdOrderByOrderIndexAsc(UUID planTerapeuticoId);
}
