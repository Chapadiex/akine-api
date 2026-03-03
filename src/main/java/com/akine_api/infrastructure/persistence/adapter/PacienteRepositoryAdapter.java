package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.PacienteRepositoryPort;
import com.akine_api.domain.model.Paciente;
import com.akine_api.infrastructure.persistence.mapper.PacienteEntityMapper;
import com.akine_api.infrastructure.persistence.repository.PacienteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PacienteRepositoryAdapter implements PacienteRepositoryPort {

    private final PacienteJpaRepository jpaRepository;
    private final PacienteEntityMapper mapper;

    public PacienteRepositoryAdapter(PacienteJpaRepository jpaRepository,
                                     PacienteEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Paciente save(Paciente paciente) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(paciente)));
    }

    @Override
    public Optional<Paciente> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Paciente> findByDni(String dni) {
        return jpaRepository.findByDni(dni).map(mapper::toDomain);
    }

    @Override
    public Optional<Paciente> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public List<Paciente> findByIds(List<UUID> ids) {
        if (ids.isEmpty()) return List.of();
        List<Paciente> pacientes = jpaRepository.findAllById(ids)
                .stream()
                .map(mapper::toDomain)
                .toList();
        return pacientes.stream()
                .sorted(Comparator.comparingInt(p -> ids.indexOf(p.getId())))
                .toList();
    }

    @Override
    public List<Paciente> searchByNombreApellido(String query, int limit) {
        return jpaRepository.findTop20ByApellidoContainingIgnoreCaseOrNombreContainingIgnoreCase(query, query)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
