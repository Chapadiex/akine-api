package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ConsultorioFeriadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ConsultorioFeriadoJpaRepository extends JpaRepository<ConsultorioFeriadoEntity, UUID> {

    boolean existsByConsultorioIdAndFecha(UUID consultorioId, LocalDate fecha);

    List<ConsultorioFeriadoEntity> findByConsultorioIdAndFechaBetweenOrderByFechaAsc(
            UUID consultorioId, LocalDate from, LocalDate to);
}
