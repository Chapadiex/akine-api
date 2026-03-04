package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ConsultorioEspecialidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultorioEspecialidadJpaRepository extends JpaRepository<ConsultorioEspecialidadEntity, UUID> {
    Optional<ConsultorioEspecialidadEntity> findByConsultorioIdAndEspecialidadId(UUID consultorioId, UUID especialidadId);
    List<ConsultorioEspecialidadEntity> findByConsultorioId(UUID consultorioId);
    List<ConsultorioEspecialidadEntity> findByConsultorioIdAndActivo(UUID consultorioId, boolean activo);

    @Query(value = """
            SELECT ce.*
            FROM consultorio_especialidad ce
            INNER JOIN especialidad_catalogo ec ON ec.id = ce.especialidad_id
            WHERE ce.consultorio_id = :consultorioId
              AND (:includeInactive = true OR ce.activo = true)
              AND LOWER(ec.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
            ORDER BY ec.nombre ASC
            """, nativeQuery = true)
    List<ConsultorioEspecialidadEntity> searchByConsultorioAndNombre(
            @Param("consultorioId") UUID consultorioId,
            @Param("search") String search,
            @Param("includeInactive") boolean includeInactive
    );
}
