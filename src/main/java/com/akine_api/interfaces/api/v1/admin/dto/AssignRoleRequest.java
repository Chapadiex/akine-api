package com.akine_api.interfaces.api.v1.admin.dto;

import com.akine_api.domain.model.RoleName;
import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(@NotNull RoleName roleName) {}
