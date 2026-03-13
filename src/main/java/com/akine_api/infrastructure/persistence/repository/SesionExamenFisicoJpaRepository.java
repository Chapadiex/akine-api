package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.SesionExamenFisicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SesionExamenFisicoJpaRepository extends JpaRepository<SesionExamenFisicoEntity, UUID> {
    Optional<SesionExamenFisicoEntity> findBySesionId(UUID sesionId);
}
