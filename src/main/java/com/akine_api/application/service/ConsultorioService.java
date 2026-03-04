package com.akine_api.application.service;

import com.akine_api.application.dto.command.CreateConsultorioCommand;
import com.akine_api.application.dto.command.UpdateConsultorioCommand;
import com.akine_api.application.dto.result.ConsultorioResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.ConsultorioInactiveException;
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
    private final ConsultorioEspecialidadBootstrapService consultorioEspecialidadBootstrapService;
    private final ConsultorioAntecedenteBootstrapService consultorioAntecedenteBootstrapService;

    public ConsultorioService(ConsultorioRepositoryPort consultorioRepo,
                              UserRepositoryPort userRepo,
                              ConsultorioEspecialidadBootstrapService consultorioEspecialidadBootstrapService,
                              ConsultorioAntecedenteBootstrapService consultorioAntecedenteBootstrapService) {
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
        this.consultorioEspecialidadBootstrapService = consultorioEspecialidadBootstrapService;
        this.consultorioAntecedenteBootstrapService = consultorioAntecedenteBootstrapService;
    }

    @Transactional(readOnly = true)
    public List<ConsultorioResult> list(String userEmail, Set<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            return consultorioRepo.findAll().stream().map(this::toResult).toList();
        }
        UUID userId = resolveUserId(userEmail);
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        return consultorioRepo.findByIds(ids).stream()
                .filter(Consultorio::isActive)
                .map(this::toResult)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConsultorioResult getById(UUID id, String userEmail, Set<String> roles) {
        Consultorio c = findOrThrow(id);
        if (roles.contains("ROLE_ADMIN")) {
            return toResult(c);
        }
        assertMember(id, resolveUserId(userEmail));
        assertActive(c);
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
        Consultorio saved = consultorioRepo.save(c);
        consultorioEspecialidadBootstrapService.enableDefaultsForConsultorio(saved.getId());
        consultorioAntecedenteBootstrapService.ensureDefaults(saved.getId(), "system");
        return toResult(saved);
    }

    public ConsultorioResult update(UpdateConsultorioCommand cmd, String userEmail, Set<String> roles) {
        Consultorio c = findOrThrow(cmd.id());
        if (!roles.contains("ROLE_ADMIN")) {
            assertAdminMember(cmd.id(), resolveUserId(userEmail), roles);
        }
        assertActive(c);
        c.update(cmd.name(), cmd.cuit(), cmd.address(), cmd.phone(), cmd.email());
        return toResult(consultorioRepo.save(c));
    }

    public void inactivate(UUID id, String userEmail, Set<String> roles) {
        if (!roles.contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Solo ADMIN puede dar de baja consultorios");
        }
        Consultorio c = findOrThrow(id);
        if (c.isActive()) {
            c.inactivate();
            consultorioRepo.save(c);
        }
    }

    public ConsultorioResult activate(UUID id, Set<String> roles) {
        if (!roles.contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Solo ADMIN puede reactivar consultorios");
        }
        Consultorio c = findOrThrow(id);
        if (!c.isActive()) {
            c.activate();
            c = consultorioRepo.save(c);
        }
        return toResult(c);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Consultorio findOrThrow(UUID id) {
        return ConsultorioStateGuardService.requireExists(consultorioRepo, id);
    }

    private void assertActive(Consultorio consultorio) {
        if (!consultorio.isActive()) {
            throw new ConsultorioInactiveException("Consultorio inactivo. Solo ADMIN puede reactivarlo.");
        }
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
