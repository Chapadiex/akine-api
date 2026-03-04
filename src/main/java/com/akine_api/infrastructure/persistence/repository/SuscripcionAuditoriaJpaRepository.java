package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.SuscripcionAuditoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SuscripcionAuditoriaJpaRepository extends JpaRepository<SuscripcionAuditoriaEntity, UUID> {
}
