package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.SesionExamenFisico;
import com.akine_api.infrastructure.persistence.entity.SesionExamenFisicoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SesionExamenFisicoEntityMapper {

    default SesionExamenFisico toDomain(SesionExamenFisicoEntity entity) {
        if (entity == null) return null;
        return new SesionExamenFisico(
                entity.getId(),
                entity.getSesionId(),
                entity.getRangoMovimientoJson(),
                entity.getFuerzaMuscularJson(),
                entity.getFuncionalidadNota(),
                entity.getMarchaBalanceNota(),
                entity.getSignosInflamatorios(),
                entity.getObservacionesNeuroResp(),
                entity.getTestsMedidasJson(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default SesionExamenFisicoEntity toEntity(SesionExamenFisico domain) {
        if (domain == null) return null;
        SesionExamenFisicoEntity entity = new SesionExamenFisicoEntity();
        entity.setId(domain.getId());
        entity.setSesionId(domain.getSesionId());
        entity.setRangoMovimientoJson(domain.getRangoMovimientoJson());
        entity.setFuerzaMuscularJson(domain.getFuerzaMuscularJson());
        entity.setFuncionalidadNota(domain.getFuncionalidadNota());
        entity.setMarchaBalanceNota(domain.getMarchaBalanceNota());
        entity.setSignosInflamatorios(domain.getSignosInflamatorios());
        entity.setObservacionesNeuroResp(domain.getObservacionesNeuroResp());
        entity.setTestsMedidasJson(domain.getTestsMedidasJson());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
