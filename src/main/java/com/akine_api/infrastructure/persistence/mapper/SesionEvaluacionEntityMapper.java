package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.SesionEvaluacion;
import com.akine_api.infrastructure.persistence.entity.SesionEvaluacionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SesionEvaluacionEntityMapper {

    default SesionEvaluacion toDomain(SesionEvaluacionEntity entity) {
        if (entity == null) return null;
        return new SesionEvaluacion(
                entity.getId(),
                entity.getSesionId(),
                entity.getDolorIntensidad(),
                entity.getDolorZona(),
                entity.getDolorLateralidad(),
                entity.getDolorTipo(),
                entity.getDolorComportamiento(),
                entity.getEvolucionEstado(),
                entity.getEvolucionNota(),
                entity.getObjetivoSesion(),
                entity.getLimitacionFuncional(),
                entity.getRespuestaPaciente(),
                entity.getTolerancia(),
                entity.getIndicacionesDomiciliarias(),
                entity.getProximaConducta(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default SesionEvaluacionEntity toEntity(SesionEvaluacion domain) {
        if (domain == null) return null;
        SesionEvaluacionEntity entity = new SesionEvaluacionEntity();
        entity.setId(domain.getId());
        entity.setSesionId(domain.getSesionId());
        entity.setDolorIntensidad(domain.getDolorIntensidad());
        entity.setDolorZona(domain.getDolorZona());
        entity.setDolorLateralidad(domain.getDolorLateralidad());
        entity.setDolorTipo(domain.getDolorTipo());
        entity.setDolorComportamiento(domain.getDolorComportamiento());
        entity.setEvolucionEstado(domain.getEvolucionEstado());
        entity.setEvolucionNota(domain.getEvolucionNota());
        entity.setObjetivoSesion(domain.getObjetivoSesion());
        entity.setLimitacionFuncional(domain.getLimitacionFuncional());
        entity.setRespuestaPaciente(domain.getRespuestaPaciente());
        entity.setTolerancia(domain.getTolerancia());
        entity.setIndicacionesDomiciliarias(domain.getIndicacionesDomiciliarias());
        entity.setProximaConducta(domain.getProximaConducta());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
