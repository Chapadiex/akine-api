package com.akine_api.infrastructure.persistence.adapter.facturacion;

import com.akine_api.domain.model.facturacion.LotePresentacion;
import com.akine_api.domain.repository.facturacion.LotePresentacionRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.facturacion.LotePresentacionEntity;
import com.akine_api.infrastructure.persistence.mapper.facturacion.LotePresentacionEntityMapper;
import com.akine_api.infrastructure.persistence.repository.facturacion.LotePresentacionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LotePresentacionRepositoryAdapter implements LotePresentacionRepositoryPort {

    private final LotePresentacionJpaRepository jpaRepository;
    private final LotePresentacionEntityMapper mapper;

    @Override
    public LotePresentacion save(LotePresentacion lote) {
        LotePresentacionEntity entity = mapper.toEntity(lote);
        LotePresentacionEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<LotePresentacion> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<LotePresentacion> findByFinanciadorId(UUID financiadorId) {
        return jpaRepository.findByFinanciadorId(financiadorId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
