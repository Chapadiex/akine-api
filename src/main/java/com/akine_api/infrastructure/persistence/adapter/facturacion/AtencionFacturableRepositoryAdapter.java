package com.akine_api.infrastructure.persistence.adapter.facturacion;

import com.akine_api.domain.model.facturacion.AtencionFacturable;
import com.akine_api.domain.repository.facturacion.AtencionFacturableRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.facturacion.AtencionFacturableEntity;
import com.akine_api.infrastructure.persistence.mapper.facturacion.AtencionFacturableEntityMapper;
import com.akine_api.infrastructure.persistence.repository.facturacion.AtencionFacturableJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AtencionFacturableRepositoryAdapter implements AtencionFacturableRepositoryPort {

    private final AtencionFacturableJpaRepository jpaRepository;
    private final AtencionFacturableEntityMapper mapper;

    @Override
    public AtencionFacturable save(AtencionFacturable atencionFacturable) {
        AtencionFacturableEntity entity = mapper.toEntity(atencionFacturable);
        AtencionFacturableEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AtencionFacturable> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AtencionFacturable> findByAtencionId(UUID atencionId) {
        return jpaRepository.findByAtencionId(atencionId).map(mapper::toDomain);
    }

    @Override
    public List<AtencionFacturable> findByPacienteId(UUID pacienteId) {
        return jpaRepository.findByPacienteId(pacienteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
