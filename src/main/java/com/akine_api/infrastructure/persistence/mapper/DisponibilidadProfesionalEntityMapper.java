package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.DisponibilidadProfesional;
import com.akine_api.infrastructure.persistence.entity.DisponibilidadProfesionalEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DisponibilidadProfesionalEntityMapper {

    default DisponibilidadProfesional toDomain(DisponibilidadProfesionalEntity entity) {
        if (entity == null) return null;
        return new DisponibilidadProfesional(
                entity.getId(),
                entity.getProfesionalId(),
                entity.getConsultorioId(),
                entity.getDiaSemana(),
                entity.getHoraInicio(),
                entity.getHoraFin(),
                entity.isActivo()
        );
    }

    default DisponibilidadProfesionalEntity toEntity(DisponibilidadProfesional domain) {
        if (domain == null) return null;
        DisponibilidadProfesionalEntity e = new DisponibilidadProfesionalEntity();
        e.setId(domain.getId());
        e.setProfesionalId(domain.getProfesionalId());
        e.setConsultorioId(domain.getConsultorioId());
        e.setDiaSemana(domain.getDiaSemana());
        e.setHoraInicio(domain.getHoraInicio());
        e.setHoraFin(domain.getHoraFin());
        e.setActivo(domain.isActivo());
        return e;
    }
}
