package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ConfiguracionConsultorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfiguracionConsultorioJpaRepository extends JpaRepository<ConfiguracionConsultorioEntity, UUID> {
    Optional<ConfiguracionConsultorioEntity> findByConsultorioId(UUID consultorioId);
}
