package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.DisponibilidadProfesionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface DisponibilidadProfesionalJpaRepository extends JpaRepository<DisponibilidadProfesionalEntity, UUID> {
    List<DisponibilidadProfesionalEntity> findByProfesionalIdAndConsultorioId(UUID profesionalId, UUID consultorioId);
    List<DisponibilidadProfesionalEntity> findByProfesionalIdAndConsultorioIdAndDiaSemana(UUID profesionalId, UUID consultorioId, DayOfWeek diaSemana);
    boolean existsByProfesionalIdAndConsultorioIdAndDiaSemana(UUID profesionalId, UUID consultorioId, DayOfWeek diaSemana);
}
