package com.akine_api.infrastructure.persistence.adapter.convenio;

import com.akine_api.domain.model.convenio.Prestacion;
import com.akine_api.domain.repository.convenio.PrestacionRepositoryPort;
import com.akine_api.infrastructure.persistence.mapper.convenio.PrestacionEntityMapper;
import com.akine_api.infrastructure.persistence.repository.convenio.PrestacionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PrestacionRepositoryAdapter implements PrestacionRepositoryPort {

    private final PrestacionJpaRepository jpaRepo;
    private final PrestacionEntityMapper mapper;

    @Override
    public Prestacion save(Prestacion p) {
        return mapper.toDomain(jpaRepo.save(mapper.toEntity(p)));
    }

    @Override
    public Optional<Prestacion> findById(UUID id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Prestacion> findByCodigoNomenclador(String codigo) {
        return jpaRepo.findByCodigoNomenclador(codigo).map(mapper::toDomain);
    }

    @Override
    public List<Prestacion> findAll() {
        return jpaRepo.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prestacion> findAllActivas() {
        return jpaRepo.findByActivaTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
