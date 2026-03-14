package com.akine_api.application.service;

import com.akine_api.application.dto.command.CreateProfesionalCommand;
import com.akine_api.application.dto.command.UpdateProfesionalCommand;
import com.akine_api.application.dto.result.ProfesionalResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.ProfesionalNotFoundException;
import com.akine_api.domain.model.Profesional;
import com.akine_api.domain.model.ProfesionalConsultorio;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfesionalService {

    private final ProfesionalRepositoryPort profesionalRepo;
    private final ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;

    public ProfesionalService(ProfesionalRepositoryPort profesionalRepo,
                              ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo,
                              ConsultorioRepositoryPort consultorioRepo,
                              UserRepositoryPort userRepo) {
        this.profesionalRepo = profesionalRepo;
        this.profesionalConsultorioRepo = profesionalConsultorioRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<ProfesionalResult> list(UUID consultorioId, String userEmail, Set<String> roles,
                                        String dni, String q, String matricula,
                                        List<String> especialidades, Boolean activo) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);

        return profesionalRepo.findByConsultorioId(consultorioId).stream()
                .filter(p -> dni == null || dni.isBlank() || normalize(p.getNroDocumento()).equals(normalize(dni)))
                .filter(p -> matricula == null || matricula.isBlank() || containsIgnoreCase(p.getMatricula(), matricula))
                .filter(p -> q == null || q.isBlank() || matchesQuery(p, q))
                .filter(p -> activo == null || p.isActivo() == activo)
                .filter(p -> hasAnyEspecialidad(p, especialidades))
                .map(this::toResult)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProfesionalResult getById(UUID consultorioId, UUID profesionalId,
                                     String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        Profesional p = profesionalRepo.findById(profesionalId)
                .filter(pr -> pr.getConsultorioId().equals(consultorioId))
                .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado"));
        return toResult(p);
    }

    public ProfesionalResult create(CreateProfesionalCommand cmd, String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        validateUniqueCreate(cmd);

        String especialidadesCsv = normalizeEspecialidades(cmd.especialidades(), cmd.especialidad());
        String especialidadPrincipal = firstEspecialidad(especialidadesCsv);

        Profesional p = new Profesional(
                UUID.randomUUID(),
                cmd.consultorioId(),
                cmd.nombre(),
                cmd.apellido(),
                normalize(cmd.nroDocumento()),
                cmd.matricula(),
                especialidadPrincipal,
                especialidadesCsv,
                cmd.email(),
                cmd.telefono(),
                cmd.domicilio(),
                cmd.fotoPerfilUrl(),
                LocalDate.now(),
                null,
                null,
                true,
                Instant.now()
        );
        Profesional saved = profesionalRepo.save(p);
        ensureProfesionalConsultorioRelation(saved.getId(), cmd.consultorioId());
        return toResult(saved);
    }

    public ProfesionalResult update(UpdateProfesionalCommand cmd, String userEmail, Set<String> roles) {
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        Profesional p = profesionalRepo.findById(cmd.id())
                .filter(pr -> pr.getConsultorioId().equals(cmd.consultorioId()))
                .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado"));

        validateUniqueUpdate(cmd, p);
        String especialidadesCsv = normalizeEspecialidades(cmd.especialidades(), cmd.especialidad());
        String especialidadPrincipal = firstEspecialidad(especialidadesCsv);

        p.update(
                cmd.nombre(),
                cmd.apellido(),
                normalize(cmd.nroDocumento()),
                cmd.matricula(),
                especialidadPrincipal,
                especialidadesCsv,
                cmd.email(),
                cmd.telefono(),
                cmd.domicilio(),
                cmd.fotoPerfilUrl()
        );
        return toResult(profesionalRepo.save(p));
    }

    public ProfesionalResult changeEstado(UUID consultorioId, UUID profesionalId, boolean activo,
                                          LocalDate fechaDeBaja, String motivoDeBaja,
                                          String userEmail, Set<String> roles) {
        assertCanWrite(consultorioId, userEmail, roles);
        Profesional p = profesionalRepo.findById(profesionalId)
                .filter(pr -> pr.getConsultorioId().equals(consultorioId))
                .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado"));

        if (activo) {
            p.activate();
            return toResult(profesionalRepo.save(p));
        }

        LocalDate baja = fechaDeBaja != null ? fechaDeBaja : LocalDate.now();
        String motivo = motivoDeBaja != null ? motivoDeBaja.trim() : "";
        if (motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de baja es obligatorio.");
        }
        p.inactivate(baja, motivo);
        return toResult(profesionalRepo.save(p));
    }

    public void inactivate(UUID consultorioId, UUID profesionalId,
                           String userEmail, Set<String> roles) {
        changeEstado(
                consultorioId,
                profesionalId,
                false,
                LocalDate.now(),
                "Baja logica",
                userEmail,
                roles
        );
    }

    private void validateUniqueCreate(CreateProfesionalCommand cmd) {
        if (cmd.matricula() != null && !cmd.matricula().isBlank()
                && profesionalRepo.existsByMatriculaAndConsultorioId(cmd.matricula(), cmd.consultorioId())) {
            throw new IllegalArgumentException("La matricula ya esta registrada.");
        }
        String nroDocumento = normalize(cmd.nroDocumento());
        if (nroDocumento != null && profesionalRepo.existsByNroDocumento(nroDocumento)) {
            throw new IllegalArgumentException("El DNI ya esta registrado.");
        }
    }

    private void validateUniqueUpdate(UpdateProfesionalCommand cmd, Profesional current) {
        if (cmd.matricula() != null && !cmd.matricula().isBlank()
                && !cmd.matricula().equals(current.getMatricula())
                && profesionalRepo.existsByMatriculaAndConsultorioIdAndIdNot(
                cmd.matricula(), cmd.consultorioId(), cmd.id())) {
            throw new IllegalArgumentException("La matricula ya esta registrada.");
        }

        String newDocumento = normalize(cmd.nroDocumento());
        String currentDocumento = normalize(current.getNroDocumento());
        if (newDocumento != null && !newDocumento.equals(currentDocumento)
                && profesionalRepo.existsByNroDocumentoAndIdNot(newDocumento, cmd.id())) {
            throw new IllegalArgumentException("El DNI ya esta registrado.");
        }
    }

    private void ensureProfesionalConsultorioRelation(UUID profesionalId, UUID consultorioId) {
        profesionalConsultorioRepo.findByProfesionalIdAndConsultorioId(profesionalId, consultorioId)
                .ifPresentOrElse(existing -> {
                    if (!existing.isActivo()) {
                        profesionalConsultorioRepo.save(new ProfesionalConsultorio(
                                existing.getId(),
                                existing.getProfesionalId(),
                                existing.getConsultorioId(),
                                true,
                                existing.getCreatedAt()
                        ));
                    }
                }, () -> profesionalConsultorioRepo.save(new ProfesionalConsultorio(
                        UUID.randomUUID(),
                        profesionalId,
                        consultorioId,
                        true,
                        Instant.now()
                )));
    }

    private boolean matchesQuery(Profesional p, String q) {
        return containsIgnoreCase(p.getNombre(), q)
                || containsIgnoreCase(p.getApellido(), q)
                || containsIgnoreCase(p.getNombre() + " " + p.getApellido(), q);
    }

    private boolean hasAnyEspecialidad(Profesional p, List<String> especialidades) {
        if (especialidades == null || especialidades.isEmpty()) {
            return true;
        }
        Set<String> target = especialidades.stream()
                .map(this::normalizeLower)
                .filter(v -> !v.isBlank())
                .collect(Collectors.toSet());

        Set<String> current = parseEspecialidades(p.getEspecialidades()).stream()
                .map(this::normalizeLower)
                .collect(Collectors.toSet());

        if (current.isEmpty() && p.getEspecialidad() != null) {
            current.add(normalizeLower(p.getEspecialidad()));
        }
        return current.stream().anyMatch(target::contains);
    }

    private String normalizeEspecialidades(String especialidades, String especialidadFallback) {
        List<String> values = parseEspecialidades(especialidades);
        if (values.isEmpty() && especialidadFallback != null && !especialidadFallback.isBlank()) {
            values = List.of(especialidadFallback.trim());
        }
        return values.stream()
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .distinct()
                .collect(Collectors.joining("|"));
    }

    private List<String> parseEspecialidades(String especialidades) {
        if (especialidades == null || especialidades.isBlank()) {
            return List.of();
        }
        return Arrays.stream(especialidades.split("[|,]"))
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .distinct()
                .toList();
    }

    private String firstEspecialidad(String especialidadesCsv) {
        return parseEspecialidades(especialidadesCsv).stream().findFirst().orElse(null);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String normalizeLower(String value) {
        String normalized = normalize(value);
        return normalized == null ? "" : normalized.toLowerCase();
    }

    private boolean containsIgnoreCase(String value, String query) {
        if (value == null || query == null) {
            return false;
        }
        return value.toLowerCase().contains(query.toLowerCase());
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

    private ProfesionalResult toResult(Profesional p) {
        int consultoriosAsociados = profesionalConsultorioRepo.findByProfesionalId(p.getId()).stream()
                .filter(pc -> pc.isActivo())
                .map(pc -> pc.getConsultorioId())
                .collect(Collectors.toSet())
                .size();

        return new ProfesionalResult(
                p.getId(),
                p.getConsultorioId(),
                p.getNombre(),
                p.getApellido(),
                p.getNroDocumento(),
                p.getMatricula(),
                p.getEspecialidad(),
                p.getEspecialidades(),
                p.getEmail(),
                p.getTelefono(),
                p.getDomicilio(),
                p.getFotoPerfilUrl(),
                p.getFechaAlta(),
                p.getFechaBaja(),
                p.getMotivoBaja(),
                consultoriosAsociados,
                p.isActivo(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
