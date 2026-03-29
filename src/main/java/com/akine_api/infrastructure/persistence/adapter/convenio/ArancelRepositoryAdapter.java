package com.akine_api.infrastructure.persistence.adapter.convenio;

import com.akine_api.domain.model.convenio.Arancel;
import com.akine_api.domain.repository.convenio.ArancelRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.convenio.ArancelEntity;
import com.akine_api.infrastructure.persistence.mapper.convenio.ArancelEntityMapper;
import com.akine_api.infrastructure.persistence.repository.convenio.ArancelJpaRepository;
import com.akine_api.infrastructure.persistence.repository.convenio.ConvenioVersionJpaRepository;
import com.akine_api.infrastructure.persistence.repository.convenio.PrestacionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ArancelRepositoryAdapter implements ArancelRepositoryPort {

    private final ArancelJpaRepository jpaRepo;
    private final ConvenioVersionJpaRepository convenioVersionJpaRepo;
    private final PrestacionJpaRepository prestacionJpaRepo;
    private final ArancelEntityMapper mapper;

    @Override
    public Arancel save(Arancel a) {
        ArancelEntity entity = mapper.toEntity(a);
        entity.setConvenioVersion(convenioVersionJpaRepo.getReferenceById(a.getConvenioVersionId()));
        entity.setPrestacion(prestacionJpaRepo.getReferenceById(a.getPrestacionId()));
        return mapper.toDomain(jpaRepo.save(entity));
    }

    @Override
    public Optional<Arancel> findById(UUID id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Arancel> findByConvenioVersionId(UUID versionId) {
        return jpaRepo.findByConvenioVersionId(versionId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Arancel> findVigentesByVersion(UUID versionId, LocalDate fecha) {
        return jpaRepo.findVigentesByVersion(versionId, fecha).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepo.deleteById(id);
    }
}
