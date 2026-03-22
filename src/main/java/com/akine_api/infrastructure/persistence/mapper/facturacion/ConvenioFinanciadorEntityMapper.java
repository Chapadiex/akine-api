package com.akine_api.infrastructure.persistence.mapper.facturacion;

import com.akine_api.domain.model.facturacion.ConvenioFinanciador;
import com.akine_api.infrastructure.persistence.entity.facturacion.ConvenioFinanciadorEntity;
import com.akine_api.infrastructure.persistence.mapper.cobertura.FinanciadorSaludEntityMapper;
import com.akine_api.infrastructure.persistence.mapper.cobertura.PlanFinanciadorEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {FinanciadorSaludEntityMapper.class, PlanFinanciadorEntityMapper.class})
public interface ConvenioFinanciadorEntityMapper {
    ConvenioFinanciadorEntityMapper INSTANCE = Mappers.getMapper(ConvenioFinanciadorEntityMapper.class);

    @Mapping(source = "financiador.id", target = "financiadorId")
    @Mapping(source = "plan.id", target = "planId")
    ConvenioFinanciador toDomain(ConvenioFinanciadorEntity entity);

    @Mapping(source = "financiadorId", target = "financiador.id")
    @Mapping(source = "planId", target = "plan.id")
    ConvenioFinanciadorEntity toEntity(ConvenioFinanciador domain);
}
