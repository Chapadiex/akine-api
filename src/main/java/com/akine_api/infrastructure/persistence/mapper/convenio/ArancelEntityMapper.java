package com.akine_api.infrastructure.persistence.mapper.convenio;

import com.akine_api.domain.model.convenio.Arancel;
import com.akine_api.infrastructure.persistence.entity.convenio.ArancelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArancelEntityMapper {
    @Mapping(source = "convenioVersion.id", target = "convenioVersionId")
    @Mapping(source = "prestacion.id", target = "prestacionId")
    Arancel toDomain(ArancelEntity e);

    @Mapping(source = "convenioVersionId", target = "convenioVersion.id")
    @Mapping(source = "prestacionId", target = "prestacion.id")
    ArancelEntity toEntity(Arancel d);
}
