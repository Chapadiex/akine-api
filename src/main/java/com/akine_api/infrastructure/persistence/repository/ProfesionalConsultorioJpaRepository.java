package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ProfesionalConsultorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfesionalConsultorioJpaRepository extends JpaRepository<ProfesionalConsultorioEntity, UUID> {
    List<ProfesionalConsultorioEntity> findByConsultorioId(UUID consultorioId);
    List<ProfesionalConsultorioEntity> findByProfesionalId(UUID profesionalId);
    Optional<ProfesionalConsultorioEntity> findByProfesionalIdAndConsultorioId(UUID profesionalId, UUID consultorioId);
    boolean existsByProfesionalIdAndConsultorioId(UUID profesionalId, UUID consultorioId);
}
