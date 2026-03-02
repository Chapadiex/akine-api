package com.akine_api.interfaces.api.v1.admin;

import com.akine_api.application.dto.command.AssignRoleCommand;
import com.akine_api.application.dto.result.UserSummaryResult;
import com.akine_api.application.service.AdminUserService;
import com.akine_api.interfaces.api.v1.admin.dto.AssignRoleRequest;
import com.akine_api.interfaces.api.v1.admin.dto.PagedUserListResponse;
import com.akine_api.interfaces.api.v1.admin.dto.UserSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminUserService adminUserService;

    public AdminController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/users")
    public ResponseEntity<PagedUserListResponse> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<UserSummaryResponse> content = adminUserService.listUsers(page, size)
                .stream().map(this::toResponse).collect(Collectors.toList());
        long total = adminUserService.countUsers();

        return ResponseEntity.ok(new PagedUserListResponse(content, page, size, total));
    }

    @PostMapping("/users/{id}/roles")
    public ResponseEntity<UserSummaryResponse> assignRole(
            @PathVariable UUID id,
            @Valid @RequestBody AssignRoleRequest req) {

        UserSummaryResult result = adminUserService.assignRole(
                new AssignRoleCommand(id, req.roleName())
        );
        return ResponseEntity.ok(toResponse(result));
    }

    private UserSummaryResponse toResponse(UserSummaryResult r) {
        return new UserSummaryResponse(
                r.id(), r.email(), r.firstName(), r.lastName(), r.status(), r.roles()
        );
    }
}
