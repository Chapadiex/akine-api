package com.akine_api.application.mapper.cobertura;

import com.akine_api.application.dto.cobertura.FinanciadorSaludDTO;
import com.akine_api.domain.model.cobertura.FinanciadorSalud;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FinanciadorSaludDTOMapper {
    FinanciadorSaludDTOMapper INSTANCE = Mappers.getMapper(FinanciadorSaludDTOMapper.class);

    FinanciadorSaludDTO toDto(FinanciadorSalud domain);
    FinanciadorSalud toDomain(FinanciadorSaludDTO dto);
}
