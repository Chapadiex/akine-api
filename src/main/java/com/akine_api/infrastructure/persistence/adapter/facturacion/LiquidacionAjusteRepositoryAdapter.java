package com.akine_api.infrastructure.persistence.adapter.facturacion;

import com.akine_api.domain.model.facturacion.LiquidacionAjuste;
import com.akine_api.domain.repository.facturacion.LiquidacionAjusteRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.facturacion.LiquidacionAjusteEntity;
import com.akine_api.infrastructure.persistence.mapper.facturacion.LiquidacionAjusteEntityMapper;
import com.akine_api.infrastructure.persistence.repository.facturacion.LiquidacionAjusteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LiquidacionAjusteRepositoryAdapter implements LiquidacionAjusteRepositoryPort {

    private final LiquidacionAjusteJpaRepository jpaRepository;
    private final LiquidacionAjusteEntityMapper mapper;

    @Override
    public LiquidacionAjuste save(LiquidacionAjuste ajuste) {
        LiquidacionAjusteEntity entity = mapper.toEntity(ajuste);
        LiquidacionAjusteEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<LiquidacionAjuste> findByLiquidacionId(UUID liquidacionId) {
        // En una implementación real se agregaría el método al JpaRepository
        return jpaRepository.findAll().stream()
                .filter(a -> a.getLiquidacion().getId().equals(liquidacionId))
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
