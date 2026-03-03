package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.PacienteConsultorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PacienteConsultorioJpaRepository extends JpaRepository<PacienteConsultorioEntity, UUID> {
    boolean existsByPacienteIdAndConsultorioId(UUID pacienteId, UUID consultorioId);

    @Query("""
            SELECT pc.pacienteId
            FROM PacienteConsultorioEntity pc
            WHERE pc.consultorioId = :consultorioId
            ORDER BY pc.createdAt DESC
            """)
    List<UUID> findPacienteIdsByConsultorioId(@Param("consultorioId") UUID consultorioId);

    @Query("""
            SELECT pc.pacienteId
            FROM PacienteConsultorioEntity pc
            WHERE pc.consultorioId = :consultorioId
            AND pc.pacienteId IN :pacienteIds
            """)
    List<UUID> findPacienteIdsByConsultorioIdAndPacienteIds(@Param("consultorioId") UUID consultorioId,
                                                            @Param("pacienteIds") List<UUID> pacienteIds);
}
