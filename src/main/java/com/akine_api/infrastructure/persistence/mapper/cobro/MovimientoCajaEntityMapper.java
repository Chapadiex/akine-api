package com.akine_api.infrastructure.persistence.mapper.cobro;

import com.akine_api.domain.model.cobro.MovimientoCaja;
import com.akine_api.infrastructure.persistence.entity.cobro.MovimientoCajaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovimientoCajaEntityMapper {
    MovimientoCaja toDomain(MovimientoCajaEntity entity);
    MovimientoCajaEntity toEntity(MovimientoCaja domain);
}
