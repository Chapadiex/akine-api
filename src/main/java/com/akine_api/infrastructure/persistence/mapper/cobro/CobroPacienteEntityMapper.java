package com.akine_api.infrastructure.persistence.mapper.cobro;

import com.akine_api.domain.model.cobro.CobroPaciente;
import com.akine_api.domain.model.cobro.CobroPacienteDetalle;
import com.akine_api.infrastructure.persistence.entity.cobro.CobroPacienteDetalleEntity;
import com.akine_api.infrastructure.persistence.entity.cobro.CobroPacienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CobroPacienteEntityMapper {
    CobroPaciente toDomain(CobroPacienteEntity entity);
    CobroPacienteEntity toEntity(CobroPaciente domain);
    CobroPacienteDetalle detalleToDomaain(CobroPacienteDetalleEntity entity);
    CobroPacienteDetalleEntity detalleToEntity(CobroPacienteDetalle domain);
}
