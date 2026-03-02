package com.akine_api.application.service;

import com.akine_api.application.dto.command.CreateConsultorioCommand;
import com.akine_api.application.dto.command.UpdateConsultorioCommand;
import com.akine_api.application.dto.result.ConsultorioResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ConsultorioService {

    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;

    public ConsultorioService(ConsultorioRepositoryPort consultorioRepo,
                              UserRepositoryPort userRepo) {
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<ConsultorioResult> list(String userEmail, Set<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            return consultorioRepo.findAll().stream().map(this::toResult).toList();
        }
        UUID userId = resolveUserId(userEmail);
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        return consultorioRepo.findByIds(ids).stream().map(this::toResult).toList();
    }

    @Transactional(readOnly = true)
    public ConsultorioResult getById(UUID id, String userEmail, Set<String> roles) {
        Consultorio c = findOrThrow(id);
        if (!roles.contains("ROLE_ADMIN")) {
            assertMember(id, resolveUserId(userEmail));
        }
        return toResult(c);
    }

    public ConsultorioResult create(CreateConsultorioCommand cmd, Set<String> roles) {
        if (!roles.contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Solo ADMIN puede crear consultorios");
        }
        Consultorio c = new Consultorio(
                UUID.randomUUID(),
                cmd.name(), cmd.cuit(), cmd.address(), cmd.phone(), cmd.email(),
                "ACTIVE", Instant.now()
        );
        return toResult(consultorioRepo.save(c));
    }

    public ConsultorioResult update(UpdateConsultorioCommand cmd, String userEmail, Set<String> roles) {
        Consultorio c = findOrThrow(cmd.id());
        if (!roles.contains("ROLE_ADMIN")) {
            assertAdminMember(cmd.id(), resolveUserId(userEmail), roles);
        }
        c.update(cmd.name(), cmd.cuit(), cmd.address(), cmd.phone(), cmd.email());
        return toResult(consultorioRepo.save(c));
    }

    public void inactivate(UUID id, String userEmail, Set<String> roles) {
        Consultorio c = findOrThrow(id);
        if (!roles.contains("ROLE_ADMIN")) {
            assertAdminMember(id, resolveUserId(userEmail), roles);
        }
        c.inactivate();
        consultorioRepo.save(c);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Consultorio findOrThrow(UUID id) {
        return consultorioRepo.findById(id)
                .orElseThrow(() -> new ConsultorioNotFoundException("Consultorio no encontrado: " + id));
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private void assertMember(UUID consultorioId, UUID userId) {
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        if (!ids.contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private void assertAdminMember(UUID consultorioId, UUID userId, Set<String> roles) {
        if (!roles.contains("ROLE_PROFESIONAL_ADMIN")) {
            throw new AccessDeniedException("Permiso denegado");
        }
        assertMember(consultorioId, userId);
    }

    private ConsultorioResult toResult(Consultorio c) {
        return new ConsultorioResult(
                c.getId(), c.getName(), c.getCuit(), c.getAddress(),
                c.getPhone(), c.getEmail(), c.getStatus(),
                c.getCreatedAt(), c.getUpdatedAt()
        );
    }
}
