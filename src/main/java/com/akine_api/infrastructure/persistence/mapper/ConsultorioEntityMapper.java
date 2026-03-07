package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Consultorio;
import com.akine_api.infrastructure.persistence.entity.ConsultorioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsultorioEntityMapper {

    default Consultorio toDomain(ConsultorioEntity entity) {
        if (entity == null) return null;
        return new Consultorio(
                entity.getId(),
                entity.getName(),
                entity.getCuit(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getMapLatitude(),
                entity.getMapLongitude(),
                entity.getGoogleMapsUrl(),
                entity.getStatus(),
                entity.getEmpresaId(),
                entity.getCreatedAt()
        );
    }

    default ConsultorioEntity toEntity(Consultorio domain) {
        if (domain == null) return null;
        ConsultorioEntity e = new ConsultorioEntity();
        e.setId(domain.getId());
        e.setName(domain.getName());
        e.setCuit(domain.getCuit());
        e.setAddress(domain.getAddress());
        e.setPhone(domain.getPhone());
        e.setEmail(domain.getEmail());
        e.setMapLatitude(domain.getMapLatitude());
        e.setMapLongitude(domain.getMapLongitude());
        e.setGoogleMapsUrl(domain.getGoogleMapsUrl());
        e.setEmpresaId(domain.getEmpresaId());
        e.setStatus(domain.getStatus());
        e.setCreatedAt(domain.getCreatedAt());
        e.setUpdatedAt(domain.getUpdatedAt());
        return e;
    }
}
