package com.akine_api.infrastructure.persistence.mapper.facturacion;

import com.akine_api.domain.model.facturacion.ConvenioPrestacionValor;
import com.akine_api.infrastructure.persistence.entity.facturacion.ConvenioPrestacionValorEntity;
import com.akine_api.infrastructure.persistence.mapper.cobertura.PlanFinanciadorEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ConvenioFinanciadorEntityMapper.class, PlanFinanciadorEntityMapper.class, PrestacionArancelableEntityMapper.class})
public interface ConvenioPrestacionValorEntityMapper {
    ConvenioPrestacionValorEntityMapper INSTANCE = Mappers.getMapper(ConvenioPrestacionValorEntityMapper.class);

    @Mapping(source = "convenio.id", target = "convenioId")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "prestacion.id", target = "prestacionId")
    ConvenioPrestacionValor toDomain(ConvenioPrestacionValorEntity entity);

    @Mapping(source = "convenioId", target = "convenio.id")
    @Mapping(source = "planId", target = "plan.id")
    @Mapping(source = "prestacionId", target = "prestacion.id")
    ConvenioPrestacionValorEntity toEntity(ConvenioPrestacionValor domain);
}
