package com.akine_api.infrastructure.persistence.adapter.facturacion;

import com.akine_api.domain.model.facturacion.ConvenioFinanciador;
import com.akine_api.domain.repository.facturacion.ConvenioFinanciadorRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.facturacion.ConvenioFinanciadorEntity;
import com.akine_api.infrastructure.persistence.mapper.facturacion.ConvenioFinanciadorEntityMapper;
import com.akine_api.infrastructure.persistence.repository.facturacion.ConvenioFinanciadorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConvenioFinanciadorRepositoryAdapter implements ConvenioFinanciadorRepositoryPort {

    private final ConvenioFinanciadorJpaRepository jpaRepository;
    private final ConvenioFinanciadorEntityMapper mapper;

    @Override
    public ConvenioFinanciador save(ConvenioFinanciador convenio) {
        ConvenioFinanciadorEntity entity = mapper.toEntity(convenio);
        ConvenioFinanciadorEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ConvenioFinanciador> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ConvenioFinanciador> findByFinanciadorId(UUID financiadorId) {
        return jpaRepository.findByFinanciadorId(financiadorId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConvenioFinanciador> findByConsultorioId(UUID consultorioId) {
        return jpaRepository.findByConsultorioId(consultorioId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ConvenioFinanciador> findVigenteByFinanciadorPlanConsultorio(
            UUID financiadorId, UUID planId, UUID consultorioId, LocalDate fecha) {
        return jpaRepository.findVigenteByFinanciadorPlanConsultorio(financiadorId, planId, consultorioId, fecha)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsSolapamiento(UUID financiadorId, UUID planId, UUID consultorioId,
                                      LocalDate vigenciaDesde, LocalDate vigenciaHasta, UUID excludeId) {
        return jpaRepository.existsSolapamiento(financiadorId, planId, consultorioId,
                vigenciaDesde, vigenciaHasta, excludeId);
    }
}
