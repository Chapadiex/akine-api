package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.PlanTratamientoDetalle;
import com.akine_api.infrastructure.persistence.entity.PlanTratamientoDetalleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanTratamientoDetalleEntityMapper {

    default PlanTratamientoDetalle toDomain(PlanTratamientoDetalleEntity entity) {
        if (entity == null) return null;
        return new PlanTratamientoDetalle(
                entity.getId(),
                entity.getPlanTerapeuticoId(),
                entity.getTratamientoId(),
                entity.getTratamientoNombreSnapshot(),
                entity.getTratamientoCategoriaCodigoSnapshot(),
                entity.getTratamientoCategoriaNombreSnapshot(),
                entity.getTratamientoTipoSnapshot(),
                Boolean.TRUE.equals(entity.getTratamientoRequiereAutorizacionSnapshot()),
                Boolean.TRUE.equals(entity.getTratamientoRequierePrescripcionMedicaSnapshot()),
                entity.getTratamientoDuracionSugeridaMinutosSnapshot(),
                entity.getCantidadSesiones(),
                entity.getFrecuenciaSugerida(),
                entity.getCaracterCaso(),
                entity.getFechaEstimadaInicio(),
                entity.isRequiereAutorizacion(),
                entity.getObservaciones(),
                entity.getObservacionesAdministrativas(),
                entity.getOrderIndex(),
                entity.getCreatedAt()
        );
    }

    default PlanTratamientoDetalleEntity toEntity(PlanTratamientoDetalle domain) {
        if (domain == null) return null;
        PlanTratamientoDetalleEntity entity = new PlanTratamientoDetalleEntity();
        entity.setId(domain.getId());
        entity.setPlanTerapeuticoId(domain.getPlanTerapeuticoId());
        entity.setTratamientoId(domain.getTratamientoId());
        entity.setTratamientoNombreSnapshot(domain.getTratamientoNombreSnapshot());
        entity.setTratamientoCategoriaCodigoSnapshot(domain.getTratamientoCategoriaCodigoSnapshot());
        entity.setTratamientoCategoriaNombreSnapshot(domain.getTratamientoCategoriaNombreSnapshot());
        entity.setTratamientoTipoSnapshot(domain.getTratamientoTipoSnapshot());
        entity.setTratamientoRequiereAutorizacionSnapshot(domain.isTratamientoRequiereAutorizacionSnapshot());
        entity.setTratamientoRequierePrescripcionMedicaSnapshot(domain.isTratamientoRequierePrescripcionMedicaSnapshot());
        entity.setTratamientoDuracionSugeridaMinutosSnapshot(domain.getTratamientoDuracionSugeridaMinutosSnapshot());
        entity.setCantidadSesiones(domain.getCantidadSesiones());
        entity.setFrecuenciaSugerida(domain.getFrecuenciaSugerida());
        entity.setCaracterCaso(domain.getCaracterCaso());
        entity.setFechaEstimadaInicio(domain.getFechaEstimadaInicio());
        entity.setRequiereAutorizacion(domain.isRequiereAutorizacion());
        entity.setObservaciones(domain.getObservaciones());
        entity.setObservacionesAdministrativas(domain.getObservacionesAdministrativas());
        entity.setOrderIndex(domain.getOrderIndex());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
