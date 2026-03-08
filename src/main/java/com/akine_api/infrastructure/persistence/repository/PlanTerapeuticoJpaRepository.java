package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.PlanTerapeuticoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlanTerapeuticoJpaRepository extends JpaRepository<PlanTerapeuticoEntity, UUID> {
    Optional<PlanTerapeuticoEntity> findFirstByConsultorioIdAndPacienteIdOrderByCreatedAtDesc(UUID consultorioId, UUID pacienteId);
    Optional<PlanTerapeuticoEntity> findByAtencionInicialId(UUID atencionInicialId);
}
