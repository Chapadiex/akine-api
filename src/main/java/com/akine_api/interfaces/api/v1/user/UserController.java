package com.akine_api.interfaces.api.v1.user;

import com.akine_api.application.dto.command.ChangePasswordCommand;
import com.akine_api.application.dto.command.UpdateProfileCommand;
import com.akine_api.application.dto.result.UserContextResult;
import com.akine_api.application.dto.result.UserProfileResult;
import com.akine_api.application.service.UserProfileService;
import com.akine_api.infrastructure.persistence.repository.UserJpaRepository;
import com.akine_api.interfaces.api.v1.user.dto.ChangePasswordRequest;
import com.akine_api.interfaces.api.v1.user.dto.UpdateProfileRequest;
import com.akine_api.interfaces.api.v1.user.dto.UserContextResponse;
import com.akine_api.interfaces.api.v1.user.dto.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserProfileService profileService;
    private final UserJpaRepository userJpaRepository;

    public UserController(UserProfileService profileService,
                          UserJpaRepository userJpaRepository) {
        this.profileService = profileService;
        this.userJpaRepository = userJpaRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails principal) {
        UUID userId = resolveUserId(principal.getUsername());
        UserProfileResult result = profileService.getMyProfile(userId);
        return ResponseEntity.ok(toResponse(result));
    }

    @GetMapping("/me/context")
    public ResponseEntity<UserContextResponse> getMyContext(
            @AuthenticationPrincipal UserDetails principal) {
        UUID userId = resolveUserId(principal.getUsername());
        UserContextResult result = profileService.getMyContext(userId);
        return ResponseEntity.ok(toContextResponse(result));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody UpdateProfileRequest req) {
        UUID userId = resolveUserId(principal.getUsername());
        UserProfileResult result = profileService.updateMyProfile(
                new UpdateProfileCommand(userId, req.firstName(), req.lastName(), req.phone())
        );
        return ResponseEntity.ok(toResponse(result));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ChangePasswordRequest req) {
        UUID userId = resolveUserId(principal.getUsername());
        profileService.changePassword(
                new ChangePasswordCommand(userId, req.currentPassword(), req.newPassword())
        );
        return ResponseEntity.noContent().build();
    }

    private UUID resolveUserId(String email) {
        return userJpaRepository.findByEmail(email)
                .map(e -> e.getId())
                .orElseThrow(() -> new com.akine_api.domain.exception.UserNotFoundException(email));
    }

    private UserProfileResponse toResponse(UserProfileResult r) {
        return new UserProfileResponse(
                r.id(), r.email(), r.firstName(), r.lastName(),
                r.phone(), r.status(), r.roles(), r.consultorioIds()
        );
    }

    private UserContextResponse toContextResponse(UserContextResult r) {
        return new UserContextResponse(
                r.tipo(), r.matricula(), r.especialidades(),
                r.nroDocumento(), r.domicilio(), r.cargo(), r.dni()
        );
    }
}
