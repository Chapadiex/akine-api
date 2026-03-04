package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ProfesionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfesionalJpaRepository extends JpaRepository<ProfesionalEntity, UUID> {
    List<ProfesionalEntity> findByConsultorioId(UUID consultorioId);
    Optional<ProfesionalEntity> findByEmail(String email);
    boolean existsByMatriculaAndConsultorioId(String matricula, UUID consultorioId);
    boolean existsByMatriculaAndConsultorioIdAndIdNot(String matricula, UUID consultorioId, UUID id);
    boolean existsByNroDocumento(String nroDocumento);
    boolean existsByNroDocumentoAndIdNot(String nroDocumento, UUID id);
}
