package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.ConsultorioHorarioRepositoryPort;
import com.akine_api.domain.model.ConsultorioHorario;
import com.akine_api.infrastructure.persistence.mapper.ConsultorioHorarioEntityMapper;
import com.akine_api.infrastructure.persistence.repository.ConsultorioHorarioJpaRepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConsultorioHorarioRepositoryAdapter implements ConsultorioHorarioRepositoryPort {

    private final ConsultorioHorarioJpaRepository repo;
    private final ConsultorioHorarioEntityMapper mapper;

    public ConsultorioHorarioRepositoryAdapter(ConsultorioHorarioJpaRepository repo,
                                               ConsultorioHorarioEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<ConsultorioHorario> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioId(consultorioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ConsultorioHorario> findByConsultorioIdAndDiaSemana(UUID consultorioId, DayOfWeek diaSemana) {
        return repo.findByConsultorioIdAndDiaSemana(consultorioId, diaSemana).map(mapper::toDomain);
    }

    @Override
    public ConsultorioHorario save(ConsultorioHorario horario) {
        return mapper.toDomain(repo.save(mapper.toEntity(horario)));
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
