package com.akine_api.application.mapper;

import com.akine_api.application.dto.ConfiguracionConsultorioDTO;
import com.akine_api.domain.model.ConfiguracionConsultorio;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfiguracionConsultorioDTOMapper {
    ConfiguracionConsultorioDTO toDto(ConfiguracionConsultorio domain);
    ConfiguracionConsultorio toDomain(ConfiguracionConsultorioDTO dto);
}
