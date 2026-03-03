package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.ConsultorioFeriadoRepositoryPort;
import com.akine_api.domain.model.ConsultorioFeriado;
import com.akine_api.infrastructure.persistence.mapper.ConsultorioFeriadoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.ConsultorioFeriadoJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConsultorioFeriadoRepositoryAdapter implements ConsultorioFeriadoRepositoryPort {

    private final ConsultorioFeriadoJpaRepository repo;
    private final ConsultorioFeriadoEntityMapper mapper;

    public ConsultorioFeriadoRepositoryAdapter(ConsultorioFeriadoJpaRepository repo,
                                                ConsultorioFeriadoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public boolean existsByConsultorioIdAndFecha(UUID consultorioId, LocalDate fecha) {
        return repo.existsByConsultorioIdAndFecha(consultorioId, fecha);
    }

    @Override
    public List<ConsultorioFeriado> findByConsultorioIdAndYear(UUID consultorioId, int year) {
        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = LocalDate.of(year, 12, 31);
        return repo.findByConsultorioIdAndFechaBetweenOrderByFechaAsc(consultorioId, from, to)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ConsultorioFeriado> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public ConsultorioFeriado save(ConsultorioFeriado feriado) {
        return mapper.toDomain(repo.save(mapper.toEntity(feriado)));
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
