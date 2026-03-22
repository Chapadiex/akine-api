package com.akine_api.infrastructure.persistence.mapper.cobro;

import com.akine_api.domain.model.cobro.LoteFacturacionOs;
import com.akine_api.domain.model.cobro.LoteFacturacionOsDetalle;
import com.akine_api.infrastructure.persistence.entity.cobro.LoteFacturacionOsDetalleEntity;
import com.akine_api.infrastructure.persistence.entity.cobro.LoteFacturacionOsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoteFacturacionOsEntityMapper {
    LoteFacturacionOs toDomain(LoteFacturacionOsEntity entity);
    LoteFacturacionOsEntity toEntity(LoteFacturacionOs domain);
    LoteFacturacionOsDetalle toDetalleDomain(LoteFacturacionOsDetalleEntity entity);
    LoteFacturacionOsDetalleEntity toDetalleEntity(LoteFacturacionOsDetalle domain);
}
