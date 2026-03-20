package com.akine_api.infrastructure.persistence.mapper.facturacion;

import com.akine_api.domain.model.facturacion.LiquidacionAjuste;
import com.akine_api.infrastructure.persistence.entity.facturacion.LiquidacionAjusteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LiquidacionAjusteEntityMapper {
    LiquidacionAjusteEntityMapper INSTANCE = Mappers.getMapper(LiquidacionAjusteEntityMapper.class);

    @Mapping(source = "liquidacion.id", target = "liquidacionId")
    @Mapping(source = "loteItem.id", target = "loteItemId")
    LiquidacionAjuste toDomain(LiquidacionAjusteEntity entity);

    @Mapping(source = "liquidacionId", target = "liquidacion.id")
    @Mapping(source = "loteItemId", target = "loteItem.id")
    LiquidacionAjusteEntity toEntity(LiquidacionAjuste domain);
}
