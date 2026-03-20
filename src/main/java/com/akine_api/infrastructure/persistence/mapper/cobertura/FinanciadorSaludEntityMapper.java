package com.akine_api.infrastructure.persistence.mapper.cobertura;

import com.akine_api.domain.model.cobertura.FinanciadorSalud;
import com.akine_api.infrastructure.persistence.entity.cobertura.FinanciadorSaludEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FinanciadorSaludEntityMapper {
    FinanciadorSaludEntityMapper INSTANCE = Mappers.getMapper(FinanciadorSaludEntityMapper.class);

    FinanciadorSalud toDomain(FinanciadorSaludEntity entity);

    FinanciadorSaludEntity toEntity(FinanciadorSalud domain);
}
