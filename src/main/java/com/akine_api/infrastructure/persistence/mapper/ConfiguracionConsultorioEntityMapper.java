package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.ConfiguracionConsultorio;
import com.akine_api.infrastructure.persistence.entity.ConfiguracionConsultorioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfiguracionConsultorioEntityMapper {
    ConfiguracionConsultorio toDomain(ConfiguracionConsultorioEntity entity);
    ConfiguracionConsultorioEntity toEntity(ConfiguracionConsultorio domain);
}
