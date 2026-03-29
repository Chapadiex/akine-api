package com.akine_api.infrastructure.persistence.adapter.convenio;

import com.akine_api.domain.model.convenio.Convenio;
import com.akine_api.domain.repository.convenio.ConvenioRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.convenio.ConvenioEntity;
import com.akine_api.infrastructure.persistence.mapper.convenio.ConvenioEntityMapper;
import com.akine_api.infrastructure.persistence.repository.cobertura.FinanciadorSaludJpaRepository;
import com.akine_api.infrastructure.persistence.repository.convenio.ConvenioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConvenioRepositoryAdapter implements ConvenioRepositoryPort {

    private final ConvenioJpaRepository jpaRepo;
    private final FinanciadorSaludJpaRepository financiadorJpaRepo;
    private final ConvenioEntityMapper mapper;

    @Override
    public Convenio save(Convenio c) {
        ConvenioEntity entity = mapper.toEntity(c);
        entity.setFinanciador(financiadorJpaRepo.getReferenceById(c.getFinanciadorId()));
        return mapper.toDomain(jpaRepo.save(entity));
    }

    @Override
    public Optional<Convenio> findById(UUID id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Convenio> findByConsultorioId(UUID consultorioId) {
        return jpaRepo.findByConsultorioId(consultorioId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        jpaRepo.deleteById(id);
    }
}
