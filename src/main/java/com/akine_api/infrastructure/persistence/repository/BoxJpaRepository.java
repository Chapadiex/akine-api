package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.BoxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoxJpaRepository extends JpaRepository<BoxEntity, UUID> {
    List<BoxEntity> findByConsultorioId(UUID consultorioId);
    boolean existsByCodigoAndConsultorioId(String codigo, UUID consultorioId);
}
