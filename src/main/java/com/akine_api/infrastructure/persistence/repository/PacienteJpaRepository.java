package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PacienteJpaRepository extends JpaRepository<PacienteEntity, UUID> {
    Optional<PacienteEntity> findByDni(String dni);
    Optional<PacienteEntity> findByUserId(UUID userId);
    List<PacienteEntity> findTop20ByApellidoContainingIgnoreCaseOrNombreContainingIgnoreCase(String apellido, String nombre);
}
