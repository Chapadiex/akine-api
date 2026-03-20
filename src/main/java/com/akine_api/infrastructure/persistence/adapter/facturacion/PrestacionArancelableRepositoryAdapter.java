package com.akine_api.infrastructure.persistence.adapter.facturacion;

import com.akine_api.domain.model.facturacion.PrestacionArancelable;
import com.akine_api.domain.repository.facturacion.PrestacionArancelableRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.facturacion.PrestacionArancelableEntity;
import com.akine_api.infrastructure.persistence.mapper.facturacion.PrestacionArancelableEntityMapper;
import com.akine_api.infrastructure.persistence.repository.facturacion.PrestacionArancelableJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PrestacionArancelableRepositoryAdapter implements PrestacionArancelableRepositoryPort {

    private final PrestacionArancelableJpaRepository jpaRepository;
    private final PrestacionArancelableEntityMapper mapper;

    @Override
    public PrestacionArancelable save(PrestacionArancelable prestacion) {
        PrestacionArancelableEntity entity = mapper.toEntity(prestacion);
        PrestacionArancelableEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<PrestacionArancelable> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<PrestacionArancelable> findByCodigoInterno(String codigoInterno) {
        return jpaRepository.findByCodigoInterno(codigoInterno).map(mapper::toDomain);
    }

    @Override
    public List<PrestacionArancelable> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
