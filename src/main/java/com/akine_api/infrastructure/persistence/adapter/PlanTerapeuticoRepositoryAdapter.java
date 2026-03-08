package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.PlanTerapeuticoRepositoryPort;
import com.akine_api.domain.model.PlanTerapeutico;
import com.akine_api.infrastructure.persistence.mapper.PlanTerapeuticoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.PlanTerapeuticoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PlanTerapeuticoRepositoryAdapter implements PlanTerapeuticoRepositoryPort {

    private final PlanTerapeuticoJpaRepository repo;
    private final PlanTerapeuticoEntityMapper mapper;

    public PlanTerapeuticoRepositoryAdapter(PlanTerapeuticoJpaRepository repo,
                                            PlanTerapeuticoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public PlanTerapeutico save(PlanTerapeutico planTerapeutico) {
        return mapper.toDomain(repo.save(mapper.toEntity(planTerapeutico)));
    }

    @Override
    public Optional<PlanTerapeutico> findLatestByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId) {
        return repo.findFirstByConsultorioIdAndPacienteIdOrderByCreatedAtDesc(consultorioId, pacienteId).map(mapper::toDomain);
    }

    @Override
    public Optional<PlanTerapeutico> findByAtencionInicialId(UUID atencionInicialId) {
        return repo.findByAtencionInicialId(atencionInicialId).map(mapper::toDomain);
    }
}
