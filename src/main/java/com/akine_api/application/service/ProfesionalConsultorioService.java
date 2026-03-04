package com.akine_api.application.service;

import com.akine_api.application.dto.command.AsignarProfesionalCommand;
import com.akine_api.application.dto.command.DesasignarProfesionalCommand;
import com.akine_api.application.dto.result.ProfesionalConsultorioResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.ProfesionalConsultorioNotFoundException;
import com.akine_api.domain.exception.ProfesionalNotFoundException;
import com.akine_api.domain.model.Profesional;
import com.akine_api.domain.model.ProfesionalConsultorio;
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
public class ProfesionalConsultorioService {

    private final ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    private final ProfesionalRepositoryPort profesionalRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;

    public ProfesionalConsultorioService(ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo,
                                         ProfesionalRepositoryPort profesionalRepo,
                                         ConsultorioRepositoryPort consultorioRepo,
                                         UserRepositoryPort userRepo) {
        this.profesionalConsultorioRepo = profesionalConsultorioRepo;
        this.profesionalRepo = profesionalRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<ProfesionalConsultorioResult> list(UUID consultorioId, String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        return profesionalConsultorioRepo.findByConsultorioId(consultorioId).stream()
                .map(pc -> toResult(pc, profesionalRepo.findById(pc.getProfesionalId()).orElse(null)))
                .toList();
    }

    public ProfesionalConsultorioResult asignar(AsignarProfesionalCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        Profesional profesional = profesionalRepo.findById(cmd.profesionalId())
                .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado: " + cmd.profesionalId()));

        ProfesionalConsultorio saved = profesionalConsultorioRepo
                .findByProfesionalIdAndConsultorioId(cmd.profesionalId(), cmd.consultorioId())
                .map(existing -> {
                    if (existing.isActivo()) {
                        throw new IllegalArgumentException("El profesional ya esta asignado a este consultorio");
                    }
                    return new ProfesionalConsultorio(
                            existing.getId(),
                            existing.getProfesionalId(),
                            existing.getConsultorioId(),
                            true,
                            existing.getCreatedAt()
                    );
                })
                .orElseGet(() -> new ProfesionalConsultorio(
                        UUID.randomUUID(),
                        cmd.profesionalId(),
                        cmd.consultorioId(),
                        true,
                        Instant.now()
                ));

        return toResult(profesionalConsultorioRepo.save(saved), profesional);
    }

    public void desasignar(DesasignarProfesionalCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        ProfesionalConsultorio pc = profesionalConsultorioRepo
                .findByProfesionalIdAndConsultorioId(cmd.profesionalId(), cmd.consultorioId())
                .orElseThrow(() -> new ProfesionalConsultorioNotFoundException(
                        "Asignacion no encontrada para profesional " + cmd.profesionalId()));
        pc.inactivate();
        profesionalConsultorioRepo.save(pc);
    }

    private void assertConsultorioExists(UUID consultorioId) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
    }

    private void assertCanRead(UUID consultorioId, String userEmail, Set<String> roles) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
        if (roles.contains("ROLE_ADMIN")) return;
        UUID userId = resolveUserId(userEmail);
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        if (!ids.contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private void assertCanWrite(UUID consultorioId, String userEmail, Set<String> roles) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
        if (roles.contains("ROLE_ADMIN")) return;
        if (!roles.contains("ROLE_PROFESIONAL_ADMIN")) {
            throw new AccessDeniedException("Permiso denegado");
        }
        UUID userId = resolveUserId(userEmail);
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        if (!ids.contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private ProfesionalConsultorioResult toResult(ProfesionalConsultorio pc, Profesional profesional) {
        String nombre = profesional != null ? profesional.getNombre() : "";
        String apellido = profesional != null ? profesional.getApellido() : "";
        return new ProfesionalConsultorioResult(
                pc.getId(),
                pc.getProfesionalId(),
                pc.getConsultorioId(),
                nombre,
                apellido,
                pc.isActivo()
        );
    }
}
