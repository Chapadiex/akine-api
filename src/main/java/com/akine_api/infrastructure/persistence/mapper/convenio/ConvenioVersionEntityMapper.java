package com.akine_api.infrastructure.persistence.mapper.convenio;

import com.akine_api.domain.model.convenio.ConvenioVersion;
import com.akine_api.infrastructure.persistence.entity.convenio.ConvenioVersionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConvenioVersionEntityMapper {
    @Mapping(source = "convenio.id", target = "convenioId")
    @Mapping(source = "createdAt", target = "creadoAt")
    ConvenioVersion toDomain(ConvenioVersionEntity e);

    @Mapping(source = "convenioId", target = "convenio.id")
    ConvenioVersionEntity toEntity(ConvenioVersion d);
}
