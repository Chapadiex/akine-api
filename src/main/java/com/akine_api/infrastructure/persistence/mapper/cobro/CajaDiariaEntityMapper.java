package com.akine_api.infrastructure.persistence.mapper.cobro;

import com.akine_api.domain.model.cobro.CajaDiaria;
import com.akine_api.infrastructure.persistence.entity.cobro.CajaDiariaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CajaDiariaEntityMapper {
    CajaDiaria toDomain(CajaDiariaEntity entity);
    CajaDiariaEntity toEntity(CajaDiaria domain);
}
