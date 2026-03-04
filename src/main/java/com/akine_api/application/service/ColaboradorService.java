package com.akine_api.application.service;

import com.akine_api.application.dto.command.*;
import com.akine_api.application.dto.result.ColaboradorEmpleadoResult;
import com.akine_api.application.dto.result.ColaboradorProfesionalResult;
import com.akine_api.application.port.output.*;
import com.akine_api.domain.exception.EmpleadoNotFoundException;
import com.akine_api.domain.exception.ProfesionalNotFoundException;
import com.akine_api.domain.exception.RoleNotFoundException;
import com.akine_api.domain.exception.UserAlreadyExistsException;
import com.akine_api.domain.model.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ColaboradorService {

    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;
    private final RoleRepositoryPort roleRepo;
    private final MembershipRepositoryPort membershipRepo;
    private final ActivationTokenRepositoryPort activationTokenRepo;
    private final PasswordEncoderPort passwordEncoder;
    private final EmailPort emailPort;
    private final ProfesionalRepositoryPort profesionalRepo;
    private final EmpleadoRepositoryPort empleadoRepo;

    public ColaboradorService(ConsultorioRepositoryPort consultorioRepo,
                              UserRepositoryPort userRepo,
                              RoleRepositoryPort roleRepo,
                              MembershipRepositoryPort membershipRepo,
                              ActivationTokenRepositoryPort activationTokenRepo,
                              PasswordEncoderPort passwordEncoder,
                              EmailPort emailPort,
                              ProfesionalRepositoryPort profesionalRepo,
                              EmpleadoRepositoryPort empleadoRepo) {
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.membershipRepo = membershipRepo;
        this.activationTokenRepo = activationTokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailPort = emailPort;
        this.profesionalRepo = profesionalRepo;
        this.empleadoRepo = empleadoRepo;
    }

    @Transactional(readOnly = true)
    public List<ColaboradorProfesionalResult> listProfesionales(UUID consultorioId, String userEmail, Set<String> roles,
                                                                String q, String matricula, List<String> especialidades, Boolean activo) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        return profesionalRepo.findByConsultorioId(consultorioId).stream()
                .filter(p -> q == null || q.isBlank() || matchesQuery(p.getNombre(), p.getApellido(), p.getEmail(), q))
                .filter(p -> matricula == null || matricula.isBlank() || containsIgnoreCase(p.getMatricula(), matricula))
                .filter(p -> activo == null || p.isActivo() == activo)
                .filter(p -> hasAnyEspecialidad(p.getEspecialidades(), especialidades))
                .map(this::toProfesionalResult)
                .toList();
    }

    @Transactional(readOnly = true)
    public ColaboradorProfesionalResult getProfesional(UUID consultorioId, UUID profesionalId,
                                                       String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        Profesional profesional = findProfesional(consultorioId, profesionalId);
        return toProfesionalResult(profesional);
    }

    public ColaboradorProfesionalResult createProfesional(CreateColaboradorProfesionalCommand cmd,
                                                          String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);

        if (profesionalRepo.existsByMatriculaAndConsultorioId(cmd.matricula(), cmd.consultorioId())) {
            throw new IllegalArgumentException("La matricula ya esta registrada.");
        }
        String dni = normalize(cmd.nroDocumento());
        if (dni != null && profesionalRepo.existsByNroDocumento(dni)) {
            throw new IllegalArgumentException("El DNI ya esta registrado.");
        }

        String especialidadesCsv = normalizeEspecialidades(cmd.especialidades());
        String especialidadPrincipal = parseEspecialidades(especialidadesCsv).stream().findFirst().orElse(null);
        String modoAlta = normalizeModoAlta(cmd.modoAlta());
        String email = normalize(cmd.email());

        UUID linkedUserId = null;
        if ("INVITACION".equals(modoAlta)) {
            if (email == null) {
                throw new IllegalArgumentException("Email obligatorio para alta por invitacion.");
            }
            User user = createPendingUserForCollaborator(
                    email,
                    cmd.nombre(),
                    cmd.apellido(),
                    cmd.telefono(),
                    RoleName.PROFESIONAL,
                    MembershipRole.PROFESIONAL,
                    cmd.consultorioId()
            );
            linkedUserId = user.getId();
        }

        Profesional profesional = new Profesional(
                UUID.randomUUID(),
                cmd.consultorioId(),
                linkedUserId,
                cmd.nombre(),
                cmd.apellido(),
                dni,
                cmd.matricula(),
                especialidadPrincipal,
                especialidadesCsv,
                email,
                normalize(cmd.telefono()),
                normalize(cmd.domicilio()),
                normalize(cmd.fotoPerfilUrl()),
                LocalDate.now(),
                null,
                null,
                true,
                Instant.now()
        );
        return toProfesionalResult(profesionalRepo.save(profesional));
    }

    public ColaboradorProfesionalResult updateProfesional(UpdateColaboradorProfesionalCommand cmd,
                                                          String userEmail, Set<String> roles) {
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        Profesional profesional = findProfesional(cmd.consultorioId(), cmd.profesionalId());

        if (!profesional.getMatricula().equals(cmd.matricula())
                && profesionalRepo.existsByMatriculaAndConsultorioIdAndIdNot(
                cmd.matricula(), cmd.consultorioId(), cmd.profesionalId())) {
            throw new IllegalArgumentException("La matricula ya esta registrada.");
        }

        String dni = normalize(cmd.nroDocumento());
        if (dni != null && !dni.equals(normalize(profesional.getNroDocumento()))
                && profesionalRepo.existsByNroDocumentoAndIdNot(dni, cmd.profesionalId())) {
            throw new IllegalArgumentException("El DNI ya esta registrado.");
        }

        String especialidadesCsv = normalizeEspecialidades(cmd.especialidades());
        String especialidadPrincipal = parseEspecialidades(especialidadesCsv).stream().findFirst().orElse(null);

        profesional.update(
                cmd.nombre(),
                cmd.apellido(),
                dni,
                cmd.matricula(),
                especialidadPrincipal,
                especialidadesCsv,
                normalize(cmd.email()),
                normalize(cmd.telefono()),
                normalize(cmd.domicilio()),
                normalize(cmd.fotoPerfilUrl())
        );
        return toProfesionalResult(profesionalRepo.save(profesional));
    }

    public ColaboradorProfesionalResult changeProfesionalEstado(ChangeColaboradorEstadoCommand cmd,
                                                                String userEmail, Set<String> roles) {
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        Profesional profesional = findProfesional(cmd.consultorioId(), cmd.colaboradorId());
        if (cmd.activo()) {
            profesional.activate();
        } else {
            LocalDate fecha = cmd.fechaDeBaja() != null ? cmd.fechaDeBaja() : LocalDate.now();
            String motivo = normalize(cmd.motivoDeBaja());
            if (motivo == null) {
                throw new IllegalArgumentException("El motivo de baja es obligatorio.");
            }
            profesional.inactivate(fecha, motivo);
        }
        return toProfesionalResult(profesionalRepo.save(profesional));
    }

    public ColaboradorProfesionalResult crearCuentaProfesional(UUID consultorioId, UUID profesionalId, String requestedEmail,
                                                               String userEmail, Set<String> roles) {
        assertCanWrite(consultorioId, userEmail, roles);
        Profesional profesional = findProfesional(consultorioId, profesionalId);
        if (profesional.getUserId() != null) {
            throw new IllegalArgumentException("El profesional ya tiene cuenta vinculada.");
        }
        String email = normalize(requestedEmail);
        if (email == null) {
            email = normalize(profesional.getEmail());
        }
        if (email == null) {
            throw new IllegalArgumentException("Email obligatorio para crear cuenta.");
        }

        User user = createPendingUserForCollaborator(
                email,
                profesional.getNombre(),
                profesional.getApellido(),
                profesional.getTelefono(),
                RoleName.PROFESIONAL,
                MembershipRole.PROFESIONAL,
                consultorioId
        );
        if (!Objects.equals(profesional.getEmail(), email)) {
            profesional.update(
                    profesional.getNombre(),
                    profesional.getApellido(),
                    profesional.getNroDocumento(),
                    profesional.getMatricula(),
                    profesional.getEspecialidad(),
                    profesional.getEspecialidades(),
                    email,
                    profesional.getTelefono(),
                    profesional.getDomicilio(),
                    profesional.getFotoPerfilUrl()
            );
        }
        profesional.linkUser(user.getId());
        return toProfesionalResult(profesionalRepo.save(profesional));
    }

    public ColaboradorProfesionalResult reenviarActivacionProfesional(UUID consultorioId, UUID profesionalId,
                                                                      String userEmail, Set<String> roles) {
        assertCanWrite(consultorioId, userEmail, roles);
        Profesional profesional = findProfesional(consultorioId, profesionalId);
        if (profesional.getUserId() == null) {
            throw new IllegalArgumentException("El profesional no tiene cuenta vinculada.");
        }
        User user = userRepo.findById(profesional.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta vinculada no encontrada."));
        resendActivationForUser(user);
        return toProfesionalResult(profesional);
    }

    @Transactional(readOnly = true)
    public List<ColaboradorEmpleadoResult> listEmpleados(UUID consultorioId, String userEmail, Set<String> roles,
                                                         String q, String cargo, Boolean activo) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        return empleadoRepo.findByConsultorioId(consultorioId).stream()
                .filter(e -> q == null || q.isBlank() || matchesQuery(e.getNombre(), e.getApellido(), e.getEmail(), q))
                .filter(e -> cargo == null || cargo.isBlank() || containsIgnoreCase(e.getCargo(), cargo))
                .filter(e -> activo == null || e.isActivo() == activo)
                .map(this::toEmpleadoResult)
                .toList();
    }

    @Transactional(readOnly = true)
    public ColaboradorEmpleadoResult getEmpleado(UUID consultorioId, UUID empleadoId,
                                                 String userEmail, Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        return toEmpleadoResult(findEmpleado(consultorioId, empleadoId));
    }

    public ColaboradorEmpleadoResult createEmpleado(CreateEmpleadoCommand cmd,
                                                    String userEmail, Set<String> roles) {
        assertConsultorioExists(cmd.consultorioId());
        assertCanWrite(cmd.consultorioId(), userEmail, roles);

        String email = normalize(cmd.email());
        if (email == null) {
            throw new IllegalArgumentException("Email obligatorio.");
        }
        if (empleadoRepo.existsByConsultorioIdAndEmail(cmd.consultorioId(), email)) {
            throw new IllegalArgumentException("Ya existe un empleado con ese email en el consultorio.");
        }

        User user = createPendingUserForCollaborator(
                email,
                cmd.nombre(),
                cmd.apellido(),
                cmd.telefono(),
                RoleName.ADMINISTRATIVO,
                MembershipRole.ADMINISTRATIVO,
                cmd.consultorioId()
        );

        Empleado empleado = new Empleado(
                UUID.randomUUID(),
                cmd.consultorioId(),
                user.getId(),
                cmd.nombre(),
                cmd.apellido(),
                normalize(cmd.dni()),
                cmd.cargo(),
                normalize(cmd.nroLegajo()),
                email,
                normalize(cmd.telefono()),
                normalize(cmd.notasInternas()),
                LocalDate.now(),
                null,
                null,
                true,
                Instant.now(),
                Instant.now()
        );
        return toEmpleadoResult(empleadoRepo.save(empleado));
    }

    public ColaboradorEmpleadoResult updateEmpleado(UpdateEmpleadoCommand cmd,
                                                    String userEmail, Set<String> roles) {
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        Empleado empleado = findEmpleado(cmd.consultorioId(), cmd.empleadoId());

        String email = normalize(cmd.email());
        if (email == null) {
            throw new IllegalArgumentException("Email obligatorio.");
        }
        if (empleadoRepo.existsByConsultorioIdAndEmailAndIdNot(cmd.consultorioId(), email, cmd.empleadoId())) {
            throw new IllegalArgumentException("Ya existe un empleado con ese email en el consultorio.");
        }
        String dni = normalize(cmd.dni());
        if (dni != null && empleadoRepo.existsByConsultorioIdAndDniAndIdNot(cmd.consultorioId(), dni, cmd.empleadoId())) {
            throw new IllegalArgumentException("Ya existe un empleado con ese DNI en el consultorio.");
        }

        empleado.update(
                cmd.nombre(),
                cmd.apellido(),
                dni,
                cmd.cargo(),
                normalize(cmd.nroLegajo()),
                email,
                normalize(cmd.telefono()),
                normalize(cmd.notasInternas())
        );
        return toEmpleadoResult(empleadoRepo.save(empleado));
    }

    public ColaboradorEmpleadoResult changeEmpleadoEstado(ChangeColaboradorEstadoCommand cmd,
                                                          String userEmail, Set<String> roles) {
        assertCanWrite(cmd.consultorioId(), userEmail, roles);
        Empleado empleado = findEmpleado(cmd.consultorioId(), cmd.colaboradorId());
        if (cmd.activo()) {
            empleado.activate();
        } else {
            LocalDate fecha = cmd.fechaDeBaja() != null ? cmd.fechaDeBaja() : LocalDate.now();
            String motivo = normalize(cmd.motivoDeBaja());
            if (motivo == null) {
                throw new IllegalArgumentException("El motivo de baja es obligatorio.");
            }
            empleado.inactivate(fecha, motivo);
        }
        return toEmpleadoResult(empleadoRepo.save(empleado));
    }

    public ColaboradorEmpleadoResult reenviarActivacionEmpleado(UUID consultorioId, UUID empleadoId,
                                                                String userEmail, Set<String> roles) {
        assertCanWrite(consultorioId, userEmail, roles);
        Empleado empleado = findEmpleado(consultorioId, empleadoId);
        if (empleado.getUserId() == null) {
            throw new IllegalArgumentException("El empleado no tiene cuenta vinculada.");
        }
        User user = userRepo.findById(empleado.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta vinculada no encontrada."));
        resendActivationForUser(user);
        return toEmpleadoResult(empleado);
    }

    private Profesional findProfesional(UUID consultorioId, UUID profesionalId) {
        return profesionalRepo.findById(profesionalId)
                .filter(p -> p.getConsultorioId().equals(consultorioId))
                .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado"));
    }

    private Empleado findEmpleado(UUID consultorioId, UUID empleadoId) {
        return empleadoRepo.findByConsultorioIdAndId(consultorioId, empleadoId)
                .orElseThrow(() -> new EmpleadoNotFoundException("Empleado no encontrado"));
    }

    private User createPendingUserForCollaborator(String email, String firstName, String lastName, String phone,
                                                  RoleName roleName, MembershipRole membershipRole, UUID consultorioId) {
        if (userRepo.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }
        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName.name()));

        User user = new User(
                UUID.randomUUID(),
                email,
                passwordEncoder.encode(UUID.randomUUID().toString()),
                firstName,
                lastName,
                phone,
                UserStatus.PENDING,
                Instant.now()
        );
        user.addRole(role);
        User saved = userRepo.save(user);

        membershipRepo.save(new Membership(
                UUID.randomUUID(),
                saved.getId(),
                consultorioId,
                membershipRole,
                MembershipStatus.ACTIVE,
                Instant.now()
        ));

        sendActivationEmail(saved);
        return saved;
    }

    private void resendActivationForUser(User user) {
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalArgumentException("La cuenta ya esta activa.");
        }
        if (user.getStatus() == UserStatus.REJECTED) {
            user.markPending();
            userRepo.save(user);
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

    private ColaboradorProfesionalResult toProfesionalResult(Profesional p) {
        List<String> especialidades = parseEspecialidades(p.getEspecialidades());
        ColaboradorCuentaStatus cuentaStatus = resolveCuentaStatus(p.getUserId());
        ColaboradorEstado estado = resolveEstado(p.isActivo(), cuentaStatus);
        Instant ultimoEnvio = p.getUserId() == null
                ? null
                : activationTokenRepo.findLastCreatedAtByUserId(p.getUserId()).orElse(null);
        return new ColaboradorProfesionalResult(
                p.getId(),
                p.getConsultorioId(),
                p.getUserId(),
                p.getNombre(),
                p.getApellido(),
                p.getNroDocumento(),
                p.getMatricula(),
                especialidades,
                p.getEmail(),
                p.getTelefono(),
                p.getDomicilio(),
                p.getFotoPerfilUrl(),
                p.getFechaAlta(),
                p.getFechaBaja(),
                p.getMotivoBaja(),
                p.isActivo(),
                estado,
                cuentaStatus,
                ultimoEnvio,
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    private ColaboradorEmpleadoResult toEmpleadoResult(Empleado e) {
        ColaboradorCuentaStatus cuentaStatus = resolveCuentaStatus(e.getUserId());
        ColaboradorEstado estado = resolveEstado(e.isActivo(), cuentaStatus);
        Instant ultimoEnvio = e.getUserId() == null
                ? null
                : activationTokenRepo.findLastCreatedAtByUserId(e.getUserId()).orElse(null);
        return new ColaboradorEmpleadoResult(
                e.getId(),
                e.getConsultorioId(),
                e.getUserId(),
                e.getNombre(),
                e.getApellido(),
                e.getDni(),
                e.getCargo(),
                e.getNroLegajo(),
                e.getEmail(),
                e.getTelefono(),
                e.getNotasInternas(),
                e.getFechaAlta(),
                e.getFechaBaja(),
                e.getMotivoBaja(),
                e.isActivo(),
                estado,
                cuentaStatus,
                ultimoEnvio,
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private ColaboradorCuentaStatus resolveCuentaStatus(UUID userId) {
        if (userId == null) return ColaboradorCuentaStatus.NONE;
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return ColaboradorCuentaStatus.NONE;
        return switch (user.getStatus()) {
            case PENDING -> ColaboradorCuentaStatus.PENDING;
            case REJECTED -> ColaboradorCuentaStatus.REJECTED;
            case ACTIVE, SUSPENDED -> ColaboradorCuentaStatus.ACTIVE;
        };
    }

    private ColaboradorEstado resolveEstado(boolean activo, ColaboradorCuentaStatus cuentaStatus) {
        if (!activo) return ColaboradorEstado.INACTIVO;
        if (cuentaStatus == ColaboradorCuentaStatus.PENDING) return ColaboradorEstado.INVITADO;
        if (cuentaStatus == ColaboradorCuentaStatus.REJECTED) return ColaboradorEstado.RECHAZADO;
        return ColaboradorEstado.ACTIVO;
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

    private boolean matchesQuery(String nombre, String apellido, String email, String q) {
        return containsIgnoreCase(nombre, q)
                || containsIgnoreCase(apellido, q)
                || containsIgnoreCase(nombre + " " + apellido, q)
                || containsIgnoreCase(email, q);
    }

    private boolean hasAnyEspecialidad(String especialidadesCsv, List<String> filtros) {
        if (filtros == null || filtros.isEmpty()) return true;
        Set<String> current = parseEspecialidades(especialidadesCsv).stream()
                .map(v -> v.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        for (String filtro : filtros) {
            String normalized = normalize(filtro);
            if (normalized != null && current.contains(normalized.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private String normalizeEspecialidades(List<String> especialidades) {
        if (especialidades == null) {
            throw new IllegalArgumentException("Debe seleccionar al menos una especialidad.");
        }
        List<String> values = especialidades.stream()
                .map(this::normalize)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una especialidad.");
        }
        return String.join("|", values);
    }

    private List<String> parseEspecialidades(String especialidadesCsv) {
        if (especialidadesCsv == null || especialidadesCsv.isBlank()) return List.of();
        return Arrays.stream(especialidadesCsv.split("\\|"))
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .toList();
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String normalizeModoAlta(String modoAlta) {
        String value = normalize(modoAlta);
        if (value == null) return "DIRECTA";
        String upper = value.toUpperCase(Locale.ROOT);
        if (!upper.equals("DIRECTA") && !upper.equals("INVITACION")) {
            throw new IllegalArgumentException("modoAlta invalido. Valores permitidos: DIRECTA, INVITACION.");
        }
        return upper;
    }

    private boolean containsIgnoreCase(String source, String query) {
        if (source == null || query == null) return false;
        return source.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
