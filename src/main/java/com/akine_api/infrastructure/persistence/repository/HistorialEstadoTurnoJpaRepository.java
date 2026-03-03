package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.HistorialEstadoTurnoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistorialEstadoTurnoJpaRepository extends JpaRepository<HistorialEstadoTurnoEntity, UUID> {

    List<HistorialEstadoTurnoEntity> findByTurnoIdOrderByCreatedAtAsc(UUID turnoId);
}
