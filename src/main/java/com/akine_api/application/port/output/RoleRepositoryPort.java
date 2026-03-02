package com.akine_api.application.port.output;

import com.akine_api.domain.model.Role;
import com.akine_api.domain.model.RoleName;

import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findByName(RoleName name);
}
