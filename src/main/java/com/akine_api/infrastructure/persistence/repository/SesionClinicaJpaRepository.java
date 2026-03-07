package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.SesionClinicaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SesionClinicaJpaRepository extends JpaRepository<SesionClinicaEntity, UUID> {
    Optional<SesionClinicaEntity> findByTurnoId(UUID turnoId);
    List<SesionClinicaEntity> findByConsultorioIdOrderByFechaAtencionDesc(UUID consultorioId);
    List<SesionClinicaEntity> findByPacienteIdAndConsultorioIdOrderByFechaAtencionDesc(UUID pacienteId, UUID consultorioId);
}
