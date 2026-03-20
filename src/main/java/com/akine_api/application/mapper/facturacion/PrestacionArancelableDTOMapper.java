package com.akine_api.application.mapper.facturacion;

import com.akine_api.application.dto.facturacion.PrestacionArancelableDTO;
import com.akine_api.domain.model.facturacion.PrestacionArancelable;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PrestacionArancelableDTOMapper {
    PrestacionArancelableDTOMapper INSTANCE = Mappers.getMapper(PrestacionArancelableDTOMapper.class);

    PrestacionArancelableDTO toDto(PrestacionArancelable domain);
    PrestacionArancelable toDomain(PrestacionArancelableDTO dto);
}
