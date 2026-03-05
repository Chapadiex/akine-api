package com.akine_api.service;

import com.akine_api.application.dto.command.LoginCommand;
import com.akine_api.application.dto.result.AuthResult;
import com.akine_api.application.port.output.*;
import com.akine_api.application.service.AuthenticationService;
import com.akine_api.application.service.SuscripcionService;
import com.akine_api.domain.exception.InvalidCredentialsException;
import com.akine_api.domain.exception.InvalidTokenException;
import com.akine_api.domain.exception.UserNotActiveException;
import com.akine_api.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock UserRepositoryPort userRepo;
    @Mock RefreshTokenRepositoryPort refreshTokenRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock PasswordEncoderPort passwordEncoder;
    @Mock TokenGeneratorPort tokenGenerator;
    @Mock SuscripcionService suscripcionService;

    AuthenticationService service;

    @BeforeEach
    void setUp() {
        service = new AuthenticationService(userRepo, refreshTokenRepo, consultorioRepo,
                profesionalRepo, passwordEncoder, tokenGenerator, suscripcionService);
        ReflectionTestUtils.setField(service, "accessTokenExpirationMs", 900000L);
        ReflectionTestUtils.setField(service, "refreshTokenExpirationDays", 30L);
    }

    private User activeUser() {
        User u = new User(UUID.randomUUID(), "u@test.com", "hashed",
                "Carlos", "G", null, UserStatus.PENDING, Instant.now());
        u.activate();
        u.addRole(new Role(UUID.randomUUID(), RoleName.PACIENTE, "Paciente"));
        return u;
    }

    @Test
    void login_validCredentials_returnsAuthResult() {
        User user = activeUser();
        when(userRepo.findByEmail("u@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass1234!", user.getPasswordHash())).thenReturn(true);
        when(consultorioRepo.findConsultorioIdsByUserId(user.getId())).thenReturn(List.of());
        when(tokenGenerator.generateAccessToken(any(), any())).thenReturn("access-token");
        when(tokenGenerator.generateRefreshToken()).thenReturn("refresh-token");
        when(refreshTokenRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthResult result = service.login(new LoginCommand("u@test.com", "Pass1234!"));

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        verify(refreshTokenRepo).save(any(RefreshToken.class));
    }

    @Test
    void login_wrongPassword_throwsInvalidCredentials() {
        User user = activeUser();
        when(userRepo.findByEmail("u@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", user.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginCommand("u@test.com", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_profesionalAdminWithoutVigentConsultorios_returnsPendingAccountState() {
        User user = new User(UUID.randomUUID(), "owner@test.com", "hashed",
                "Owner", "One", null, UserStatus.PENDING, Instant.now());
        user.activate();
        user.addRole(new Role(UUID.randomUUID(), RoleName.PROFESIONAL_ADMIN, "Profesional admin"));

        when(userRepo.findByEmail("owner@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass1234!", user.getPasswordHash())).thenReturn(true);
        when(consultorioRepo.findConsultorioIdsByUserId(user.getId())).thenReturn(List.of());
        when(suscripcionService.findLatestByOwnerUserId(user.getId())).thenReturn(Optional.empty());
        when(tokenGenerator.generateAccessToken(any(), any())).thenReturn("access-token");
        when(tokenGenerator.generateRefreshToken()).thenReturn("refresh-token");
        when(refreshTokenRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthResult result = service.login(new LoginCommand("owner@test.com", "Pass1234!"));
        assertThat(result.accountState()).isEqualTo("REJECTED");
    }

    @Test
    void login_pendingUser_throwsUserNotActive() {
        User pendingUser = new User(UUID.randomUUID(), "p@test.com", "hashed",
                "A", "B", null, UserStatus.PENDING, Instant.now());
        when(userRepo.findByEmail("p@test.com")).thenReturn(Optional.of(pendingUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.login(new LoginCommand("p@test.com", "Pass1234!")))
                .isInstanceOf(UserNotActiveException.class);
    }

    @Test
    void login_userNotFound_throwsInvalidCredentials() {
        when(userRepo.findByEmail("none@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginCommand("none@test.com", "Pass1234!")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void refresh_revokedToken_throwsInvalidToken() {
        String raw = "revoked-token";
        String hash = hashToken(raw);
        var revokedToken = new RefreshToken(UUID.randomUUID(), UUID.randomUUID(), hash,
                Instant.now().plusSeconds(3600), true, Instant.now());
        when(refreshTokenRepo.findByTokenHash(hash)).thenReturn(Optional.of(revokedToken));

        assertThatThrownBy(() -> service.refresh(raw))
                .isInstanceOf(InvalidTokenException.class);

        verify(refreshTokenRepo).revokeAllByUserId(revokedToken.getUserId());
    }

    @Test
    void logout_revokesToken() {
        String raw = "logout-token";
        String hash = hashToken(raw);
        var token = new RefreshToken(UUID.randomUUID(), UUID.randomUUID(), hash,
                Instant.now().plusSeconds(3600), false, Instant.now());
        when(refreshTokenRepo.findByTokenHash(hash)).thenReturn(Optional.of(token));
        when(refreshTokenRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.logout(raw);

        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepo).save(token);
    }

    private String hashToken(String raw) {
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            var sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
