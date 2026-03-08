package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.HistoriaClinicaLegajo;
import com.akine_api.infrastructure.persistence.entity.HistoriaClinicaLegajoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistoriaClinicaLegajoEntityMapper {

    default HistoriaClinicaLegajo toDomain(HistoriaClinicaLegajoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new HistoriaClinicaLegajo(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getPacienteId(),
                entity.getCreatedByUserId(),
                entity.getUpdatedByUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default HistoriaClinicaLegajoEntity toEntity(HistoriaClinicaLegajo domain) {
        if (domain == null) {
            return null;
        }
        HistoriaClinicaLegajoEntity entity = new HistoriaClinicaLegajoEntity();
        entity.setId(domain.getId());
        entity.setConsultorioId(domain.getConsultorioId());
        entity.setPacienteId(domain.getPacienteId());
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setUpdatedByUserId(domain.getUpdatedByUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
