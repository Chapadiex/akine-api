package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.HistorialEstadoTurno;
import com.akine_api.infrastructure.persistence.entity.HistorialEstadoTurnoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistorialEstadoTurnoEntityMapper {

    default HistorialEstadoTurno toDomain(HistorialEstadoTurnoEntity entity) {
        if (entity == null) return null;
        return new HistorialEstadoTurno(
                entity.getId(),
                entity.getTurnoId(),
                entity.getEstadoAnterior(),
                entity.getEstadoNuevo(),
                entity.getCambiadoPorUserId(),
                entity.getMotivo(),
                entity.getCreatedAt()
        );
    }

    default HistorialEstadoTurnoEntity toEntity(HistorialEstadoTurno domain) {
        if (domain == null) return null;
        HistorialEstadoTurnoEntity e = new HistorialEstadoTurnoEntity();
        e.setId(domain.getId());
        e.setTurnoId(domain.getTurnoId());
        e.setEstadoAnterior(domain.getEstadoAnterior());
        e.setEstadoNuevo(domain.getEstadoNuevo());
        e.setCambiadoPorUserId(domain.getCambiadoPorUserId());
        e.setMotivo(domain.getMotivo());
        e.setCreatedAt(domain.getCreatedAt());
        return e;
    }
}
