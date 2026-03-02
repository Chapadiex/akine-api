package com.akine_api.application.port.output;

import com.akine_api.domain.model.DisponibilidadProfesional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisponibilidadProfesionalRepositoryPort {
    List<DisponibilidadProfesional> findByProfesionalIdAndConsultorioId(UUID profesionalId, UUID consultorioId);
    List<DisponibilidadProfesional> findByProfesionalIdAndConsultorioIdAndDiaSemana(UUID profesionalId, UUID consultorioId, DayOfWeek diaSemana);
    Optional<DisponibilidadProfesional> findById(UUID id);
    boolean existsByProfesionalIdAndConsultorioIdAndDiaSemana(UUID profesionalId, UUID consultorioId, DayOfWeek diaSemana);
    DisponibilidadProfesional save(DisponibilidadProfesional disponibilidad);
    void deleteById(UUID id);
}
