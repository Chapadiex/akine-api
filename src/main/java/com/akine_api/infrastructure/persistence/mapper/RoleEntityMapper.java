package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Role;
import com.akine_api.domain.model.RoleName;
import com.akine_api.infrastructure.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleEntityMapper {

    default Role toDomain(RoleEntity entity) {
        if (entity == null) return null;
        return new Role(entity.getId(), RoleName.valueOf(entity.getName()), entity.getDescription());
    }

    @Mapping(target = "name", expression = "java(domain.getName().name())")
    RoleEntity toEntity(Role domain);
}
