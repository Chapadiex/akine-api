package com.akine_api.infrastructure.persistence.mapper.cobro;

import com.akine_api.domain.model.cobro.PagoObraSocial;
import com.akine_api.infrastructure.persistence.entity.cobro.PagoObraSocialEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PagoObraSocialEntityMapper {
    PagoObraSocial toDomain(PagoObraSocialEntity entity);
    PagoObraSocialEntity toEntity(PagoObraSocial domain);
}
