package com.akine_api.infrastructure.persistence.mapper.cobertura;

import com.akine_api.domain.model.cobertura.PlanFinanciador;
import com.akine_api.infrastructure.persistence.entity.cobertura.PlanFinanciadorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {FinanciadorSaludEntityMapper.class})
public interface PlanFinanciadorEntityMapper {
    PlanFinanciadorEntityMapper INSTANCE = Mappers.getMapper(PlanFinanciadorEntityMapper.class);

    @Mapping(source = "financiador.id", target = "financiadorId")
    PlanFinanciador toDomain(PlanFinanciadorEntity entity);

    @Mapping(source = "financiadorId", target = "financiador.id")
    PlanFinanciadorEntity toEntity(PlanFinanciador domain);
}
