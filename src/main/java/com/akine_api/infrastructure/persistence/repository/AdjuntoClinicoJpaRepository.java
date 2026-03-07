package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.AdjuntoClinicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AdjuntoClinicoJpaRepository extends JpaRepository<AdjuntoClinicoEntity, UUID> {
    List<AdjuntoClinicoEntity> findBySesionIdOrderByCreatedAtAsc(UUID sesionId);
    List<AdjuntoClinicoEntity> findBySesionIdInOrderByCreatedAtAsc(List<UUID> sesionIds);
}
