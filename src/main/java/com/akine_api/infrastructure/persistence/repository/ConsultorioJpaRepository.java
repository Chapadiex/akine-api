package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ConsultorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsultorioJpaRepository extends JpaRepository<ConsultorioEntity, UUID> {
}
