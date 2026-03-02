package com.akine_api.application.service;

import com.akine_api.application.dto.command.AssignRoleCommand;
import com.akine_api.application.dto.result.UserSummaryResult;
import com.akine_api.application.port.output.RoleRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.RoleNotFoundException;
import com.akine_api.domain.exception.UserNotFoundException;
import com.akine_api.domain.model.Role;
import com.akine_api.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminUserService {

    private final UserRepositoryPort userRepo;
    private final RoleRepositoryPort roleRepo;

    public AdminUserService(UserRepositoryPort userRepo, RoleRepositoryPort roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResult> listUsers(int page, int size) {
        return userRepo.findAll(page, size).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepo.count();
    }

    public UserSummaryResult assignRole(AssignRoleCommand cmd) {
        User user = userRepo.findById(cmd.targetUserId())
                .orElseThrow(() -> new UserNotFoundException(cmd.targetUserId().toString()));
        Role role = roleRepo.findByName(cmd.roleName())
                .orElseThrow(() -> new RoleNotFoundException(cmd.roleName().name()));
        user.addRole(role);
        User updated = userRepo.save(user);
        return toSummary(updated);
    }

    private UserSummaryResult toSummary(User user) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());
        return new UserSummaryResult(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus().name(),
                roles
        );
    }
}
