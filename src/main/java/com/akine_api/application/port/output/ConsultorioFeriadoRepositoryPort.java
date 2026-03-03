package com.akine_api.application.port.output;

import com.akine_api.domain.model.ConsultorioFeriado;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultorioFeriadoRepositoryPort {
    boolean existsByConsultorioIdAndFecha(UUID consultorioId, LocalDate fecha);
    List<ConsultorioFeriado> findByConsultorioIdAndYear(UUID consultorioId, int year);
    Optional<ConsultorioFeriado> findById(UUID id);
    ConsultorioFeriado save(ConsultorioFeriado feriado);
    void deleteById(UUID id);
}
