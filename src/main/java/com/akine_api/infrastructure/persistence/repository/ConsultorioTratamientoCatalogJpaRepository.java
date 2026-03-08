package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ConsultorioTratamientoCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsultorioTratamientoCatalogJpaRepository extends JpaRepository<ConsultorioTratamientoCatalogEntity, UUID> {
}
