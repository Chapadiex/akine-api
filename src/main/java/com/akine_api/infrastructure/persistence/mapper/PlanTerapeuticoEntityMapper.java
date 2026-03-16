package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.PlanTerapeutico;
import com.akine_api.infrastructure.persistence.entity.PlanTerapeuticoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanTerapeuticoEntityMapper {

    default PlanTerapeutico toDomain(PlanTerapeuticoEntity entity) {
        if (entity == null) return null;
        return new PlanTerapeutico(
                entity.getId(),
                entity.getAtencionInicialId(),
                entity.getCasoAtencionId(),
                entity.getConsultorioId(),
                entity.getPacienteId(),
                entity.getProfesionalId(),
                entity.getEstado(),
                entity.getObservacionesGenerales(),
                entity.getCreatedByUserId(),
                entity.getUpdatedByUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default PlanTerapeuticoEntity toEntity(PlanTerapeutico domain) {
        if (domain == null) return null;
        PlanTerapeuticoEntity entity = new PlanTerapeuticoEntity();
        entity.setId(domain.getId());
        entity.setAtencionInicialId(domain.getAtencionInicialId());
        entity.setCasoAtencionId(domain.getCasoAtencionId());
        entity.setConsultorioId(domain.getConsultorioId());
        entity.setPacienteId(domain.getPacienteId());
        entity.setProfesionalId(domain.getProfesionalId());
        entity.setEstado(domain.getEstado());
        entity.setObservacionesGenerales(domain.getObservacionesGenerales());
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setUpdatedByUserId(domain.getUpdatedByUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
