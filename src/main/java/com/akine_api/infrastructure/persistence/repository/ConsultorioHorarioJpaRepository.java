package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ConsultorioHorarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultorioHorarioJpaRepository extends JpaRepository<ConsultorioHorarioEntity, UUID> {
    List<ConsultorioHorarioEntity> findByConsultorioId(UUID consultorioId);
    Optional<ConsultorioHorarioEntity> findByConsultorioIdAndDiaSemana(UUID consultorioId, DayOfWeek diaSemana);
}
