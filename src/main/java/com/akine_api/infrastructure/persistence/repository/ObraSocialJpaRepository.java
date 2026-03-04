package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ObraSocialEntity;
import com.akine_api.domain.model.ObraSocialEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ObraSocialJpaRepository extends JpaRepository<ObraSocialEntity, UUID> {

    Optional<ObraSocialEntity> findByIdAndConsultorioId(UUID id, UUID consultorioId);

    boolean existsByConsultorioIdAndCuit(UUID consultorioId, String cuit);

    boolean existsByConsultorioIdAndCuitAndIdNot(UUID consultorioId, String cuit, UUID id);

    @Query("""
            SELECT os FROM ObraSocialEntity os
            WHERE os.consultorioId = :consultorioId
              AND (:q IS NULL OR :q = '' OR
                   LOWER(os.acronimo) LIKE LOWER(CONCAT('%', :q, '%')) OR
                   LOWER(os.nombreCompleto) LIKE LOWER(CONCAT('%', :q, '%')) OR
                   LOWER(os.cuit) LIKE LOWER(CONCAT('%', :q, '%')))
              AND (:estado IS NULL OR os.estado = :estado)
              AND (:conPlanes IS NULL OR
                   (:conPlanes = TRUE AND EXISTS (SELECT p.id FROM ObraSocialPlanEntity p WHERE p.obraSocial.id = os.id)) OR
                   (:conPlanes = FALSE AND NOT EXISTS (SELECT p.id FROM ObraSocialPlanEntity p WHERE p.obraSocial.id = os.id)))
            """)
    Page<ObraSocialEntity> search(
            @Param("consultorioId") UUID consultorioId,
            @Param("q") String q,
            @Param("estado") ObraSocialEstado estado,
            @Param("conPlanes") Boolean conPlanes,
            Pageable pageable
    );
}

