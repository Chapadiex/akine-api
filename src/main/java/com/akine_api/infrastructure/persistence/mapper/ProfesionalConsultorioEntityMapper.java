package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.ProfesionalConsultorio;
import com.akine_api.infrastructure.persistence.entity.ProfesionalConsultorioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfesionalConsultorioEntityMapper {

    default ProfesionalConsultorio toDomain(ProfesionalConsultorioEntity entity) {
        if (entity == null) return null;
        return new ProfesionalConsultorio(
                entity.getId(),
                entity.getProfesionalId(),
                entity.getConsultorioId(),
                entity.isActivo(),
                entity.getCreatedAt()
        );
    }

    default ProfesionalConsultorioEntity toEntity(ProfesionalConsultorio domain) {
        if (domain == null) return null;
        ProfesionalConsultorioEntity e = new ProfesionalConsultorioEntity();
        e.setId(domain.getId());
        e.setProfesionalId(domain.getProfesionalId());
        e.setConsultorioId(domain.getConsultorioId());
        e.setActivo(domain.isActivo());
        e.setCreatedAt(domain.getCreatedAt());
        return e;
    }
}
