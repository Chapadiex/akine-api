package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.TurnoRepositoryPort;
import com.akine_api.domain.model.Turno;
import com.akine_api.infrastructure.persistence.mapper.TurnoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.TurnoJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TurnoRepositoryAdapter implements TurnoRepositoryPort {

    private final TurnoJpaRepository repo;
    private final TurnoEntityMapper mapper;

    public TurnoRepositoryAdapter(TurnoJpaRepository repo, TurnoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Optional<Turno> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Turno> findByConsultorioIdAndRange(UUID consultorioId, LocalDateTime from, LocalDateTime to) {
        return repo.findByConsultorioIdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThan(
                consultorioId, from, to).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Turno> findByProfesionalIdAndRange(UUID profesionalId, LocalDateTime from, LocalDateTime to) {
        return repo.findByProfesionalIdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThan(
                profesionalId, from, to).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Turno> findByPacienteIdAndRange(UUID pacienteId, LocalDateTime from, LocalDateTime to) {
        return repo.findByPacienteIdAndFechaHoraInicioGreaterThanEqualAndFechaHoraInicioLessThan(
                pacienteId, from, to).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Turno save(Turno turno) {
        return mapper.toDomain(repo.save(mapper.toEntity(turno)));
    }
}
