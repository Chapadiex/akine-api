package com.akine_api.infrastructure.persistence.adapter.cobertura;

import com.akine_api.domain.model.cobertura.FinanciadorSalud;
import com.akine_api.domain.repository.cobertura.FinanciadorSaludRepositoryPort;
import com.akine_api.infrastructure.persistence.entity.cobertura.FinanciadorSaludEntity;
import com.akine_api.infrastructure.persistence.mapper.cobertura.FinanciadorSaludEntityMapper;
import com.akine_api.infrastructure.persistence.repository.cobertura.FinanciadorSaludJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FinanciadorSaludRepositoryAdapter implements FinanciadorSaludRepositoryPort {

    private final FinanciadorSaludJpaRepository jpaRepository;
    private final FinanciadorSaludEntityMapper mapper;

    @Override
    public FinanciadorSalud save(FinanciadorSalud financiadorSalud) {
        FinanciadorSaludEntity entity = mapper.toEntity(financiadorSalud);
        FinanciadorSaludEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public FinanciadorSalud update(UUID id, FinanciadorSalud financiadorSalud) {
        FinanciadorSaludEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FinanciadorSalud no encontrado: " + id));
        entity.setCodigoExterno(financiadorSalud.getCodigoExterno());
        entity.setTipoFinanciador(financiadorSalud.getTipoFinanciador());
        entity.setNombre(financiadorSalud.getNombre());
        entity.setNombreCorto(financiadorSalud.getNombreCorto());
        entity.setAmbitoCobertura(financiadorSalud.getAmbitoCobertura());
        entity.setActivo(financiadorSalud.getActivo());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<FinanciadorSalud> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNombreAndConsultorioId(String nombre, UUID consultorioId) {
        return jpaRepository.existsByNombreIgnoreCaseAndConsultorioId(nombre, consultorioId);
    }

    @Override
    public boolean existsByNombreAndConsultorioIdExcludingId(String nombre, UUID consultorioId, UUID excludeId) {
        return jpaRepository.existsByNombreIgnoreCaseAndConsultorioIdAndIdNot(nombre, consultorioId, excludeId);
    }

    @Override
    public List<FinanciadorSalud> findAllByConsultorioId(UUID consultorioId) {
        return jpaRepository.findByConsultorioId(consultorioId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
