package com.akine_api.application.mapper.facturacion;

import com.akine_api.application.dto.facturacion.LotePresentacionDTO;
import com.akine_api.domain.model.facturacion.LotePresentacion;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LotePresentacionDTOMapper {
    LotePresentacionDTOMapper INSTANCE = Mappers.getMapper(LotePresentacionDTOMapper.class);

    LotePresentacionDTO toDto(LotePresentacion domain);
    LotePresentacion toDomain(LotePresentacionDTO dto);
}
