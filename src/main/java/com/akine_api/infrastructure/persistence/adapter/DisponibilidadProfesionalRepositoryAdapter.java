package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.DisponibilidadProfesionalRepositoryPort;
import com.akine_api.domain.model.DisponibilidadProfesional;
import com.akine_api.infrastructure.persistence.mapper.DisponibilidadProfesionalEntityMapper;
import com.akine_api.infrastructure.persistence.repository.DisponibilidadProfesionalJpaRepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DisponibilidadProfesionalRepositoryAdapter implements DisponibilidadProfesionalRepositoryPort {

    private final DisponibilidadProfesionalJpaRepository repo;
    private final DisponibilidadProfesionalEntityMapper mapper;

    public DisponibilidadProfesionalRepositoryAdapter(DisponibilidadProfesionalJpaRepository repo,
                                                      DisponibilidadProfesionalEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<DisponibilidadProfesional> findByProfesionalIdAndConsultorioId(UUID profesionalId, UUID consultorioId) {
        return repo.findByProfesionalIdAndConsultorioId(profesionalId, consultorioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<DisponibilidadProfesional> findByProfesionalIdAndConsultorioIdAndDiaSemana(UUID profesionalId, UUID consultorioId, DayOfWeek diaSemana) {
        return repo.findByProfesionalIdAndConsultorioIdAndDiaSemana(profesionalId, consultorioId, diaSemana)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<DisponibilidadProfesional> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByProfesionalIdAndConsultorioIdAndDiaSemana(UUID profesionalId, UUID consultorioId, DayOfWeek diaSemana) {
        return repo.existsByProfesionalIdAndConsultorioIdAndDiaSemana(profesionalId, consultorioId, diaSemana);
    }

    @Override
    public DisponibilidadProfesional save(DisponibilidadProfesional disponibilidad) {
        return mapper.toDomain(repo.save(mapper.toEntity(disponibilidad)));
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
