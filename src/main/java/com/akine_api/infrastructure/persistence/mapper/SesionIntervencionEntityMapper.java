package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.SesionIntervencion;
import com.akine_api.infrastructure.persistence.entity.SesionIntervencionEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SesionIntervencionEntityMapper {

    default SesionIntervencion toDomain(SesionIntervencionEntity entity) {
        if (entity == null) return null;
        return new SesionIntervencion(
                entity.getId(),
                entity.getSesionId(),
                entity.getTratamientoId(),
                entity.getTratamientoNombre(),
                entity.getTécnica(),
                entity.getZona(),
                entity.getParametrosJson(),
                entity.getDuracionMinutos(),
                entity.getProfesionalId(),
                entity.getObservaciones(),
                entity.getOrderIndex(),
                entity.getCreatedAt()
        );
    }

    default SesionIntervencionEntity toEntity(SesionIntervencion domain) {
        if (domain == null) return null;
        SesionIntervencionEntity entity = new SesionIntervencionEntity();
        entity.setId(domain.getId());
        entity.setSesionId(domain.getSesionId());
        entity.setTratamientoId(domain.getTratamientoId());
        entity.setTratamientoNombre(domain.getTratamientoNombre());
        entity.setTécnica(domain.getTécnica());
        entity.setZona(domain.getZona());
        entity.setParametrosJson(domain.getParametrosJson());
        entity.setDuracionMinutos(domain.getDuracionMinutos());
        entity.setProfesionalId(domain.getProfesionalId());
        entity.setObservaciones(domain.getObservaciones());
        entity.setOrderIndex(domain.getOrderIndex());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    default List<SesionIntervencion> toDomainList(List<SesionIntervencionEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    default List<SesionIntervencionEntity> toEntityList(List<SesionIntervencion> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
