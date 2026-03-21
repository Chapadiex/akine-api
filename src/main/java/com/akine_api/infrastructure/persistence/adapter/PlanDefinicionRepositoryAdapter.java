package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.PlanDefinicionRepositoryPort;
import com.akine_api.domain.model.PlanDefinicion;
import com.akine_api.infrastructure.persistence.mapper.PlanDefinicionEntityMapper;
import com.akine_api.infrastructure.persistence.repository.PlanDefinicionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PlanDefinicionRepositoryAdapter implements PlanDefinicionRepositoryPort {

    private final PlanDefinicionJpaRepository planRepo;
    private final PlanDefinicionEntityMapper mapper;

    public PlanDefinicionRepositoryAdapter(PlanDefinicionJpaRepository planRepo,
                                           PlanDefinicionEntityMapper mapper) {
        this.planRepo = planRepo;
        this.mapper = mapper;
    }

    @Override
    public Optional<PlanDefinicion> findByCodigo(String codigo) {
        return planRepo.findById(codigo).map(mapper::toDomain);
    }

    @Override
    public List<PlanDefinicion> findAllActivos() {
        return planRepo.findAllByActivoTrueOrderByOrdenAsc()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
