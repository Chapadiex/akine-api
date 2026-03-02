package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Box;
import com.akine_api.domain.model.BoxCapacidadTipo;
import com.akine_api.domain.model.BoxTipo;
import com.akine_api.infrastructure.persistence.entity.BoxEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BoxEntityMapper {

    default Box toDomain(BoxEntity entity) {
        if (entity == null) return null;
        return new Box(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getNombre(),
                entity.getCodigo(),
                BoxTipo.valueOf(entity.getTipo()),
                entity.getCapacityType() == null ? BoxCapacidadTipo.UNLIMITED : BoxCapacidadTipo.valueOf(entity.getCapacityType()),
                entity.getCapacity(),
                entity.isActivo(),
                entity.getCreatedAt()
        );
    }

    default BoxEntity toEntity(Box domain) {
        if (domain == null) return null;
        BoxEntity e = new BoxEntity();
        e.setId(domain.getId());
        e.setConsultorioId(domain.getConsultorioId());
        e.setNombre(domain.getNombre());
        e.setCodigo(domain.getCodigo());
        e.setTipo(domain.getTipo().name());
        e.setCapacityType(domain.getCapacityType().name());
        e.setCapacity(domain.getCapacity());
        e.setActivo(domain.isActivo());
        e.setCreatedAt(domain.getCreatedAt());
        e.setUpdatedAt(domain.getUpdatedAt());
        return e;
    }
}
