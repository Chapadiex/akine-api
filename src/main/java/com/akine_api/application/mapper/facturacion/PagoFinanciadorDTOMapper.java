package com.akine_api.application.mapper.facturacion;

import com.akine_api.application.dto.facturacion.PagoFinanciadorDTO;
import com.akine_api.domain.model.facturacion.PagoFinanciador;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PagoFinanciadorDTOMapper {
    PagoFinanciadorDTOMapper INSTANCE = Mappers.getMapper(PagoFinanciadorDTOMapper.class);

    PagoFinanciadorDTO toDto(PagoFinanciador domain);
    PagoFinanciador toDomain(PagoFinanciadorDTO dto);
}
