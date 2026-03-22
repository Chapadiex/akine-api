package com.akine_api.infrastructure.persistence.mapper.cobro;

import com.akine_api.domain.model.cobro.AuditoriaEvento;
import com.akine_api.infrastructure.persistence.entity.cobro.AuditoriaEventoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditoriaEventoEntityMapper {
    AuditoriaEvento toDomain(AuditoriaEventoEntity entity);
    AuditoriaEventoEntity toEntity(AuditoriaEvento domain);
}
