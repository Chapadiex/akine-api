package com.akine_api.infrastructure.persistence.adapter.sesion;

import com.akine_api.domain.model.sesion.SesionAdministrativa;
import com.akine_api.domain.repository.sesion.SesionAdministrativaRepositoryPort;
import com.akine_api.infrastructure.persistence.mapper.sesion.SesionAdministrativaEntityMapper;
import com.akine_api.infrastructure.persistence.repository.sesion.SesionAdministrativaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SesionAdministrativaRepositoryAdapter implements SesionAdministrativaRepositoryPort {

    private final SesionAdministrativaJpaRepository repo;
    private final SesionAdministrativaEntityMapper mapper;

    public SesionAdministrativaRepositoryAdapter(SesionAdministrativaJpaRepository repo,
                                                  SesionAdministrativaEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public SesionAdministrativa save(SesionAdministrativa sesion) {
        return mapper.toDomain(repo.save(mapper.toEntity(sesion)));
    }

    @Override
    public Optional<SesionAdministrativa> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<SesionAdministrativa> findBySesionId(UUID sesionId) {
        return repo.findBySesionId(sesionId).map(mapper::toDomain);
    }

    @Override
    public Optional<SesionAdministrativa> findByTurnoId(UUID turnoId) {
        return repo.findByTurnoId(turnoId).map(mapper::toDomain);
    }

    @Override
    public List<SesionAdministrativa> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioId(consultorioId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
