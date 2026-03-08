package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.PlanTratamientoDetalleRepositoryPort;
import com.akine_api.domain.model.PlanTratamientoDetalle;
import com.akine_api.infrastructure.persistence.mapper.PlanTratamientoDetalleEntityMapper;
import com.akine_api.infrastructure.persistence.repository.PlanTratamientoDetalleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PlanTratamientoDetalleRepositoryAdapter implements PlanTratamientoDetalleRepositoryPort {

    private final PlanTratamientoDetalleJpaRepository repo;
    private final PlanTratamientoDetalleEntityMapper mapper;

    public PlanTratamientoDetalleRepositoryAdapter(PlanTratamientoDetalleJpaRepository repo,
                                                   PlanTratamientoDetalleEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<PlanTratamientoDetalle> saveAll(List<PlanTratamientoDetalle> detalles) {
        return repo.saveAll(detalles.stream().map(mapper::toEntity).toList()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<PlanTratamientoDetalle> findByPlanTerapeuticoId(UUID planTerapeuticoId) {
        return repo.findByPlanTerapeuticoIdOrderByOrderIndexAsc(planTerapeuticoId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
