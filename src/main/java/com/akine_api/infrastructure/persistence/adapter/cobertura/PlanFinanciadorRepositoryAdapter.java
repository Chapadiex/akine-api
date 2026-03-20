package com.akine_api.infrastructure.persistence.adapter.cobertura;

import com.akine_api.domain.model.cobertura.PlanFinanciador;
import com.akine_api.domain.repository.cobertura.PlanFinanciadorRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.cobertura.PlanFinanciadorEntity;
import com.akine_api.infrastructure.persistence.mapper.cobertura.PlanFinanciadorEntityMapper;
import com.akine_api.infrastructure.persistence.repository.cobertura.PlanFinanciadorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlanFinanciadorRepositoryAdapter implements PlanFinanciadorRepositoryPort {

    private final PlanFinanciadorJpaRepository jpaRepository;
    private final PlanFinanciadorEntityMapper mapper;

    @Override
    public PlanFinanciador save(PlanFinanciador planFinanciador) {
        PlanFinanciadorEntity entity = mapper.toEntity(planFinanciador);
        PlanFinanciadorEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PlanFinanciador> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PlanFinanciador update(UUID id, PlanFinanciador planFinanciador) {
        PlanFinanciadorEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PlanFinanciador no encontrado: " + id));
        entity.setNombrePlan(planFinanciador.getNombrePlan());
        entity.setTipoPlan(planFinanciador.getTipoPlan());
        entity.setRequiereAutorizacionDefault(planFinanciador.getRequiereAutorizacionDefault());
        entity.setVigenciaDesde(planFinanciador.getVigenciaDesde());
        entity.setVigenciaHasta(planFinanciador.getVigenciaHasta());
        entity.setActivo(planFinanciador.getActivo());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean existsWithOverlappingVigencia(String nombrePlan, UUID financiadorId, LocalDate vigenciaDesde, LocalDate vigenciaHasta) {
        return jpaRepository.existsWithOverlappingVigencia(nombrePlan, financiadorId, vigenciaDesde, vigenciaHasta);
    }

    @Override
    public boolean existsWithOverlappingVigenciaExcludingId(String nombrePlan, UUID financiadorId, LocalDate vigenciaDesde, LocalDate vigenciaHasta, UUID excludeId) {
        return jpaRepository.existsWithOverlappingVigenciaExcludingId(nombrePlan, financiadorId, vigenciaDesde, vigenciaHasta, excludeId);
    }

    @Override
    public List<PlanFinanciador> findByFinanciadorId(UUID financiadorId) {
        return jpaRepository.findByFinanciadorId(financiadorId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
