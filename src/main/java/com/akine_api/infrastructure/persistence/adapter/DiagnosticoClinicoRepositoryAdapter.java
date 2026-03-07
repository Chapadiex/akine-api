package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.DiagnosticoClinicoRepositoryPort;
import com.akine_api.domain.model.DiagnosticoClinico;
import com.akine_api.infrastructure.persistence.mapper.DiagnosticoClinicoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.DiagnosticoClinicoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DiagnosticoClinicoRepositoryAdapter implements DiagnosticoClinicoRepositoryPort {

    private final DiagnosticoClinicoJpaRepository repo;
    private final DiagnosticoClinicoEntityMapper mapper;

    public DiagnosticoClinicoRepositoryAdapter(DiagnosticoClinicoJpaRepository repo,
                                               DiagnosticoClinicoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public DiagnosticoClinico save(DiagnosticoClinico diagnosticoClinico) {
        return mapper.toDomain(repo.save(mapper.toEntity(diagnosticoClinico)));
    }

    @Override
    public Optional<DiagnosticoClinico> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<DiagnosticoClinico> findByPacienteIdAndConsultorioId(UUID pacienteId, UUID consultorioId) {
        return repo.findByPacienteIdAndConsultorioIdOrderByFechaInicioDesc(pacienteId, consultorioId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
