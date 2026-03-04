package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Suscripcion;
import com.akine_api.domain.model.SuscripcionStatus;
import com.akine_api.infrastructure.persistence.entity.SuscripcionEntity;
import org.mapstruct.Mapper;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface SuscripcionEntityMapper {

    default Suscripcion toDomain(SuscripcionEntity entity) {
        if (entity == null) return null;
        return new Suscripcion(
                entity.getId(),
                entity.getOwnerUserId(),
                entity.getEmpresaId(),
                entity.getConsultorioBaseId(),
                SuscripcionStatus.valueOf(entity.getStatus()),
                entity.getRequestedAt(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getReviewedAt(),
                entity.getReviewedByUserId(),
                entity.getRejectionReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default SuscripcionEntity toEntity(Suscripcion domain) {
        if (domain == null) return null;
        SuscripcionEntity entity = new SuscripcionEntity();
        entity.setId(domain.getId());
        entity.setOwnerUserId(domain.getOwnerUserId());
        entity.setEmpresaId(domain.getEmpresaId());
        entity.setConsultorioBaseId(domain.getConsultorioBaseId());
        entity.setStatus(domain.getStatus().name());
        entity.setRequestedAt(domain.getRequestedAt());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setReviewedAt(domain.getReviewedAt());
        entity.setReviewedByUserId(domain.getReviewedByUserId());
        entity.setRejectionReason(domain.getRejectionReason());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt() == null ? Instant.now() : domain.getUpdatedAt());
        return entity;
    }
}
