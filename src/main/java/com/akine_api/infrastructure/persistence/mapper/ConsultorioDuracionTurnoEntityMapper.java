package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.ConsultorioDuracionTurno;
import com.akine_api.infrastructure.persistence.entity.ConsultorioDuracionTurnoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsultorioDuracionTurnoEntityMapper {

    default ConsultorioDuracionTurno toDomain(ConsultorioDuracionTurnoEntity entity) {
        if (entity == null) return null;
        return new ConsultorioDuracionTurno(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getMinutos()
        );
    }

    default ConsultorioDuracionTurnoEntity toEntity(ConsultorioDuracionTurno domain) {
        if (domain == null) return null;
        ConsultorioDuracionTurnoEntity e = new ConsultorioDuracionTurnoEntity();
        e.setId(domain.getId());
        e.setConsultorioId(domain.getConsultorioId());
        e.setMinutos(domain.getMinutos());
        return e;
    }
}
