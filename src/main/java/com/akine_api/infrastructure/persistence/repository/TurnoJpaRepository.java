package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.TurnoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TurnoJpaRepository extends JpaRepository<TurnoEntity, UUID> {

    List<TurnoEntity> findByConsultorioIdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThan(
            UUID consultorioId, LocalDateTime from, LocalDateTime to);

    List<TurnoEntity> findByProfesionalIdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThan(
            UUID profesionalId, LocalDateTime from, LocalDateTime to);

    List<TurnoEntity> findByPacienteIdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThan(
            UUID pacienteId, LocalDateTime from, LocalDateTime to);
}
