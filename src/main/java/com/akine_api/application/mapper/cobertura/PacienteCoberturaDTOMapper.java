package com.akine_api.application.mapper.cobertura;

import com.akine_api.application.dto.cobertura.PacienteCoberturaDTO;
import com.akine_api.domain.model.cobertura.PacienteCobertura;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PacienteCoberturaDTOMapper {
    PacienteCoberturaDTOMapper INSTANCE = Mappers.getMapper(PacienteCoberturaDTOMapper.class);

    PacienteCoberturaDTO toDto(PacienteCobertura domain);
    PacienteCobertura toDomain(PacienteCoberturaDTO dto);
}
