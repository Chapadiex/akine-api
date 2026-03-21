package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ConsultorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ConsultorioJpaRepository extends JpaRepository<ConsultorioEntity, UUID> {

    @Query(value = "SELECT NEXTVAL('seq_consultorio_nro')", nativeQuery = true)
    long nextNroConsultorioSequence();

    long countByEmpresaId(UUID empresaId);
}
