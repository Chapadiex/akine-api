package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.ConsultorioEspecialidad;
import com.akine_api.infrastructure.persistence.entity.ConsultorioEspecialidadEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsultorioEspecialidadEntityMapper {

    default ConsultorioEspecialidad toDomain(ConsultorioEspecialidadEntity entity) {
        if (entity == null) return null;
        return new ConsultorioEspecialidad(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getEspecialidadId(),
                entity.isActivo(),
                entity.getCreatedAt()
        );
    }

    default ConsultorioEspecialidadEntity toEntity(ConsultorioEspecialidad domain) {
        if (domain == null) return null;
        ConsultorioEspecialidadEntity e = new ConsultorioEspecialidadEntity();
        e.setId(domain.getId());
        e.setConsultorioId(domain.getConsultorioId());
        e.setEspecialidadId(domain.getEspecialidadId());
        e.setActivo(domain.isActivo());
        e.setCreatedAt(domain.getCreatedAt());
        e.setUpdatedAt(domain.getUpdatedAt());
        return e;
    }
}
