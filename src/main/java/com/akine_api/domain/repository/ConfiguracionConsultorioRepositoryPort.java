package com.akine_api.domain.repository;

import com.akine_api.domain.model.ConfiguracionConsultorio;

import java.util.Optional;
import java.util.UUID;

public interface ConfiguracionConsultorioRepositoryPort {
    ConfiguracionConsultorio save(ConfiguracionConsultorio config);
    Optional<ConfiguracionConsultorio> findByConsultorioId(UUID consultorioId);
}
