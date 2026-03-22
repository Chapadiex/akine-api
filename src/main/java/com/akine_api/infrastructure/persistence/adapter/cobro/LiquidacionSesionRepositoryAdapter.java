package com.akine_api.infrastructure.persistence.adapter.cobro;

import com.akine_api.domain.model.cobro.EstadoLiquidacion;
import com.akine_api.domain.model.cobro.LiquidacionSesion;
import com.akine_api.domain.repository.cobro.LiquidacionSesionRepositoryPort;
import com.akine_api.infrastructure.persistence.mapper.cobro.LiquidacionSesionEntityMapper;
import com.akine_api.infrastructure.persistence.repository.cobro.LiquidacionSesionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class LiquidacionSesionRepositoryAdapter implements LiquidacionSesionRepositoryPort {

    private final LiquidacionSesionJpaRepository repo;
    private final LiquidacionSesionEntityMapper mapper;

    public LiquidacionSesionRepositoryAdapter(LiquidacionSesionJpaRepository repo,
                                               LiquidacionSesionEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public LiquidacionSesion save(LiquidacionSesion liquidacion) {
        return mapper.toDomain(repo.save(mapper.toEntity(liquidacion)));
    }

    @Override
    public Optional<LiquidacionSesion> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<LiquidacionSesion> findActivaBySesionId(UUID sesionId) {
        return repo.findActivaBySesionId(sesionId).map(mapper::toDomain);
    }

    @Override
    public List<LiquidacionSesion> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioId(consultorioId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<LiquidacionSesion> findByConsultorioIdAndEstado(UUID consultorioId, EstadoLiquidacion estado) {
        return repo.findByConsultorioIdAndEstado(consultorioId, estado).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
