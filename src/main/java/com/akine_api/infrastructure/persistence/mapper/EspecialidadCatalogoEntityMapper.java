package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.EspecialidadCatalogo;
import com.akine_api.infrastructure.persistence.entity.EspecialidadCatalogoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EspecialidadCatalogoEntityMapper {

    default EspecialidadCatalogo toDomain(EspecialidadCatalogoEntity entity) {
        if (entity == null) return null;
        return new EspecialidadCatalogo(
                entity.getId(),
                entity.getNombre(),
                entity.getSlug(),
                entity.isActivo(),
                entity.getCreatedAt()
        );
    }

    default EspecialidadCatalogoEntity toEntity(EspecialidadCatalogo domain) {
        if (domain == null) return null;
        EspecialidadCatalogoEntity e = new EspecialidadCatalogoEntity();
        e.setId(domain.getId());
        e.setNombre(domain.getNombre());
        e.setSlug(domain.getSlug());
        e.setActivo(domain.isActivo());
        e.setCreatedAt(domain.getCreatedAt());
        e.setUpdatedAt(domain.getUpdatedAt());
        return e;
    }
}
