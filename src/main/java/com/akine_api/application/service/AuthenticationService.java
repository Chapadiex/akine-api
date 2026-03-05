package com.akine_api.application.service;

import com.akine_api.application.dto.command.LoginCommand;
import com.akine_api.application.dto.result.AuthResult;
import com.akine_api.application.dto.result.TokenPairResult;
import com.akine_api.application.port.output.*;
import com.akine_api.domain.exception.InvalidCredentialsException;
import com.akine_api.domain.exception.InvalidTokenException;
import com.akine_api.domain.exception.UserNotFoundException;
import com.akine_api.domain.exception.UserNotActiveException;
import com.akine_api.domain.model.Profesional;
import com.akine_api.domain.model.RefreshToken;
import com.akine_api.domain.model.RoleName;
import com.akine_api.domain.model.SuscripcionStatus;
import com.akine_api.domain.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuthenticationService {

    private final UserRepositoryPort userRepo;
    private final RefreshTokenRepositoryPort refreshTokenRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final ProfesionalRepositoryPort profesionalRepo;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final SuscripcionService suscripcionService;

    @Value("${app.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-token-expiration-days}")
    private long refreshTokenExpirationDays;

    public AuthenticationService(UserRepositoryPort userRepo,
                                 RefreshTokenRepositoryPort refreshTokenRepo,
                                 ConsultorioRepositoryPort consultorioRepo,
                                 ProfesionalRepositoryPort profesionalRepo,
                                 PasswordEncoderPort passwordEncoder,
                                 TokenGeneratorPort tokenGenerator,
                                 SuscripcionService suscripcionService) {
        this.userRepo = userRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.consultorioRepo = consultorioRepo;
        this.profesionalRepo = profesionalRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.suscripcionService = suscripcionService;
    }

    public AuthResult login(LoginCommand cmd) {
        User user = userRepo.findByEmail(cmd.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(cmd.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isActive()) {
            throw new UserNotActiveException();
        }

        return buildAuthResult(user);
    }

    public TokenPairResult refresh(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken stored = refreshTokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::new);

        if (!stored.isValid()) {
            // Revoke all tokens for this user (possible token theft)
            refreshTokenRepo.revokeAllByUserId(stored.getUserId());
            throw new InvalidTokenException();
        }

        User user = userRepo.findById(stored.getUserId())
                .orElseThrow(() -> new UserNotFoundException(stored.getUserId().toString()));

        // Rotate: revoke old, issue new
        stored.revoke();
        refreshTokenRepo.save(stored);

        List<UUID> consultorioIds = resolveScopedConsultorioIds(user);
        String newAccessToken = tokenGenerator.generateAccessToken(user, consultorioIds);
        String newRawRefresh = tokenGenerator.generateRefreshToken();
        saveRefreshToken(user.getId(), newRawRefresh);

        return new TokenPairResult(newAccessToken, newRawRefresh, accessTokenExpirationMs);
    }

    public void logout(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        refreshTokenRepo.findByTokenHash(tokenHash).ifPresent(t -> {
            t.revoke();
            refreshTokenRepo.save(t);
        });
    }

    private AuthResult buildAuthResult(User user) {
        List<UUID> consultorioIds = resolveScopedConsultorioIds(user);
        String accountState = resolveAccountState(user, consultorioIds);
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .sorted()
                .toList();
        String defaultRole = roles.stream().findFirst().orElse("PACIENTE");
        String accessToken = tokenGenerator.generateAccessToken(user, consultorioIds);
        String rawRefresh = tokenGenerator.generateRefreshToken();
        saveRefreshToken(user.getId(), rawRefresh);

        UUID profesionalId = profesionalRepo.findByUserId(user.getId())
                .or(() -> profesionalRepo.findByEmail(user.getEmail()))
                .map(Profesional::getId)
                .orElse(null);

        return new AuthResult(
                accessToken,
                rawRefresh,
                accessTokenExpirationMs,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles,
                accountState,
                defaultRole,
                roles,
                consultorioIds,
                profesionalId
        );
    }

    private List<UUID> resolveScopedConsultorioIds(User user) {
        suscripcionService.expireDueSubscriptions();
        List<UUID> consultorioIds = consultorioRepo.findConsultorioIdsByUserId(user.getId());
        boolean isPlatformAdmin = user.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ADMIN);
        // Login is allowed for non-active subscription states so frontend can route to review/suspended screens.
        // Access to consultorio-scoped business endpoints remains controlled by consultorio memberships/guards.
        if (isPlatformAdmin) return consultorioIds;
        return consultorioIds;
    }

    private String resolveAccountState(User user, List<UUID> consultorioIds) {
        if (user.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ADMIN)) {
            return "ACTIVE";
        }
        if (!consultorioIds.isEmpty()) {
            return "ACTIVE";
        }
        return suscripcionService.findLatestByOwnerUserId(user.getId())
                .map(s -> {
                    String status = s.status();
                    if ("PENDING".equals(status)) return "PENDING_APPROVAL";
                    return status;
                })
                .orElse(SuscripcionStatus.REJECTED.name());
    }

    private void saveRefreshToken(UUID userId, String rawToken) {
        String tokenHash = hashToken(rawToken);
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                userId,
                tokenHash,
                Instant.now().plus(refreshTokenExpirationDays, ChronoUnit.DAYS),
                false,
                Instant.now()
        );
        refreshTokenRepo.save(token);
    }

    private String hashToken(String raw) {
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            var sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
