package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.DiagnosticoClinico;
import com.akine_api.infrastructure.persistence.entity.DiagnosticoClinicoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiagnosticoClinicoEntityMapper {

    default DiagnosticoClinico toDomain(DiagnosticoClinicoEntity entity) {
        if (entity == null) return null;
        return new DiagnosticoClinico(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getPacienteId(),
                entity.getProfesionalId(),
                entity.getSesionId(),
                entity.getCodigo(),
                entity.getDescripcion(),
                entity.getEstado(),
                entity.getFechaInicio(),
                entity.getFechaFin(),
                entity.getNotas(),
                entity.getCreatedByUserId(),
                entity.getUpdatedByUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default DiagnosticoClinicoEntity toEntity(DiagnosticoClinico domain) {
        if (domain == null) return null;
        DiagnosticoClinicoEntity entity = new DiagnosticoClinicoEntity();
        entity.setId(domain.getId());
        entity.setConsultorioId(domain.getConsultorioId());
        entity.setPacienteId(domain.getPacienteId());
        entity.setProfesionalId(domain.getProfesionalId());
        entity.setSesionId(domain.getSesionId());
        entity.setCodigo(domain.getCodigo());
        entity.setDescripcion(domain.getDescripcion());
        entity.setEstado(domain.getEstado());
        entity.setFechaInicio(domain.getFechaInicio());
        entity.setFechaFin(domain.getFechaFin());
        entity.setNotas(domain.getNotas());
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setUpdatedByUserId(domain.getUpdatedByUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
