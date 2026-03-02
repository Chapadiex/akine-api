package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Paciente;
import com.akine_api.infrastructure.persistence.entity.PacienteEntity;
import org.mapstruct.Mapper;

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
                entity.getProfesion(),
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
        entity.setProfesion(domain.getProfesion());
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
}
