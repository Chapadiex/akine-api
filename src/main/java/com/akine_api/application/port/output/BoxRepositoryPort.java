package com.akine_api.application.port.output;

import com.akine_api.domain.model.Box;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoxRepositoryPort {
    Box save(Box box);
    Optional<Box> findById(UUID id);
    List<Box> findByConsultorioId(UUID consultorioId);
    boolean existsByCodigoAndConsultorioId(String codigo, UUID consultorioId);
}
