package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.ConsultorioHorario;
import com.akine_api.infrastructure.persistence.entity.ConsultorioHorarioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsultorioHorarioEntityMapper {

    default ConsultorioHorario toDomain(ConsultorioHorarioEntity entity) {
        if (entity == null) return null;
        return new ConsultorioHorario(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getDiaSemana(),
                entity.getHoraApertura(),
                entity.getHoraCierre(),
                entity.isActivo()
        );
    }

    default ConsultorioHorarioEntity toEntity(ConsultorioHorario domain) {
        if (domain == null) return null;
        ConsultorioHorarioEntity e = new ConsultorioHorarioEntity();
        e.setId(domain.getId());
        e.setConsultorioId(domain.getConsultorioId());
        e.setDiaSemana(domain.getDiaSemana());
        e.setHoraApertura(domain.getHoraApertura());
        e.setHoraCierre(domain.getHoraCierre());
        e.setActivo(domain.isActivo());
        return e;
    }
}
