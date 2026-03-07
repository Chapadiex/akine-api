package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.CargoEmpleadoCatalogo;
import com.akine_api.infrastructure.persistence.entity.CargoEmpleadoCatalogoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CargoEmpleadoCatalogoEntityMapper {

    default CargoEmpleadoCatalogo toDomain(CargoEmpleadoCatalogoEntity entity) {
        if (entity == null) return null;
        return new CargoEmpleadoCatalogo(
                entity.getId(),
                entity.getNombre(),
                entity.getSlug(),
                entity.isActivo(),
                entity.getOrden(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default CargoEmpleadoCatalogoEntity toEntity(CargoEmpleadoCatalogo domain) {
        if (domain == null) return null;
        CargoEmpleadoCatalogoEntity entity = new CargoEmpleadoCatalogoEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setSlug(domain.getSlug());
        entity.setActivo(domain.isActivo());
        entity.setOrden(domain.getOrden());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
