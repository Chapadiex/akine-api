package com.akine_api.application.port.output;

import com.akine_api.domain.model.Turno;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TurnoRepositoryPort {
    Optional<Turno> findById(UUID id);
    List<Turno> findByConsultorioIdAndRange(UUID consultorioId, LocalDateTime from, LocalDateTime to);
    List<Turno> findByProfesionalIdAndRange(UUID profesionalId, LocalDateTime from, LocalDateTime to);
    Turno save(Turno turno);
}
