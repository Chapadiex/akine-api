package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.ConsultorioDuracionTurnoRepositoryPort;
import com.akine_api.domain.model.ConsultorioDuracionTurno;
import com.akine_api.infrastructure.persistence.mapper.ConsultorioDuracionTurnoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.ConsultorioDuracionTurnoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ConsultorioDuracionTurnoRepositoryAdapter implements ConsultorioDuracionTurnoRepositoryPort {

    private final ConsultorioDuracionTurnoJpaRepository repo;
    private final ConsultorioDuracionTurnoEntityMapper mapper;

    public ConsultorioDuracionTurnoRepositoryAdapter(ConsultorioDuracionTurnoJpaRepository repo,
                                                     ConsultorioDuracionTurnoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<ConsultorioDuracionTurno> findByConsultorioId(UUID consultorioId) {
        return repo.findByConsultorioId(consultorioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByConsultorioIdAndMinutos(UUID consultorioId, int minutos) {
        return repo.existsByConsultorioIdAndMinutos(consultorioId, minutos);
    }

    @Override
    public ConsultorioDuracionTurno save(ConsultorioDuracionTurno duracion) {
        return mapper.toDomain(repo.save(mapper.toEntity(duracion)));
    }

    @Override
    public void deleteByConsultorioIdAndMinutos(UUID consultorioId, int minutos) {
        repo.deleteByConsultorioIdAndMinutos(consultorioId, minutos);
    }
}
