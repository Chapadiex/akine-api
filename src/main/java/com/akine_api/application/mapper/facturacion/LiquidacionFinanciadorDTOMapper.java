package com.akine_api.application.mapper.facturacion;

import com.akine_api.application.dto.facturacion.LiquidacionFinanciadorDTO;
import com.akine_api.domain.model.facturacion.LiquidacionFinanciador;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LiquidacionFinanciadorDTOMapper {
    LiquidacionFinanciadorDTOMapper INSTANCE = Mappers.getMapper(LiquidacionFinanciadorDTOMapper.class);

    LiquidacionFinanciadorDTO toDto(LiquidacionFinanciador domain);
    LiquidacionFinanciador toDomain(LiquidacionFinanciadorDTO dto);
}
