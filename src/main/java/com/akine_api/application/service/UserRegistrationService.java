package com.akine_api.application.service;

import com.akine_api.application.dto.command.RegisterPatientCommand;
import com.akine_api.application.dto.command.RegisterProfessionalCommand;
import com.akine_api.application.dto.result.AuthResult;
import com.akine_api.application.port.output.*;
import com.akine_api.domain.exception.InvalidTokenException;
import com.akine_api.domain.exception.RoleNotFoundException;
import com.akine_api.domain.exception.UserAlreadyExistsException;
import com.akine_api.domain.exception.UserNotFoundException;
import com.akine_api.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserRegistrationService {

    private final UserRepositoryPort userRepo;
    private final RoleRepositoryPort roleRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final MembershipRepositoryPort membershipRepo;
    private final ActivationTokenRepositoryPort activationTokenRepo;
    private final PasswordEncoderPort passwordEncoder;
    private final EmailPort emailPort;
    private final TokenGeneratorPort tokenGenerator;
    private final RefreshTokenRepositoryPort refreshTokenRepo;

    public UserRegistrationService(UserRepositoryPort userRepo,
                                   RoleRepositoryPort roleRepo,
                                   ConsultorioRepositoryPort consultorioRepo,
                                   MembershipRepositoryPort membershipRepo,
                                   ActivationTokenRepositoryPort activationTokenRepo,
                                   PasswordEncoderPort passwordEncoder,
                                   EmailPort emailPort,
                                   TokenGeneratorPort tokenGenerator,
                                   RefreshTokenRepositoryPort refreshTokenRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.consultorioRepo = consultorioRepo;
        this.membershipRepo = membershipRepo;
        this.activationTokenRepo = activationTokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailPort = emailPort;
        this.tokenGenerator = tokenGenerator;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public void registerPatient(RegisterPatientCommand cmd) {
        if (userRepo.existsByEmail(cmd.email())) {
            throw new UserAlreadyExistsException(cmd.email());
        }

        Role pacienteRole = roleRepo.findByName(RoleName.PACIENTE)
                .orElseThrow(() -> new RoleNotFoundException(RoleName.PACIENTE.name()));

        User user = new User(
                UUID.randomUUID(),
                cmd.email(),
                passwordEncoder.encode(cmd.password()),
                cmd.firstName(),
                cmd.lastName(),
                cmd.phone(),
                UserStatus.PENDING,
                Instant.now()
        );
        user.addRole(pacienteRole);
        userRepo.save(user);

        sendActivationEmail(user);
    }

    public void registerProfessional(RegisterProfessionalCommand cmd) {
        if (userRepo.existsByEmail(cmd.email())) {
            throw new UserAlreadyExistsException(cmd.email());
        }

        Role profAdminRole = roleRepo.findByName(RoleName.PROFESIONAL_ADMIN)
                .orElseThrow(() -> new RoleNotFoundException(RoleName.PROFESIONAL_ADMIN.name()));

        User user = new User(
                UUID.randomUUID(),
                cmd.email(),
                passwordEncoder.encode(cmd.password()),
                cmd.firstName(),
                cmd.lastName(),
                cmd.phone(),
                UserStatus.PENDING,
                Instant.now()
        );
        user.addRole(profAdminRole);
        User savedUser = userRepo.save(user);

        Consultorio consultorio = new Consultorio(
                UUID.randomUUID(),
                cmd.consultorioName(),
                null,
                cmd.consultorioAddress(),
                cmd.consultorioPhone(),
                null,
                "ACTIVE",
                Instant.now()
        );
        Consultorio savedConsultorio = consultorioRepo.save(consultorio);

        Membership membership = new Membership(
                UUID.randomUUID(),
                savedUser.getId(),
                savedConsultorio.getId(),
                MembershipRole.PROFESIONAL_ADMIN,
                MembershipStatus.ACTIVE,
                Instant.now()
        );
        membershipRepo.save(membership);

        sendActivationEmail(savedUser);
    }

    public void activateAccount(String rawToken) {
        String tokenHash = hashToken(rawToken);
        ActivationToken token = activationTokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::new);

        if (!token.isValid()) {
            throw new InvalidTokenException();
        }

        User user = userRepo.findById(token.getUserId())
                .orElseThrow(() -> new UserNotFoundException(token.getUserId().toString()));

        user.activate();
        userRepo.save(user);

        token.markUsed();
        activationTokenRepo.save(token);
    }

    public void resendActivation(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.isActive()) {
            return; // silently ignore if already active
        }

        activationTokenRepo.deleteByUserId(user.getId());
        sendActivationEmail(user);
    }

    private void sendActivationEmail(User user) {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawToken);

        ActivationToken token = new ActivationToken(
                UUID.randomUUID(),
                user.getId(),
                tokenHash,
                Instant.now().plus(24, ChronoUnit.HOURS),
                false,
                Instant.now()
        );
        activationTokenRepo.save(token);

        emailPort.sendActivationEmail(user.getEmail(), user.getFirstName(), rawToken);
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
