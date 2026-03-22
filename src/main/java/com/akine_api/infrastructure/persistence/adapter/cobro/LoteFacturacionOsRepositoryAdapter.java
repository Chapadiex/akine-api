package com.akine_api.infrastructure.persistence.adapter.cobro;

import com.akine_api.domain.model.cobro.EstadoLoteOs;
import com.akine_api.domain.model.cobro.LoteFacturacionOs;
import com.akine_api.domain.repository.cobro.LoteFacturacionOsRepositoryPort;
import com.akine_api.infrastructure.persistence.mapper.cobro.LoteFacturacionOsEntityMapper;
import com.akine_api.infrastructure.persistence.repository.cobro.LoteFacturacionOsJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class LoteFacturacionOsRepositoryAdapter implements LoteFacturacionOsRepositoryPort {

    private final LoteFacturacionOsJpaRepository repo;
    private final LoteFacturacionOsEntityMapper mapper;

    public LoteFacturacionOsRepositoryAdapter(LoteFacturacionOsJpaRepository repo,
                                               LoteFacturacionOsEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public LoteFacturacionOs save(LoteFacturacionOs lote) {
        return mapper.toDomain(repo.save(mapper.toEntity(lote)));
    }

    @Override
    public Optional<LoteFacturacionOs> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<LoteFacturacionOs> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioId(consultorioId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<LoteFacturacionOs> findByConsultorioIdAndEstado(UUID consultorioId, EstadoLoteOs estado) {
        return repo.findByConsultorioIdAndEstado(consultorioId, estado).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<LoteFacturacionOs> findByConsultorioIdAndFinanciadorId(UUID consultorioId, UUID financiadorId) {
        return repo.findByConsultorioIdAndFinanciadorId(consultorioId, financiadorId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<UUID> findLiquidacionIdsEnLotesActivos(UUID consultorioId) {
        return repo.findLiquidacionIdsEnLotesActivos(consultorioId);
    }
}
