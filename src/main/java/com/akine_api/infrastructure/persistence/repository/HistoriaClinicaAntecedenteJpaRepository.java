package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.HistoriaClinicaAntecedenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoriaClinicaAntecedenteJpaRepository extends JpaRepository<HistoriaClinicaAntecedenteEntity, UUID> {
    List<HistoriaClinicaAntecedenteEntity> findByConsultorioIdAndPacienteIdOrderByCriticalDescUpdatedAtDesc(UUID consultorioId, UUID pacienteId);
    void deleteByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId);
}
