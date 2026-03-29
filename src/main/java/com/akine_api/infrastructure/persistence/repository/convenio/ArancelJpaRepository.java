package com.akine_api.infrastructure.persistence.repository.convenio;

import com.akine_api.infrastructure.persistence.entity.convenio.ArancelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ArancelJpaRepository extends JpaRepository<ArancelEntity, UUID> {
    List<ArancelEntity> findByConvenioVersionId(UUID versionId);

    @Query("SELECT a FROM ArancelEntity a WHERE a.convenioVersion.id = :versionId AND a.activo = true AND a.vigenciaDesde <= :fecha AND (a.vigenciaHasta IS NULL OR a.vigenciaHasta >= :fecha)")
    List<ArancelEntity> findVigentesByVersion(@Param("versionId") UUID versionId, @Param("fecha") LocalDate fecha);
}
