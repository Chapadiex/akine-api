package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Empresa;
import com.akine_api.infrastructure.persistence.entity.EmpresaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmpresaEntityMapper {

    default Empresa toDomain(EmpresaEntity entity) {
        if (entity == null) return null;
        return new Empresa(
                entity.getId(),
                entity.getName(),
                entity.getCuit(),
                entity.getAddress(),
                entity.getCity(),
                entity.getProvince(),
                entity.getCreatedAt()
        );
    }

    default EmpresaEntity toEntity(Empresa domain) {
        if (domain == null) return null;
        EmpresaEntity entity = new EmpresaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCuit(domain.getCuit());
        entity.setAddress(domain.getAddress());
        entity.setCity(domain.getCity());
        entity.setProvince(domain.getProvince());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
