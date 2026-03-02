package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ConsultorioDuracionTurnoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsultorioDuracionTurnoJpaRepository extends JpaRepository<ConsultorioDuracionTurnoEntity, UUID> {
    List<ConsultorioDuracionTurnoEntity> findByConsultorioId(UUID consultorioId);
    boolean existsByConsultorioIdAndMinutos(UUID consultorioId, int minutos);
    void deleteByConsultorioIdAndMinutos(UUID consultorioId, int minutos);
}
