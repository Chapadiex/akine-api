package com.akine_api.infrastructure.persistence.adapter.cobertura;

import com.akine_api.domain.model.cobertura.PacienteCobertura;
import com.akine_api.domain.repository.cobertura.PacienteCoberturaRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.cobertura.PacienteCoberturaEntity;
import com.akine_api.infrastructure.persistence.mapper.cobertura.PacienteCoberturaEntityMapper;
import com.akine_api.infrastructure.persistence.repository.cobertura.PacienteCoberturaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PacienteCoberturaRepositoryAdapter implements PacienteCoberturaRepositoryPort {

    private final PacienteCoberturaJpaRepository jpaRepository;
    private final PacienteCoberturaEntityMapper mapper;

    @Override
    public PacienteCobertura save(PacienteCobertura pacienteCobertura) {
        PacienteCoberturaEntity entity = mapper.toEntity(pacienteCobertura);
        PacienteCoberturaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PacienteCobertura> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PacienteCobertura> findByPacienteId(UUID pacienteId) {
        return jpaRepository.findByPacienteId(pacienteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
