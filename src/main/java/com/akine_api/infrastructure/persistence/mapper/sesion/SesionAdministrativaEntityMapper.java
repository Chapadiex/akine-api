package com.akine_api.infrastructure.persistence.mapper.sesion;

import com.akine_api.domain.model.sesion.SesionAdministrativa;
import com.akine_api.infrastructure.persistence.entity.sesion.SesionAdministrativaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SesionAdministrativaEntityMapper {
    SesionAdministrativa toDomain(SesionAdministrativaEntity entity);
    SesionAdministrativaEntity toEntity(SesionAdministrativa domain);
}
