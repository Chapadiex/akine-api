package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.PacienteConsultorio;
import com.akine_api.infrastructure.persistence.entity.PacienteConsultorioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PacienteConsultorioEntityMapper {

    default PacienteConsultorio toDomain(PacienteConsultorioEntity entity) {
        if (entity == null) return null;
        return new PacienteConsultorio(
                entity.getId(),
                entity.getPacienteId(),
                entity.getConsultorioId(),
                entity.getCreatedByUserId(),
                entity.getCreatedAt()
        );
    }

    default PacienteConsultorioEntity toEntity(PacienteConsultorio domain) {
        if (domain == null) return null;
        PacienteConsultorioEntity entity = new PacienteConsultorioEntity();
        entity.setId(domain.getId());
        entity.setPacienteId(domain.getPacienteId());
        entity.setConsultorioId(domain.getConsultorioId());
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
