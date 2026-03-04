package com.akine_api.application.service;

import com.akine_api.application.dto.command.CreateMyPacienteCommand;
import com.akine_api.application.dto.command.CreatePacienteAdminCommand;
import com.akine_api.application.dto.result.PacienteResult;
import com.akine_api.application.dto.result.PacienteSearchResult;
import com.akine_api.application.port.output.*;
import com.akine_api.domain.exception.*;
import com.akine_api.domain.model.Paciente;
import com.akine_api.domain.model.PacienteConsultorio;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class PacienteService {

    private final PacienteRepositoryPort pacienteRepo;
    private final PacienteConsultorioRepositoryPort pacienteConsultorioRepo;
    private final UserRepositoryPort userRepo;
    private final ConsultorioRepositoryPort consultorioRepo;

    public PacienteService(PacienteRepositoryPort pacienteRepo,
                           PacienteConsultorioRepositoryPort pacienteConsultorioRepo,
                           UserRepositoryPort userRepo,
                           ConsultorioRepositoryPort consultorioRepo) {
        this.pacienteRepo = pacienteRepo;
        this.pacienteConsultorioRepo = pacienteConsultorioRepo;
        this.userRepo = userRepo;
        this.consultorioRepo = consultorioRepo;
    }

    public PacienteResult createMe(CreateMyPacienteCommand cmd, String userEmail, Set<String> roles) {
        assertPacienteRole(roles);
        UUID userId = resolveUserId(userEmail);

        if (pacienteRepo.findByUserId(userId).isPresent()) {
            throw new PacienteDuplicadoException("El usuario ya tiene una ficha de paciente");
        }

        String dni = normalizeDni(cmd.dni());
        if (pacienteRepo.findByDni(dni).isPresent()) {
            throw new PacienteDuplicadoException("Paciente ya registrado");
        }

        Instant now = Instant.now();
        Paciente created = pacienteRepo.save(new Paciente(
                UUID.randomUUID(),
                dni,
                cmd.nombre(),
                cmd.apellido(),
                cmd.telefono(),
                cmd.email(),
                cmd.fechaNacimiento(),
                cmd.sexo(),
                cmd.domicilio(),
                cmd.nacionalidad(),
                cmd.estadoCivil(),
                cmd.profesion(),
                cmd.obraSocialNombre(),
                cmd.obraSocialPlan(),
                cmd.obraSocialNroAfiliado(),
                userId,
                true,
                userId,
                now,
                now
        ));

        return toResult(created);
    }

    @Transactional(readOnly = true)
    public PacienteResult getMe(String userEmail, Set<String> roles) {
        assertPacienteRole(roles);
        UUID userId = resolveUserId(userEmail);

        Paciente paciente = pacienteRepo.findByUserId(userId)
                .orElseThrow(() -> new PacienteNotFoundException("Paciente no encontrado para el usuario"));
        return toResult(paciente);
    }

    public PacienteResult createAdmin(UUID consultorioId, CreatePacienteAdminCommand cmd, String userEmail, Set<String> roles) {
        assertBackofficeRole(roles);
        assertCanManageConsultorio(consultorioId, userEmail, roles);
        UUID actorUserId = resolveUserId(userEmail);

        String dni = normalizeDni(cmd.dni());
        Optional<Paciente> existingOpt = pacienteRepo.findByDni(dni);

        if (existingOpt.isPresent()) {
            Paciente existing = existingOpt.get();
            boolean linked = pacienteConsultorioRepo.existsByPacienteIdAndConsultorioId(existing.getId(), consultorioId);
            if (linked) {
                throw new PacienteYaVinculadoException("Paciente ya existe para este consultorio");
            }

            pacienteConsultorioRepo.save(new PacienteConsultorio(
                    UUID.randomUUID(),
                    existing.getId(),
                    consultorioId,
                    actorUserId,
                    Instant.now()
            ));
            return toResult(existing);
        }

        Instant now = Instant.now();
        Paciente created = pacienteRepo.save(new Paciente(
                UUID.randomUUID(),
                dni,
                cmd.nombre(),
                cmd.apellido(),
                cmd.telefono(),
                cmd.email(),
                cmd.fechaNacimiento(),
                cmd.sexo(),
                cmd.domicilio(),
                cmd.nacionalidad(),
                cmd.estadoCivil(),
                cmd.profesion(),
                cmd.obraSocialNombre(),
                cmd.obraSocialPlan(),
                cmd.obraSocialNroAfiliado(),
                null,
                true,
                actorUserId,
                now,
                now
        ));

        pacienteConsultorioRepo.save(new PacienteConsultorio(
                UUID.randomUUID(),
                created.getId(),
                consultorioId,
                actorUserId,
                now
        ));

        return toResult(created);
    }

    @Transactional(readOnly = true)
    public List<PacienteSearchResult> listByConsultorio(UUID consultorioId,
                                                         String userEmail,
                                                         Set<String> roles) {
        assertBackofficeRole(roles);
        assertCanManageConsultorio(consultorioId, userEmail, roles);

        List<UUID> linkedIds = pacienteConsultorioRepo.findPacienteIdsByConsultorioId(consultorioId).stream()
                .limit(200)
                .toList();
        if (linkedIds.isEmpty()) {
            return List.of();
        }

        List<Paciente> pacientes = pacienteRepo.findByIds(linkedIds);
        Set<UUID> linkedSet = new HashSet<>(linkedIds);

        return pacientes.stream().map(p -> new PacienteSearchResult(
                p.getId(),
                p.getDni(),
                p.getNombre(),
                p.getApellido(),
                p.getTelefono(),
                p.getEmail(),
                p.isActivo(),
                linkedSet.contains(p.getId())
        )).toList();
    }

    @Transactional(readOnly = true)
    public List<PacienteSearchResult> search(UUID consultorioId,
                                             String dni,
                                             String q,
                                             String userEmail,
                                             Set<String> roles) {
        assertBackofficeRole(roles);
        assertCanManageConsultorio(consultorioId, userEmail, roles);

        List<Paciente> pacientes = new ArrayList<>();
        List<UUID> linkedIdsList = List.of();
        if (dni != null && !dni.isBlank()) {
            pacienteRepo.findByDni(normalizeDni(dni)).ifPresent(pacientes::add);
        } else if (q != null && !q.isBlank()) {
            pacientes = pacienteRepo.searchByNombreApellido(q.trim(), 20);
        } else {
            return listByConsultorio(consultorioId, userEmail, roles);
        }

        if (pacientes.isEmpty()) {
            return List.of();
        }

        Set<UUID> linkedIds;
        if (!linkedIdsList.isEmpty()) {
            linkedIds = new HashSet<>(linkedIdsList);
        } else {
            List<UUID> ids = pacientes.stream().map(Paciente::getId).toList();
            linkedIds = new HashSet<>(
                    pacienteConsultorioRepo.findPacienteIdsByConsultorioIdAndPacienteIds(consultorioId, ids)
            );
        }

        return pacientes.stream().map(p -> new PacienteSearchResult(
                p.getId(),
                p.getDni(),
                p.getNombre(),
                p.getApellido(),
                p.getTelefono(),
                p.getEmail(),
                p.isActivo(),
                linkedIds.contains(p.getId())
        )).toList();
    }

    @Transactional(readOnly = true)
    public PacienteResult getById(UUID pacienteId, UUID consultorioId, String userEmail, Set<String> roles) {
        assertBackofficeRole(roles);
        assertCanManageConsultorio(consultorioId, userEmail, roles);

        Paciente paciente = pacienteRepo.findById(pacienteId)
                .orElseThrow(() -> new PacienteNotFoundException("Paciente no encontrado"));

        if (!pacienteConsultorioRepo.existsByPacienteIdAndConsultorioId(pacienteId, consultorioId)) {
            throw new PacienteNotFoundException("Paciente no encontrado en este consultorio");
        }
        return toResult(paciente);
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private void assertPacienteRole(Set<String> roles) {
        if (!roles.contains("ROLE_PACIENTE")) {
            throw new AccessDeniedException("Solo PACIENTE puede operar este endpoint");
        }
    }

    private void assertBackofficeRole(Set<String> roles) {
        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_PROFESIONAL_ADMIN") || roles.contains("ROLE_ADMINISTRATIVO")) {
            return;
        }
        throw new AccessDeniedException("Permiso denegado");
    }

    private void assertCanManageConsultorio(UUID consultorioId, String userEmail, Set<String> roles) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);

        if (roles.contains("ROLE_ADMIN")) {
            return;
        }
        UUID userId = resolveUserId(userEmail);
        List<UUID> consultorioIds = consultorioRepo.findConsultorioIdsByUserId(userId);
        if (!consultorioIds.contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private String normalizeDni(String dni) {
        return dni == null ? "" : dni.trim();
    }

    private PacienteResult toResult(Paciente p) {
        return new PacienteResult(
                p.getId(),
                p.getDni(),
                p.getNombre(),
                p.getApellido(),
                p.getTelefono(),
                p.getEmail(),
                p.getFechaNacimiento(),
                p.getSexo(),
                p.getDomicilio(),
                p.getNacionalidad(),
                p.getEstadoCivil(),
                p.getProfesion(),
                p.getObraSocialNombre(),
                p.getObraSocialPlan(),
                p.getObraSocialNroAfiliado(),
                p.getUserId(),
                p.isActivo(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
