package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.ConsultorioFeriado;
import com.akine_api.infrastructure.persistence.entity.ConsultorioFeriadoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsultorioFeriadoEntityMapper {

    default ConsultorioFeriado toDomain(ConsultorioFeriadoEntity entity) {
        if (entity == null) return null;
        return new ConsultorioFeriado(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getFecha(),
                entity.getDescripcion(),
                entity.getCreatedAt()
        );
    }

    default ConsultorioFeriadoEntity toEntity(ConsultorioFeriado domain) {
        if (domain == null) return null;
        ConsultorioFeriadoEntity e = new ConsultorioFeriadoEntity();
        e.setId(domain.getId());
        e.setConsultorioId(domain.getConsultorioId());
        e.setFecha(domain.getFecha());
        e.setDescripcion(domain.getDescripcion());
        e.setCreatedAt(domain.getCreatedAt());
        return e;
    }
}
