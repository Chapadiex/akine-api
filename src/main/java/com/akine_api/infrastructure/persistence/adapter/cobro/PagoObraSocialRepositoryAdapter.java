package com.akine_api.infrastructure.persistence.adapter.cobro;

import com.akine_api.domain.model.cobro.PagoObraSocial;
import com.akine_api.domain.repository.cobro.PagoObraSocialRepositoryPort;
import com.akine_api.infrastructure.persistence.mapper.cobro.PagoObraSocialEntityMapper;
import com.akine_api.infrastructure.persistence.repository.cobro.PagoObraSocialJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PagoObraSocialRepositoryAdapter implements PagoObraSocialRepositoryPort {

    private final PagoObraSocialJpaRepository repo;
    private final PagoObraSocialEntityMapper mapper;

    public PagoObraSocialRepositoryAdapter(PagoObraSocialJpaRepository repo,
                                            PagoObraSocialEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public PagoObraSocial save(PagoObraSocial pago) {
        return mapper.toDomain(repo.save(mapper.toEntity(pago)));
    }

    @Override
    public Optional<PagoObraSocial> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PagoObraSocial> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioId(consultorioId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<PagoObraSocial> findByLoteId(UUID loteId) {
        return repo.findByLoteId(loteId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
