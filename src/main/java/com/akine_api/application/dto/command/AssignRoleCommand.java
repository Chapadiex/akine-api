package com.akine_api.application.dto.command;

import com.akine_api.domain.model.RoleName;

import java.util.UUID;

public record AssignRoleCommand(
        UUID targetUserId,
        RoleName roleName
) {}
