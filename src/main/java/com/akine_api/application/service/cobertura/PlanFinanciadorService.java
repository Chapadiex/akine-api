package com.akine_api.application.service.cobertura;

import com.akine_api.domain.exception.PlanDuplicadoException;
import com.akine_api.domain.model.cobertura.PlanFinanciador;
import com.akine_api.domain.repository.cobertura.FinanciadorSaludRepositoryPort;
import com.akine_api.domain.repository.cobertura.PlanFinanciadorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanFinanciadorService {

    private final PlanFinanciadorRepositoryPort planRepositoryPort;
    private final FinanciadorSaludRepositoryPort financiadorRepositoryPort;

    @Transactional
    public PlanFinanciador create(PlanFinanciador planFinanciador) {
        financiadorRepositoryPort.findById(planFinanciador.getFinanciadorId())
                .orElseThrow(() -> new RuntimeException("FinanciadorSalud no encontrado"));
        if (planRepositoryPort.existsWithOverlappingVigencia(
                planFinanciador.getNombrePlan(),
                planFinanciador.getFinanciadorId(),
                planFinanciador.getVigenciaDesde(),
                planFinanciador.getVigenciaHasta())) {
            throw new PlanDuplicadoException(planFinanciador.getNombrePlan());
        }
        planFinanciador.setActivo(true);
        return planRepositoryPort.save(planFinanciador);
    }

    @Transactional
    public PlanFinanciador update(UUID id, PlanFinanciador planFinanciador) {
        PlanFinanciador existing = planRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("PlanFinanciador no encontrado"));
        if (planRepositoryPort.existsWithOverlappingVigenciaExcludingId(
                planFinanciador.getNombrePlan(),
                existing.getFinanciadorId(),
                planFinanciador.getVigenciaDesde(),
                planFinanciador.getVigenciaHasta(),
                id)) {
            throw new PlanDuplicadoException(planFinanciador.getNombrePlan());
        }
        return planRepositoryPort.update(id, planFinanciador);
    }

    @Transactional(readOnly = true)
    public List<PlanFinanciador> findByFinanciadorId(UUID financiadorId) {
        return planRepositoryPort.findByFinanciadorId(financiadorId);
    }

    @Transactional(readOnly = true)
    public PlanFinanciador findById(UUID id) {
        return planRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("PlanFinanciador no encontrado"));
    }
}
