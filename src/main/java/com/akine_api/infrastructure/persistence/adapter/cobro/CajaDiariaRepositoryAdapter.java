package com.akine_api.infrastructure.persistence.adapter.cobro;

import com.akine_api.domain.model.cobro.CajaDiaria;
import com.akine_api.domain.model.cobro.CajaDiariaEstado;
import com.akine_api.domain.repository.cobro.CajaDiariaRepositoryPort;
import com.akine_api.infrastructure.persistence.mapper.cobro.CajaDiariaEntityMapper;
import com.akine_api.infrastructure.persistence.repository.cobro.CajaDiariaJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CajaDiariaRepositoryAdapter implements CajaDiariaRepositoryPort {

    private final CajaDiariaJpaRepository repo;
    private final CajaDiariaEntityMapper mapper;

    public CajaDiariaRepositoryAdapter(CajaDiariaJpaRepository repo, CajaDiariaEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public CajaDiaria save(CajaDiaria caja) {
        return mapper.toDomain(repo.save(mapper.toEntity(caja)));
    }

    @Override
    public Optional<CajaDiaria> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<CajaDiaria> findAbiertaByConsultorioIdAndFechaAndTurno(UUID consultorioId, LocalDate fecha, String turnoCaja) {
        if (turnoCaja == null) {
            return repo.findByConsultorioIdAndFechaOperativaAndTurnoCajaIsNullAndEstado(
                    consultorioId, fecha, CajaDiariaEstado.ABIERTA)
                    .map(mapper::toDomain);
        }
        return repo.findByConsultorioIdAndFechaOperativaAndTurnoCajaAndEstado(
                consultorioId, fecha, turnoCaja, CajaDiariaEstado.ABIERTA)
                .map(mapper::toDomain);
    }

    @Override
    public List<CajaDiaria> findByConsultorioIdAndFecha(UUID consultorioId, LocalDate fecha) {
        return repo.findByConsultorioIdAndFechaOperativa(consultorioId, fecha).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsAbierta(UUID consultorioId, LocalDate fecha, String turnoCaja) {
        if (turnoCaja == null) {
            return repo.existsByConsultorioIdAndFechaOperativaAndTurnoCajaIsNullAndEstado(
                    consultorioId, fecha, CajaDiariaEstado.ABIERTA);
        }
        return repo.existsByConsultorioIdAndFechaOperativaAndTurnoCajaAndEstado(
                consultorioId, fecha, turnoCaja, CajaDiariaEstado.ABIERTA);
    }
}
