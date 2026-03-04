package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Role;
import com.akine_api.domain.model.User;
import com.akine_api.domain.model.UserStatus;
import com.akine_api.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {RoleEntityMapper.class})
public interface UserEntityMapper {

    default User toDomain(UserEntity entity) {
        if (entity == null) return null;
        User user = new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhone(),
                UserStatus.valueOf(entity.getStatus()),
                entity.getDocumentoFiscal(),
                entity.getCreatedAt()
        );
        if (entity.getRoles() != null) {
            entity.getRoles().forEach(re ->
                    user.addRole(new com.akine_api.domain.model.Role(
                            re.getId(),
                            com.akine_api.domain.model.RoleName.valueOf(re.getName()),
                            re.getDescription()
                    ))
            );
        }
        return user;
    }

    @Mapping(target = "status", expression = "java(domain.getStatus().name())")
    @Mapping(target = "documentoFiscal", source = "documentoFiscal")
    @Mapping(target = "roles", ignore = true)
    UserEntity toEntity(User domain);
}
