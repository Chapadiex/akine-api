package com.akine_api.application.service;

import com.akine_api.application.dto.command.*;
import com.akine_api.application.dto.result.CargoEmpleadoCatalogoResult;
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
    private final CargoEmpleadoCatalogoRepositoryPort cargoEmpleadoCatalogoRepo;

    public ColaboradorService(ConsultorioRepositoryPort consultorioRepo,
                              UserRepositoryPort userRepo,
                              RoleRepositoryPort roleRepo,
                              MembershipRepositoryPort membershipRepo,
                              ActivationTokenRepositoryPort activationTokenRepo,
                              PasswordEncoderPort passwordEncoder,
                              EmailPort emailPort,
                              ProfesionalRepositoryPort profesionalRepo,
                              EmpleadoRepositoryPort empleadoRepo,
                              CargoEmpleadoCatalogoRepositoryPort cargoEmpleadoCatalogoRepo) {
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.membershipRepo = membershipRepo;
        this.activationTokenRepo = activationTokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailPort = emailPort;
        this.profesionalRepo = profesionalRepo;
        this.empleadoRepo = empleadoRepo;
        this.cargoEmpleadoCatalogoRepo = cargoEmpleadoCatalogoRepo;
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
            User user = resolveOrCreateProfessionalUser(
                    email,
                    cmd.nombre(),
                    cmd.apellido(),
                    cmd.telefono(),
                    cmd.consultorioId(),
                    null
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
        String email = normalize(cmd.email());
        String currentEmail = normalize(profesional.getEmail());
        boolean emailChanged = !Objects.equals(currentEmail, email);

        if (profesional.getUserId() != null && emailChanged) {
            if (email == null) {
                throw new IllegalArgumentException("Email obligatorio para profesionales con cuenta vinculada.");
            }
            User linkedUser = userRepo.findById(profesional.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Cuenta vinculada no encontrada."));
            Optional<User> existingWithEmail = userRepo.findByEmail(email);
            if (existingWithEmail.isPresent() && !existingWithEmail.get().getId().equals(linkedUser.getId())) {
                throw new IllegalArgumentException("El email ingresado ya pertenece a otra cuenta.");
            }
            linkedUser.updateEmail(email);
            linkedUser.markPending();
            userRepo.save(linkedUser);
            activationTokenRepo.deleteByUserId(linkedUser.getId());
            sendActivationEmail(linkedUser);
        }

        profesional.update(
                cmd.nombre(),
                cmd.apellido(),
                dni,
                cmd.matricula(),
                especialidadPrincipal,
                especialidadesCsv,
                email,
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
            if (profesional.getUserId() != null) {
                User user = userRepo.findById(profesional.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("Cuenta vinculada no encontrada."));
                if (user.getStatus() == UserStatus.SUSPENDED) {
                    user.activate();
                    userRepo.save(user);
                    emailPort.sendAccountReactivatedEmail(user.getEmail(), user.getFirstName());
                }
            }
        } else {
            LocalDate fecha = cmd.fechaDeBaja() != null ? cmd.fechaDeBaja() : LocalDate.now();
            String motivo = normalize(cmd.motivoDeBaja());
            if (motivo == null) {
                throw new IllegalArgumentException("El motivo de baja es obligatorio.");
            }
            profesional.inactivate(fecha, motivo);
            if (profesional.getUserId() != null) {
                User user = userRepo.findById(profesional.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("Cuenta vinculada no encontrada."));
                user.suspend();
                userRepo.save(user);
            }
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

        User user = resolveOrCreateProfessionalUser(
                email,
                profesional.getNombre(),
                profesional.getApellido(),
                profesional.getTelefono(),
                consultorioId,
                profesionalId
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

    private User resolveOrCreateProfessionalUser(String email, String firstName, String lastName, String phone,
                                                 UUID consultorioId, UUID targetProfesionalId) {
        Optional<User> existing = userRepo.findByEmail(email);
        if (existing.isPresent()) {
            return reuseExistingProfessionalUser(existing.get(), consultorioId, targetProfesionalId);
        }
        return createPendingUserForCollaborator(
                email,
                firstName,
                lastName,
                phone,
                RoleName.PROFESIONAL,
                MembershipRole.PROFESIONAL,
                consultorioId
        );
    }

    private User reuseExistingProfessionalUser(User user, UUID consultorioId, UUID targetProfesionalId) {
        assertCompatibleUserForProfessionalLink(user.getId(), consultorioId, targetProfesionalId);
        ensureProfessionalRole(user);
        ensureProfessionalMembership(user.getId(), consultorioId);

        if (user.getStatus() == UserStatus.REJECTED || user.getStatus() == UserStatus.PENDING) {
            resendActivationForUser(user);
            return user;
        }
        if (user.getStatus() == UserStatus.SUSPENDED) {
            user.activate();
            userRepo.save(user);
            emailPort.sendAccountReactivatedEmail(user.getEmail(), user.getFirstName());
        }
        return user;
    }

    private void assertCompatibleUserForProfessionalLink(UUID userId, UUID consultorioId, UUID targetProfesionalId) {
        Optional<Profesional> linkedProfesional = profesionalRepo.findByUserId(userId);
        if (linkedProfesional.isPresent()) {
            boolean isTarget = targetProfesionalId != null && linkedProfesional.get().getId().equals(targetProfesionalId);
            if (!isTarget) {
                throw new IllegalArgumentException("El email ya esta vinculado a otro profesional.");
            }
        }

        empleadoRepo.findByUserId(userId)
                .filter(e -> e.getConsultorioId().equals(consultorioId))
                .ifPresent(e -> {
                    throw new IllegalArgumentException("El email ya esta vinculado a un colaborador incompatible en este consultorio.");
                });

        membershipRepo.findByUserIdAndConsultorioId(userId, consultorioId)
                .filter(m -> m.getRoleInConsultorio() != MembershipRole.PROFESIONAL)
                .ifPresent(m -> {
                    throw new IllegalArgumentException("El email ya pertenece a otro tipo de colaborador en este consultorio.");
                });
    }

    private void ensureProfessionalMembership(UUID userId, UUID consultorioId) {
        Optional<Membership> existingMembership = membershipRepo.findByUserIdAndConsultorioId(userId, consultorioId);
        if (existingMembership.isPresent()) {
            Membership membership = existingMembership.get();
            if (membership.getRoleInConsultorio() != MembershipRole.PROFESIONAL) {
                throw new IllegalArgumentException("El usuario ya tiene una membresia incompatible en este consultorio.");
            }
            if (membership.getStatus() != MembershipStatus.ACTIVE) {
                membership.activate();
                membershipRepo.save(membership);
            }
            return;
        }

        membershipRepo.save(new Membership(
                UUID.randomUUID(),
                userId,
                consultorioId,
                MembershipRole.PROFESIONAL,
                MembershipStatus.ACTIVE,
                Instant.now()
        ));
    }

    private void ensureProfessionalRole(User user) {
        if (user.getRoles().stream().noneMatch(r -> r.getName() == RoleName.PROFESIONAL)) {
            Role role = roleRepo.findByName(RoleName.PROFESIONAL)
                    .orElseThrow(() -> new RoleNotFoundException(RoleName.PROFESIONAL.name()));
            user.addRole(role);
            userRepo.save(user);
        }
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
    public List<CargoEmpleadoCatalogoResult> listCargosEmpleado(UUID consultorioId,
                                                                 String search,
                                                                 boolean includeInactive,
                                                                 String userEmail,
                                                                 Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanRead(consultorioId, userEmail, roles);
        return (includeInactive ? cargoEmpleadoCatalogoRepo.findAllOrdered() : cargoEmpleadoCatalogoRepo.findActiveOrdered())
                .stream()
                .filter(item -> search == null || search.isBlank()
                        || containsIgnoreCase(item.getNombre(), search)
                        || containsIgnoreCase(item.getSlug(), search))
                .map(this::toCargoCatalogoResult)
                .toList();
    }

    public CargoEmpleadoCatalogoResult createCargoEmpleado(UUID consultorioId,
                                                           String nombre,
                                                           String userEmail,
                                                           Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);
        String cleanedName = validateCargoNombre(nombre);
        String slug = normalizeSlug(cleanedName);
        if (slug.isBlank()) {
            throw new IllegalArgumentException("El nombre no es valido para generar slug.");
        }

        Optional<CargoEmpleadoCatalogo> existing = cargoEmpleadoCatalogoRepo.findBySlug(slug);
        if (existing.isPresent()) {
            CargoEmpleadoCatalogo cargo = existing.get();
            if (!cargo.isActivo()) {
                cargo.rename(cleanedName, slug);
                cargo.activate();
                return toCargoCatalogoResult(cargoEmpleadoCatalogoRepo.save(cargo));
            }
            throw new IllegalArgumentException("Ya existe un cargo con ese nombre.");
        }

        int nextOrder = cargoEmpleadoCatalogoRepo.findAllOrdered().stream()
                .mapToInt(CargoEmpleadoCatalogo::getOrden)
                .max()
                .orElse(0) + 10;

        CargoEmpleadoCatalogo created = new CargoEmpleadoCatalogo(
                UUID.randomUUID(),
                cleanedName,
                slug,
                true,
                nextOrder,
                Instant.now(),
                Instant.now()
        );
        return toCargoCatalogoResult(cargoEmpleadoCatalogoRepo.save(created));
    }

    public CargoEmpleadoCatalogoResult updateCargoEmpleado(UUID consultorioId,
                                                           UUID cargoId,
                                                           String nombre,
                                                           String userEmail,
                                                           Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);

        CargoEmpleadoCatalogo cargo = cargoEmpleadoCatalogoRepo.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado."));
        String oldName = cargo.getNombre();
        String cleanedName = validateCargoNombre(nombre);
        String slug = normalizeSlug(cleanedName);
        if (slug.isBlank()) {
            throw new IllegalArgumentException("El nombre no es valido para generar slug.");
        }
        cargoEmpleadoCatalogoRepo.findBySlug(slug)
                .filter(other -> !other.getId().equals(cargoId))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Ya existe un cargo con ese nombre.");
                });

        cargo.rename(cleanedName, slug);
        CargoEmpleadoCatalogo saved = cargoEmpleadoCatalogoRepo.save(cargo);
        if (!oldName.equals(cleanedName)) {
            empleadoRepo.updateCargoNombre(oldName, cleanedName);
        }
        return toCargoCatalogoResult(saved);
    }

    public CargoEmpleadoCatalogoResult activateCargoEmpleado(UUID consultorioId,
                                                             UUID cargoId,
                                                             String userEmail,
                                                             Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);
        CargoEmpleadoCatalogo cargo = cargoEmpleadoCatalogoRepo.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado."));
        if (!cargo.isActivo()) {
            cargo.activate();
            cargo = cargoEmpleadoCatalogoRepo.save(cargo);
        }
        return toCargoCatalogoResult(cargo);
    }

    public CargoEmpleadoCatalogoResult deactivateCargoEmpleado(UUID consultorioId,
                                                               UUID cargoId,
                                                               String userEmail,
                                                               Set<String> roles) {
        assertConsultorioExists(consultorioId);
        assertCanWrite(consultorioId, userEmail, roles);
        CargoEmpleadoCatalogo cargo = cargoEmpleadoCatalogoRepo.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado."));
        if (cargo.isActivo()) {
            cargo.deactivate();
            cargo = cargoEmpleadoCatalogoRepo.save(cargo);
        }
        return toCargoCatalogoResult(cargo);
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
        String dni = normalize(cmd.dni());
        if (dni == null) {
            throw new IllegalArgumentException("DNI obligatorio.");
        }
        if (empleadoRepo.existsByConsultorioIdAndDni(cmd.consultorioId(), dni)) {
            throw new IllegalArgumentException("Ya existe un empleado con ese DNI en el consultorio.");
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
                dni,
                cmd.fechaNacimiento(),
                resolveCargoCanonico(cmd.cargo()),
                email,
                normalize(cmd.telefono()),
                normalize(cmd.direccion()),
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
        if (dni == null) {
            throw new IllegalArgumentException("DNI obligatorio.");
        }
        if (empleadoRepo.existsByConsultorioIdAndDniAndIdNot(cmd.consultorioId(), dni, cmd.empleadoId())) {
            throw new IllegalArgumentException("Ya existe un empleado con ese DNI en el consultorio.");
        }

        empleado.update(
                cmd.nombre(),
                cmd.apellido(),
                dni,
                cmd.fechaNacimiento(),
                resolveCargoCanonicoForUpdate(cmd.cargo(), empleado.getCargo()),
                email,
                normalize(cmd.telefono()),
                normalize(cmd.direccion()),
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
                e.getFechaNacimiento(),
                e.getCargo(),
                e.getEmail(),
                e.getTelefono(),
                e.getDireccion(),
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
                .filter(Objects::nonNull)
                .flatMap(value -> Arrays.stream(value.split("[|,]")))
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
        return Arrays.stream(especialidadesCsv.split("[|,]"))
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

    private String resolveCargoCanonico(String cargoInput) {
        String normalized = normalize(cargoInput);
        if (normalized == null) {
            throw new IllegalArgumentException("Cargo obligatorio.");
        }
        String slug = normalizeSlug(normalized);
        Optional<CargoEmpleadoCatalogo> bySlug = cargoEmpleadoCatalogoRepo.findBySlugAndActive(slug);
        if (bySlug.isPresent()) {
            return bySlug.get().getNombre();
        }
        return cargoEmpleadoCatalogoRepo.findActiveOrdered().stream()
                .filter(c -> normalized.equalsIgnoreCase(c.getNombre()))
                .findFirst()
                .map(CargoEmpleadoCatalogo::getNombre)
                .orElseThrow(() -> new IllegalArgumentException("Cargo invalido. Selecciona un cargo disponible."));
    }

    private String resolveCargoCanonicoForUpdate(String cargoInput, String cargoActual) {
        String normalized = normalize(cargoInput);
        if (normalized == null) {
            throw new IllegalArgumentException("Cargo obligatorio.");
        }
        String slug = normalizeSlug(normalized);
        Optional<CargoEmpleadoCatalogo> activo = cargoEmpleadoCatalogoRepo.findBySlugAndActive(slug);
        if (activo.isPresent()) {
            return activo.get().getNombre();
        }
        Optional<CargoEmpleadoCatalogo> byName = cargoEmpleadoCatalogoRepo.findActiveOrdered().stream()
                .filter(c -> normalized.equalsIgnoreCase(c.getNombre()))
                .findFirst();
        if (byName.isPresent()) {
            return byName.get().getNombre();
        }
        if (cargoActual != null && normalizeSlug(cargoActual).equals(slug)) {
            return cargoActual;
        }
        throw new IllegalArgumentException("Cargo invalido. Selecciona un cargo disponible.");
    }

    private String validateCargoNombre(String nombre) {
        if (nombre == null) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        String cleaned = nombre.trim();
        if (cleaned.length() < 3 || cleaned.length() > 80) {
            throw new IllegalArgumentException("El nombre debe tener entre 3 y 80 caracteres.");
        }
        return cleaned;
    }

    private CargoEmpleadoCatalogoResult toCargoCatalogoResult(CargoEmpleadoCatalogo cargo) {
        return new CargoEmpleadoCatalogoResult(
                cargo.getId(),
                cargo.getNombre(),
                cargo.getSlug(),
                cargo.isActivo(),
                cargo.getCreatedAt(),
                cargo.getUpdatedAt()
        );
    }

    private String normalizeSlug(String value) {
        return java.text.Normalizer.normalize(value == null ? "" : value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "")
                .replaceAll("-{2,}", "-");
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
