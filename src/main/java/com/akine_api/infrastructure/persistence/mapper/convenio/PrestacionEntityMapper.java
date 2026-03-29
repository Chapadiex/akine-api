package com.akine_api.infrastructure.persistence.mapper.convenio;

import com.akine_api.domain.model.convenio.Prestacion;
import com.akine_api.infrastructure.persistence.entity.convenio.PrestacionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PrestacionEntityMapper {
    Prestacion toDomain(PrestacionEntity e);
    PrestacionEntity toEntity(Prestacion d);
}
