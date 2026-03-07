package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.AdjuntoClinico;
import com.akine_api.infrastructure.persistence.entity.AdjuntoClinicoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdjuntoClinicoEntityMapper {

    default AdjuntoClinico toDomain(AdjuntoClinicoEntity entity) {
        if (entity == null) return null;
        return new AdjuntoClinico(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getPacienteId(),
                entity.getSesionId(),
                entity.getStorageKey(),
                entity.getOriginalFilename(),
                entity.getContentType(),
                entity.getSizeBytes(),
                entity.getCreatedByUserId(),
                entity.getCreatedAt()
        );
    }

    default AdjuntoClinicoEntity toEntity(AdjuntoClinico domain) {
        if (domain == null) return null;
        AdjuntoClinicoEntity entity = new AdjuntoClinicoEntity();
        entity.setId(domain.getId());
        entity.setConsultorioId(domain.getConsultorioId());
        entity.setPacienteId(domain.getPacienteId());
        entity.setSesionId(domain.getSesionId());
        entity.setStorageKey(domain.getStorageKey());
        entity.setOriginalFilename(domain.getOriginalFilename());
        entity.setContentType(domain.getContentType());
        entity.setSizeBytes(domain.getSizeBytes());
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
