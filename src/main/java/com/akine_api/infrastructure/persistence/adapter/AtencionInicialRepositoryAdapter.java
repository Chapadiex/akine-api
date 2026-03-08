package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.AtencionInicialRepositoryPort;
import com.akine_api.domain.model.AtencionInicial;
import com.akine_api.infrastructure.persistence.mapper.AtencionInicialEntityMapper;
import com.akine_api.infrastructure.persistence.repository.AtencionInicialJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AtencionInicialRepositoryAdapter implements AtencionInicialRepositoryPort {

    private final AtencionInicialJpaRepository repo;
    private final AtencionInicialEntityMapper mapper;

    public AtencionInicialRepositoryAdapter(AtencionInicialJpaRepository repo,
                                            AtencionInicialEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public AtencionInicial save(AtencionInicial atencionInicial) {
        return mapper.toDomain(repo.save(mapper.toEntity(atencionInicial)));
    }

    @Override
    public Optional<AtencionInicial> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AtencionInicial> findLatestByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId) {
        return repo.findFirstByConsultorioIdAndPacienteIdOrderByFechaHoraDesc(consultorioId, pacienteId).map(mapper::toDomain);
    }

    @Override
    public List<AtencionInicial> findByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId) {
        return repo.findByConsultorioIdAndPacienteIdOrderByFechaHoraDesc(consultorioId, pacienteId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
