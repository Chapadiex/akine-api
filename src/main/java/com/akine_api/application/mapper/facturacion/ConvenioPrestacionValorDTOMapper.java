package com.akine_api.application.mapper.facturacion;

import com.akine_api.application.dto.facturacion.ConvenioPrestacionValorDTO;
import com.akine_api.domain.model.facturacion.ConvenioPrestacionValor;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConvenioPrestacionValorDTOMapper {
    ConvenioPrestacionValorDTOMapper INSTANCE = Mappers.getMapper(ConvenioPrestacionValorDTOMapper.class);

    ConvenioPrestacionValorDTO toDto(ConvenioPrestacionValor domain);
    ConvenioPrestacionValor toDomain(ConvenioPrestacionValorDTO dto);
}
