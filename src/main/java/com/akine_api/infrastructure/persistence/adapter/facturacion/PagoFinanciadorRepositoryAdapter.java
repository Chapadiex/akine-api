package com.akine_api.infrastructure.persistence.adapter.facturacion;

import com.akine_api.domain.model.facturacion.PagoFinanciador;
import com.akine_api.domain.repository.facturacion.PagoFinanciadorRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.facturacion.PagoFinanciadorEntity;
import com.akine_api.infrastructure.persistence.mapper.facturacion.PagoFinanciadorEntityMapper;
import com.akine_api.infrastructure.persistence.repository.facturacion.PagoFinanciadorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PagoFinanciadorRepositoryAdapter implements PagoFinanciadorRepositoryPort {

    private final PagoFinanciadorJpaRepository jpaRepository;
    private final PagoFinanciadorEntityMapper mapper;

    @Override
    public PagoFinanciador save(PagoFinanciador pago) {
        PagoFinanciadorEntity entity = mapper.toEntity(pago);
        PagoFinanciadorEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<PagoFinanciador> findByFinanciadorId(UUID financiadorId) {
        // En una implementación real se agregaría el método al JpaRepository
        return jpaRepository.findAll().stream()
                .filter(p -> p.getFinanciador().getId().equals(financiadorId))
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
