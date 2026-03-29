package com.akine_api.infrastructure.persistence.mapper.convenio;

import com.akine_api.domain.model.convenio.Convenio;
import com.akine_api.infrastructure.persistence.entity.convenio.ConvenioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConvenioEntityMapper {
    @Mapping(source = "financiador.id", target = "financiadorId")
    Convenio toDomain(ConvenioEntity e);

    @Mapping(source = "financiadorId", target = "financiador.id")
    ConvenioEntity toEntity(Convenio d);
}
