package com.akine_api.infrastructure.persistence.mapper.facturacion;

import com.akine_api.domain.model.facturacion.PrestacionArancelable;
import com.akine_api.infrastructure.persistence.entity.facturacion.PrestacionArancelableEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PrestacionArancelableEntityMapper {
    PrestacionArancelableEntityMapper INSTANCE = Mappers.getMapper(PrestacionArancelableEntityMapper.class);

    PrestacionArancelable toDomain(PrestacionArancelableEntity entity);
    PrestacionArancelableEntity toEntity(PrestacionArancelable domain);
}
