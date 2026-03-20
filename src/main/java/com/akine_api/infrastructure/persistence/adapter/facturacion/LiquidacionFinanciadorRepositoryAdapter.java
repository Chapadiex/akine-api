package com.akine_api.infrastructure.persistence.adapter.facturacion;

import com.akine_api.domain.model.facturacion.LiquidacionFinanciador;
import com.akine_api.domain.repository.facturacion.LiquidacionFinanciadorRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.facturacion.LiquidacionFinanciadorEntity;
import com.akine_api.infrastructure.persistence.mapper.facturacion.LiquidacionFinanciadorEntityMapper;
import com.akine_api.infrastructure.persistence.repository.facturacion.LiquidacionFinanciadorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LiquidacionFinanciadorRepositoryAdapter implements LiquidacionFinanciadorRepositoryPort {

    private final LiquidacionFinanciadorJpaRepository jpaRepository;
    private final LiquidacionFinanciadorEntityMapper mapper;

    @Override
    public LiquidacionFinanciador save(LiquidacionFinanciador liquidacion) {
        LiquidacionFinanciadorEntity entity = mapper.toEntity(liquidacion);
        LiquidacionFinanciadorEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<LiquidacionFinanciador> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
