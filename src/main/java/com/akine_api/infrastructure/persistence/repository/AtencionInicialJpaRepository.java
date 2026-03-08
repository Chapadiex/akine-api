package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.AtencionInicialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AtencionInicialJpaRepository extends JpaRepository<AtencionInicialEntity, UUID> {
    Optional<AtencionInicialEntity> findFirstByConsultorioIdAndPacienteIdOrderByFechaHoraDesc(UUID consultorioId, UUID pacienteId);
    List<AtencionInicialEntity> findByConsultorioIdAndPacienteIdOrderByFechaHoraDesc(UUID consultorioId, UUID pacienteId);
}
