package com.akine_api.interfaces.api.v1.auth;

import com.akine_api.application.dto.command.*;
import com.akine_api.application.dto.result.AuthResult;
import com.akine_api.application.dto.result.TokenPairResult;
import com.akine_api.application.service.AuthenticationService;
import com.akine_api.application.service.UserRegistrationService;
import com.akine_api.interfaces.api.v1.auth.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRegistrationService registrationService;
    private final AuthenticationService authService;

    public AuthController(UserRegistrationService registrationService,
                          AuthenticationService authService) {
        this.registrationService = registrationService;
        this.authService = authService;
    }

    @PostMapping("/register/patient")
    public ResponseEntity<Void> registerPatient(@Valid @RequestBody RegisterPatientRequest req) {
        registrationService.registerPatient(new RegisterPatientCommand(
                req.email(), req.password(), req.firstName(), req.lastName(), req.phone()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register/professional")
    public ResponseEntity<Void> registerProfessional(@Valid @RequestBody RegisterProfessionalRequest req) {
        registrationService.registerProfessional(new RegisterProfessionalCommand(
                req.email(), req.password(), req.firstName(), req.lastName(), req.phone(),
                req.consultorioName(), req.consultorioAddress(), req.consultorioPhone()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activate(@Valid @RequestBody ActivateAccountRequest req) {
        registrationService.activateAccount(req.token());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/activate-with-password")
    public ResponseEntity<Void> activateWithPassword(@Valid @RequestBody ActivateWithPasswordRequest req) {
        registrationService.activateAccountWithPassword(req.token(), req.newPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject-activation")
    public ResponseEntity<Void> rejectActivation(@Valid @RequestBody RejectActivationRequest req) {
        registrationService.rejectActivation(req.token());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-activation")
    public ResponseEntity<Void> resendActivation(@Valid @RequestBody ResendActivationRequest req) {
        registrationService.resendActivation(req.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        AuthResult result = authService.login(new LoginCommand(req.email(), req.password()));
        return ResponseEntity.ok(toAuthResponse(result));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        TokenPairResult result = authService.refresh(req.refreshToken());
        return ResponseEntity.ok(new TokenRefreshResponse(
                result.accessToken(), result.refreshToken(), "Bearer", result.expiresInMs()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest req) {
        authService.logout(req.refreshToken());
        return ResponseEntity.noContent().build();
    }

    private AuthResponse toAuthResponse(AuthResult r) {
        return new AuthResponse(
                r.accessToken(),
                r.refreshToken(),
                "Bearer",
                r.expiresInMs(),
                new AuthResponse.UserInfo(
                        r.userId(), r.email(), r.firstName(), r.lastName(),
                        r.roles(), r.consultorioIds(), r.profesionalId()
                )
        );
    }
}
