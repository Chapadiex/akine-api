package com.akine_api.infrastructure.persistence.adapter.facturacion;

import com.akine_api.domain.model.facturacion.ConvenioPrestacionValor;
import com.akine_api.domain.repository.facturacion.ConvenioPrestacionValorRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.facturacion.ConvenioPrestacionValorEntity;
import com.akine_api.infrastructure.persistence.mapper.facturacion.ConvenioPrestacionValorEntityMapper;
import com.akine_api.infrastructure.persistence.repository.facturacion.ConvenioPrestacionValorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConvenioPrestacionValorRepositoryAdapter implements ConvenioPrestacionValorRepositoryPort {

    private final ConvenioPrestacionValorJpaRepository jpaRepository;
    private final ConvenioPrestacionValorEntityMapper mapper;

    @Override
    public ConvenioPrestacionValor save(ConvenioPrestacionValor valor) {
        ConvenioPrestacionValorEntity entity = mapper.toEntity(valor);
        ConvenioPrestacionValorEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ConvenioPrestacionValor> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ConvenioPrestacionValor> findByConvenioId(UUID convenioId) {
        return jpaRepository.findByConvenioId(convenioId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
