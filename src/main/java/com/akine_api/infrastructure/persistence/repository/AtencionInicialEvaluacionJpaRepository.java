package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.AtencionInicialEvaluacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AtencionInicialEvaluacionJpaRepository extends JpaRepository<AtencionInicialEvaluacionEntity, UUID> {
    Optional<AtencionInicialEvaluacionEntity> findByAtencionInicialId(UUID atencionInicialId);
}
