package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.AtencionInicialEvaluacion;
import com.akine_api.infrastructure.persistence.entity.AtencionInicialEvaluacionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AtencionInicialEvaluacionEntityMapper {

    default AtencionInicialEvaluacion toDomain(AtencionInicialEvaluacionEntity entity) {
        if (entity == null) return null;
        return new AtencionInicialEvaluacion(
                entity.getId(),
                entity.getAtencionInicialId(),
                entity.getPeso(),
                entity.getAltura(),
                entity.getImc(),
                entity.getPresionArterial(),
                entity.getFrecuenciaCardiaca(),
                entity.getSaturacion(),
                entity.getTemperatura(),
                entity.getObservaciones(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default AtencionInicialEvaluacionEntity toEntity(AtencionInicialEvaluacion domain) {
        if (domain == null) return null;
        AtencionInicialEvaluacionEntity entity = new AtencionInicialEvaluacionEntity();
        entity.setId(domain.getId());
        entity.setAtencionInicialId(domain.getAtencionInicialId());
        entity.setPeso(domain.getPeso());
        entity.setAltura(domain.getAltura());
        entity.setImc(domain.getImc());
        entity.setPresionArterial(domain.getPresionArterial());
        entity.setFrecuenciaCardiaca(domain.getFrecuenciaCardiaca());
        entity.setSaturacion(domain.getSaturacion());
        entity.setTemperatura(domain.getTemperatura());
        entity.setObservaciones(domain.getObservaciones());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
