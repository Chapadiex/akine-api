package com.akine_api.application.port.output;

import com.akine_api.domain.model.ConsultorioHorario;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface ConsultorioHorarioRepositoryPort {
    List<ConsultorioHorario> findByConsultorioId(UUID consultorioId);
    List<ConsultorioHorario> findByConsultorioIdAndDiaSemana(UUID consultorioId, DayOfWeek diaSemana);
    ConsultorioHorario save(ConsultorioHorario horario);
    void deleteById(UUID id);
    void deleteByConsultorioIdAndDiaSemana(UUID consultorioId, DayOfWeek diaSemana);
}
