package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.domain.model.ConfiguracionConsultorio;
import com.akine_api.domain.repository.ConfiguracionConsultorioRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.ConfiguracionConsultorioEntity;
import com.akine_api.infrastructure.persistence.mapper.ConfiguracionConsultorioEntityMapper;
import com.akine_api.infrastructure.persistence.repository.ConfiguracionConsultorioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConfiguracionConsultorioRepositoryAdapter implements ConfiguracionConsultorioRepositoryPort {

    private final ConfiguracionConsultorioJpaRepository jpaRepository;
    private final ConfiguracionConsultorioEntityMapper mapper;

    @Override
    public ConfiguracionConsultorio save(ConfiguracionConsultorio config) {
        ConfiguracionConsultorioEntity entity = mapper.toEntity(config);
        ConfiguracionConsultorioEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ConfiguracionConsultorio> findByConsultorioId(UUID consultorioId) {
        return jpaRepository.findByConsultorioId(consultorioId).map(mapper::toDomain);
    }
}
