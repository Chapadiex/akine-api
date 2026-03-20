package com.akine_api.application.mapper.facturacion;

import com.akine_api.application.dto.facturacion.ConvenioFinanciadorDTO;
import com.akine_api.domain.model.facturacion.ConvenioFinanciador;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConvenioFinanciadorDTOMapper {
    ConvenioFinanciadorDTOMapper INSTANCE = Mappers.getMapper(ConvenioFinanciadorDTOMapper.class);

    ConvenioFinanciadorDTO toDto(ConvenioFinanciador domain);
    ConvenioFinanciador toDomain(ConvenioFinanciadorDTO dto);
}
