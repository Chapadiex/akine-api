package com.akine_api.infrastructure.persistence.repository.cobertura;

import com.akine_api.infrastructure.persistence.entity.cobertura.FinanciadorSaludEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinanciadorSaludJpaRepository extends JpaRepository<FinanciadorSaludEntity, UUID> {
    List<FinanciadorSaludEntity> findByConsultorioId(UUID consultorioId);
    boolean existsByNombreIgnoreCaseAndConsultorioId(String nombre, UUID consultorioId);
    boolean existsByNombreIgnoreCaseAndConsultorioIdAndIdNot(String nombre, UUID consultorioId, UUID excludeId);
}
