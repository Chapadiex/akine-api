package com.akine_api.application.port.output;

import com.akine_api.domain.model.ConsultorioDuracionTurno;

import java.util.List;
import java.util.UUID;

public interface ConsultorioDuracionTurnoRepositoryPort {
    List<ConsultorioDuracionTurno> findByConsultorioId(UUID consultorioId);
    boolean existsByConsultorioIdAndMinutos(UUID consultorioId, int minutos);
    ConsultorioDuracionTurno save(ConsultorioDuracionTurno duracion);
    void deleteByConsultorioIdAndMinutos(UUID consultorioId, int minutos);
}
