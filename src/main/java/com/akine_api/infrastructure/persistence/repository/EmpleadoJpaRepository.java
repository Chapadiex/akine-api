package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.EmpleadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmpleadoJpaRepository extends JpaRepository<EmpleadoEntity, UUID> {
    List<EmpleadoEntity> findByConsultorioId(UUID consultorioId);
    Optional<EmpleadoEntity> findByConsultorioIdAndId(UUID consultorioId, UUID id);
    Optional<EmpleadoEntity> findByUserId(UUID userId);
    boolean existsByConsultorioIdAndEmail(UUID consultorioId, String email);
    boolean existsByConsultorioIdAndEmailAndIdNot(UUID consultorioId, String email, UUID id);
    boolean existsByConsultorioIdAndDniAndIdNot(UUID consultorioId, String dni, UUID id);
}
