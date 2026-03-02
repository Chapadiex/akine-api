package com.akine_api.application.service;

import com.akine_api.application.dto.command.CreateProfesionalCommand;
import com.akine_api.application.dto.command.UpdateProfesionalCommand;
import com.akine_api.application.dto.result.ProfesionalResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.exception.ProfesionalNotFoundException;
import com.akine_api.domain.model.Profesional;
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
public class ProfesionalService {

    private final ProfesionalRepositoryPort profesionalRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;

    public ProfesionalService(ProfesionalRepositoryPort profesionalRepo,
                               ConsultorioRepositoryPort consultorioRepo,
                               UserRepositoryPort userRepo) {
        this.profesionalRepo = profesionalRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<ProfesionalResult> list(UUID consultorioId, String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        return profesionalRepo.findByConsultorioId(consultorioId).stream().map(this::toResult).toList();
    }

    @Transactional(readOnly = true)
    public ProfesionalResult getById(UUID consultorioId, UUID profesionalId,
                                     String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        Profesional p = profesionalRepo.findById(profesionalId)
                .filter(pr -> pr.getConsultorioId().equals(consultorioId))
                .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado: " + profesionalId));
        return toResult(p);
    }

    public ProfesionalResult create(CreateProfesionalCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        if (profesionalRepo.existsByMatriculaAndConsultorioId(cmd.matricula(), cmd.consultorioId())) {
            throw new IllegalArgumentException(
                    "La matrícula '" + cmd.matricula() + "' ya existe en este consultorio");
        }
        Profesional p = new Profesional(UUID.randomUUID(), cmd.consultorioId(),
                cmd.nombre(), cmd.apellido(), cmd.matricula(), cmd.especialidad(),
                cmd.email(), cmd.telefono(), true, Instant.now());
        return toResult(profesionalRepo.save(p));
    }

    public ProfesionalResult update(UpdateProfesionalCommand cmd, String userEmail, Set<String> roles) {
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        Profesional p = profesionalRepo.findById(cmd.id())
                .filter(pr -> pr.getConsultorioId().equals(cmd.consultorioId()))
                .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado: " + cmd.id()));
        if (!p.getMatricula().equals(cmd.matricula())
                && profesionalRepo.existsByMatriculaAndConsultorioIdAndIdNot(
                        cmd.matricula(), cmd.consultorioId(), cmd.id())) {
            throw new IllegalArgumentException(
                    "La matrícula '" + cmd.matricula() + "' ya existe en este consultorio");
        }
        p.update(cmd.nombre(), cmd.apellido(), cmd.matricula(),
                cmd.especialidad(), cmd.email(), cmd.telefono());
        return toResult(profesionalRepo.save(p));
    }

    public void inactivate(UUID consultorioId, UUID profesionalId,
                           String userEmail, Set<String> roles) {
        assertCanWrite(consultorioId, userEmail, roles);
        Profesional p = profesionalRepo.findById(profesionalId)
                .filter(pr -> pr.getConsultorioId().equals(consultorioId))
                .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado: " + profesionalId));
        p.inactivate();
        profesionalRepo.save(p);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void assertConsultorioExists(UUID consultorioId) {
        consultorioRepo.findById(consultorioId)
                .orElseThrow(() -> new ConsultorioNotFoundException("Consultorio no encontrado: " + consultorioId));
    }

    private void assertCanRead(UUID consultorioId, String userEmail, Set<String> roles) {
        if (roles.contains("ROLE_ADMIN")) return;
        UUID userId = resolveUserId(userEmail);
        List<UUID> ids = consultorioRepo.findConsultorioIdsByUserId(userId);
        if (!ids.contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private void assertCanWrite(UUID consultorioId, String userEmail, Set<String> roles) {
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

    private ProfesionalResult toResult(Profesional p) {
        return new ProfesionalResult(p.getId(), p.getConsultorioId(), p.getNombre(),
                p.getApellido(), p.getMatricula(), p.getEspecialidad(),
                p.getEmail(), p.getTelefono(), p.isActivo(),
                p.getCreatedAt(), p.getUpdatedAt());
    }
}
