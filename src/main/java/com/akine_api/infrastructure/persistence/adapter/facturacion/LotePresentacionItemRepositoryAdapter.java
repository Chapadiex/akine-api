package com.akine_api.infrastructure.persistence.adapter.facturacion;

import com.akine_api.domain.model.facturacion.LotePresentacionItem;
import com.akine_api.domain.repository.facturacion.LotePresentacionItemRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.facturacion.LotePresentacionItemEntity;
import com.akine_api.infrastructure.persistence.mapper.facturacion.LotePresentacionItemEntityMapper;
import com.akine_api.infrastructure.persistence.repository.facturacion.LotePresentacionItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LotePresentacionItemRepositoryAdapter implements LotePresentacionItemRepositoryPort {

    private final LotePresentacionItemJpaRepository jpaRepository;
    private final LotePresentacionItemEntityMapper mapper;

    @Override
    public LotePresentacionItem save(LotePresentacionItem item) {
        LotePresentacionItemEntity entity = mapper.toEntity(item);
        LotePresentacionItemEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<LotePresentacionItem> findByLoteId(UUID loteId) {
        return jpaRepository.findByLoteId(loteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
