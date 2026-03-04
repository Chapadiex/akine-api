package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Profesional;
import com.akine_api.infrastructure.persistence.entity.ProfesionalEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfesionalEntityMapper {

    default Profesional toDomain(ProfesionalEntity entity) {
        if (entity == null) return null;
        return new Profesional(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getNroDocumento(),
                entity.getMatricula(),
                entity.getEspecialidad(),
                entity.getEspecialidades(),
                entity.getEmail(),
                entity.getTelefono(),
                entity.getDomicilio(),
                entity.getFotoPerfilUrl(),
                entity.getFechaAlta(),
                entity.getFechaBaja(),
                entity.getMotivoBaja(),
                entity.isActivo(),
                entity.getCreatedAt()
        );
    }

    default ProfesionalEntity toEntity(Profesional domain) {
        if (domain == null) return null;
        ProfesionalEntity e = new ProfesionalEntity();
        e.setId(domain.getId());
        e.setConsultorioId(domain.getConsultorioId());
        e.setNombre(domain.getNombre());
        e.setApellido(domain.getApellido());
        e.setNroDocumento(domain.getNroDocumento());
        e.setMatricula(domain.getMatricula());
        e.setEspecialidad(domain.getEspecialidad());
        e.setEspecialidades(domain.getEspecialidades());
        e.setEmail(domain.getEmail());
        e.setTelefono(domain.getTelefono());
        e.setDomicilio(domain.getDomicilio());
        e.setFotoPerfilUrl(domain.getFotoPerfilUrl());
        e.setFechaAlta(domain.getFechaAlta());
        e.setFechaBaja(domain.getFechaBaja());
        e.setMotivoBaja(domain.getMotivoBaja());
        e.setActivo(domain.isActivo());
        e.setCreatedAt(domain.getCreatedAt());
        e.setUpdatedAt(domain.getUpdatedAt());
        return e;
    }
}
