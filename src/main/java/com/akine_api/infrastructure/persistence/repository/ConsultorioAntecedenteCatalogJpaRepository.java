package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ConsultorioAntecedenteCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsultorioAntecedenteCatalogJpaRepository
        extends JpaRepository<ConsultorioAntecedenteCatalogEntity, UUID> {
}

