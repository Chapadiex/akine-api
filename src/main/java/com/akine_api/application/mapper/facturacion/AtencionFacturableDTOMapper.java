package com.akine_api.application.mapper.facturacion;

import com.akine_api.application.dto.facturacion.AtencionFacturableDTO;
import com.akine_api.domain.model.facturacion.AtencionFacturable;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AtencionFacturableDTOMapper {
    AtencionFacturableDTOMapper INSTANCE = Mappers.getMapper(AtencionFacturableDTOMapper.class);

    AtencionFacturableDTO toDto(AtencionFacturable domain);
    AtencionFacturable toDomain(AtencionFacturableDTO dto);
}
