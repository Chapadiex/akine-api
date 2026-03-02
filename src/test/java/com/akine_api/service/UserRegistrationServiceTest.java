package com.akine_api.service;

import com.akine_api.application.dto.command.RegisterPatientCommand;
import com.akine_api.application.port.output.*;
import com.akine_api.application.service.UserRegistrationService;
import com.akine_api.domain.exception.UserAlreadyExistsException;
import com.akine_api.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock UserRepositoryPort userRepo;
    @Mock RoleRepositoryPort roleRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock MembershipRepositoryPort membershipRepo;
    @Mock ActivationTokenRepositoryPort activationTokenRepo;
    @Mock PasswordEncoderPort passwordEncoder;
    @Mock EmailPort emailPort;
    @Mock TokenGeneratorPort tokenGenerator;
    @Mock RefreshTokenRepositoryPort refreshTokenRepo;

    UserRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new UserRegistrationService(userRepo, roleRepo, consultorioRepo, membershipRepo,
                activationTokenRepo, passwordEncoder, emailPort, tokenGenerator, refreshTokenRepo);
    }

    @Test
    void registerPatient_happyPath_savesUserAndSendsEmail() {
        // Arrange
        var cmd = new RegisterPatientCommand("p@test.com", "Pass1234!", "Ana", "Lopez", null);
        var role = new Role(UUID.randomUUID(), RoleName.PACIENTE, "Paciente");
        var savedUser = new User(UUID.randomUUID(), cmd.email(), "hashed",
                cmd.firstName(), cmd.lastName(), null, UserStatus.PENDING, Instant.now());

        when(userRepo.existsByEmail(cmd.email())).thenReturn(false);
        when(roleRepo.findByName(RoleName.PACIENTE)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(cmd.password())).thenReturn("hashed");
        when(userRepo.save(any())).thenReturn(savedUser);
        when(activationTokenRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.registerPatient(cmd);

        // Assert
        verify(userRepo).save(any(User.class));
        verify(emailPort).sendActivationEmail(eq(cmd.email()), eq(cmd.firstName()), anyString());
        verify(activationTokenRepo).save(any(ActivationToken.class));
    }

    @Test
    void registerPatient_emailAlreadyExists_throwsException() {
        var cmd = new RegisterPatientCommand("dup@test.com", "Pass1234!", "Juan", "Perez", null);
        when(userRepo.existsByEmail(cmd.email())).thenReturn(true);

        assertThatThrownBy(() -> service.registerPatient(cmd))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepo, never()).save(any());
        verify(emailPort, never()).sendActivationEmail(any(), any(), any());
    }

    @Test
    void activateAccount_validToken_activatesUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String rawToken = "test-token";
        var user = new User(userId, "u@test.com", "hash", "Maria", "G", null,
                UserStatus.PENDING, Instant.now());
        var token = new ActivationToken(UUID.randomUUID(), userId,
                hashToken(rawToken), Instant.now().plusSeconds(3600), false, Instant.now());

        when(activationTokenRepo.findByTokenHash(hashToken(rawToken))).thenReturn(Optional.of(token));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        when(activationTokenRepo.save(any())).thenReturn(token);

        // Act
        service.activateAccount(rawToken);

        // Assert
        assertThat(user.isActive()).isTrue();
        verify(userRepo).save(user);
    }

    @Test
    void activateAccount_expiredToken_throwsException() {
        String rawToken = "expired-token";
        var expiredToken = new ActivationToken(UUID.randomUUID(), UUID.randomUUID(),
                hashToken(rawToken), Instant.now().minusSeconds(1), false, Instant.now());

        when(activationTokenRepo.findByTokenHash(hashToken(rawToken))).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> service.activateAccount(rawToken))
                .isInstanceOf(com.akine_api.domain.exception.InvalidTokenException.class);
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
