package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.SesionEvaluacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SesionEvaluacionJpaRepository extends JpaRepository<SesionEvaluacionEntity, UUID> {
    Optional<SesionEvaluacionEntity> findBySesionId(UUID sesionId);
}
