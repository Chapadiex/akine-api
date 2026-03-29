package com.akine_api.infrastructure.persistence.adapter.convenio;

import com.akine_api.domain.model.convenio.ConvenioVersion;
import com.akine_api.domain.repository.convenio.ConvenioVersionRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.convenio.ConvenioVersionEntity;
import com.akine_api.infrastructure.persistence.mapper.convenio.ConvenioVersionEntityMapper;
import com.akine_api.infrastructure.persistence.repository.convenio.ConvenioJpaRepository;
import com.akine_api.infrastructure.persistence.repository.convenio.ConvenioVersionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConvenioVersionRepositoryAdapter implements ConvenioVersionRepositoryPort {

    private final ConvenioVersionJpaRepository jpaRepo;
    private final ConvenioJpaRepository convenioJpaRepo;
    private final ConvenioVersionEntityMapper mapper;

    @Override
    public ConvenioVersion save(ConvenioVersion v) {
        ConvenioVersionEntity entity = mapper.toEntity(v);
        entity.setConvenio(convenioJpaRepo.getReferenceById(v.getConvenioId()));
        return mapper.toDomain(jpaRepo.save(entity));
    }

    @Override
    public Optional<ConvenioVersion> findById(UUID id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ConvenioVersion> findByConvenioId(UUID convenioId) {
        return jpaRepo.findByConvenioIdOrderByVersionNumDesc(convenioId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ConvenioVersion> findVigenteByConvenioId(UUID convenioId) {
        return jpaRepo.findVigenteByConvenioId(convenioId).map(mapper::toDomain);
    }

    @Override
    public int countLotesByVersionId(UUID versionId) {
        return 0;
    }
}
