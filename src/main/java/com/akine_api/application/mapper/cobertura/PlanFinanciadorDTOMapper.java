package com.akine_api.application.mapper.cobertura;

import com.akine_api.application.dto.cobertura.PlanFinanciadorDTO;
import com.akine_api.domain.model.cobertura.PlanFinanciador;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlanFinanciadorDTOMapper {
    PlanFinanciadorDTOMapper INSTANCE = Mappers.getMapper(PlanFinanciadorDTOMapper.class);

    PlanFinanciadorDTO toDto(PlanFinanciador domain);
    PlanFinanciador toDomain(PlanFinanciadorDTO dto);
}
