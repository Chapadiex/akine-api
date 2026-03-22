package com.akine_api.infrastructure.persistence.mapper.cobro;

import com.akine_api.domain.model.cobro.LiquidacionSesion;
import com.akine_api.infrastructure.persistence.entity.cobro.LiquidacionSesionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LiquidacionSesionEntityMapper {
    LiquidacionSesion toDomain(LiquidacionSesionEntity entity);
    LiquidacionSesionEntity toEntity(LiquidacionSesion domain);
}
