package com.akine_api.infrastructure.persistence.mapper.facturacion;

import com.akine_api.domain.model.facturacion.AtencionFacturable;
import com.akine_api.infrastructure.persistence.entity.facturacion.AtencionFacturableEntity;
import com.akine_api.infrastructure.persistence.mapper.cobertura.FinanciadorSaludEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ConvenioFinanciadorEntityMapper.class, PrestacionArancelableEntityMapper.class})
public interface AtencionFacturableEntityMapper {
    AtencionFacturableEntityMapper INSTANCE = Mappers.getMapper(AtencionFacturableEntityMapper.class);

    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "convenio.id", target = "convenioId")
    @Mapping(source = "prestacion.id", target = "prestacionId")
    AtencionFacturable toDomain(AtencionFacturableEntity entity);

    @Mapping(source = "pacienteId", target = "paciente.id")
    @Mapping(source = "convenioId", target = "convenio.id")
    @Mapping(source = "prestacionId", target = "prestacion.id")
    AtencionFacturableEntity toEntity(AtencionFacturable domain);
}
