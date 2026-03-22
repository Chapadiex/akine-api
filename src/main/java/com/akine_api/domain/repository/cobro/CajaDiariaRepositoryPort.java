package com.akine_api.domain.repository.cobro;

import com.akine_api.domain.model.cobro.CajaDiaria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CajaDiariaRepositoryPort {
    CajaDiaria save(CajaDiaria caja);
    Optional<CajaDiaria> findById(UUID id);
    Optional<CajaDiaria> findAbiertaByConsultorioIdAndFechaAndTurno(UUID consultorioId, LocalDate fecha, String turnoCaja);
    List<CajaDiaria> findByConsultorioIdAndFecha(UUID consultorioId, LocalDate fecha);
    boolean existsAbierta(UUID consultorioId, LocalDate fecha, String turnoCaja);
}
