package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.DiagnosticoClinicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DiagnosticoClinicoJpaRepository extends JpaRepository<DiagnosticoClinicoEntity, UUID> {
    List<DiagnosticoClinicoEntity> findByPacienteIdAndConsultorioIdOrderByFechaInicioDesc(UUID pacienteId, UUID consultorioId);
}
