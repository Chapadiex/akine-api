package com.akine_api.application.service;

import com.akine_api.application.dto.command.ChangePasswordCommand;
import com.akine_api.application.dto.command.UpdateProfileCommand;
import com.akine_api.application.dto.result.UserProfileResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.EmailPort;
import com.akine_api.application.port.output.PasswordEncoderPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.InvalidCredentialsException;
import com.akine_api.domain.exception.UserNotFoundException;
import com.akine_api.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserProfileService {

    private final UserRepositoryPort userRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final PasswordEncoderPort passwordEncoder;
    private final EmailPort emailPort;

    public UserProfileService(UserRepositoryPort userRepo,
                              ConsultorioRepositoryPort consultorioRepo,
                              PasswordEncoderPort passwordEncoder,
                              EmailPort emailPort) {
        this.userRepo = userRepo;
        this.consultorioRepo = consultorioRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailPort = emailPort;
    }

    @Transactional(readOnly = true)
    public UserProfileResult getMyProfile(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
        return toResult(user);
    }

    public UserProfileResult updateMyProfile(UpdateProfileCommand cmd) {
        User user = userRepo.findById(cmd.userId())
                .orElseThrow(() -> new UserNotFoundException(cmd.userId().toString()));
        user.updateProfile(cmd.firstName(), cmd.lastName(), cmd.phone());
        User updated = userRepo.save(user);
        return toResult(updated);
    }

    public void changePassword(ChangePasswordCommand cmd) {
        User user = userRepo.findById(cmd.userId())
                .orElseThrow(() -> new UserNotFoundException(cmd.userId().toString()));

        if (!passwordEncoder.matches(cmd.currentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        user.changePassword(passwordEncoder.encode(cmd.newPassword()));
        userRepo.save(user);

        emailPort.sendPasswordChangedNotification(user.getEmail(), user.getFirstName());
    }

    private UserProfileResult toResult(User user) {
        List<UUID> consultorioIds = consultorioRepo.findConsultorioIdsByUserId(user.getId());
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());
        return new UserProfileResult(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getStatus().name(),
                roles,
                consultorioIds
        );
    }
}
