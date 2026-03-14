package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Paciente;
import com.akine_api.infrastructure.persistence.entity.PacienteEntity;
import org.mapstruct.Mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PacienteEntityMapper {

    default Paciente toDomain(PacienteEntity entity) {
        if (entity == null) return null;
        return new Paciente(
                entity.getId(),
                entity.getDni(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getTelefono(),
                entity.getEmail(),
                entity.getFechaNacimiento(),
                entity.getSexo(),
                entity.getDomicilio(),
                entity.getNacionalidad(),
                entity.getEstadoCivil(),
                csvToList(entity.getProfesiones()),
                entity.getObraSocialNombre(),
                entity.getObraSocialPlan(),
                entity.getObraSocialNroAfiliado(),
                entity.getUserId(),
                entity.isActivo(),
                entity.getCreatedByUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default PacienteEntity toEntity(Paciente domain) {
        if (domain == null) return null;
        PacienteEntity entity = new PacienteEntity();
        entity.setId(domain.getId());
        entity.setDni(domain.getDni());
        entity.setNombre(domain.getNombre());
        entity.setApellido(domain.getApellido());
        entity.setTelefono(domain.getTelefono());
        entity.setEmail(domain.getEmail());
        entity.setFechaNacimiento(domain.getFechaNacimiento());
        entity.setSexo(domain.getSexo());
        entity.setDomicilio(domain.getDomicilio());
        entity.setNacionalidad(domain.getNacionalidad());
        entity.setEstadoCivil(domain.getEstadoCivil());
        entity.setProfesiones(listToCsv(domain.getProfesiones()));
        entity.setObraSocialNombre(domain.getObraSocialNombre());
        entity.setObraSocialPlan(domain.getObraSocialPlan());
        entity.setObraSocialNroAfiliado(domain.getObraSocialNroAfiliado());
        entity.setUserId(domain.getUserId());
        entity.setActivo(domain.isActivo());
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private static List<String> csvToList(String csv) {
        if (csv == null || csv.isBlank()) return Collections.emptyList();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static String listToCsv(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return list.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }
}
