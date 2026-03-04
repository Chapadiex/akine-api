package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.EspecialidadCatalogoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EspecialidadCatalogoJpaRepository extends JpaRepository<EspecialidadCatalogoEntity, UUID> {
    Optional<EspecialidadCatalogoEntity> findBySlug(String slug);
}
