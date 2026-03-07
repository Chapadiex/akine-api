package com.akine_api.infrastructure.config;

import com.akine_api.application.port.output.PasswordEncoderPort;
import com.akine_api.application.service.CargoEmpleadoCatalogoBootstrapService;
import com.akine_api.application.service.ConsultorioAntecedenteBootstrapService;
import com.akine_api.application.service.ConsultorioEspecialidadBootstrapService;
import com.akine_api.infrastructure.persistence.entity.RoleEntity;
import com.akine_api.infrastructure.persistence.entity.UserEntity;
import com.akine_api.infrastructure.persistence.repository.ConsultorioJpaRepository;
import com.akine_api.infrastructure.persistence.repository.RoleJpaRepository;
import com.akine_api.infrastructure.persistence.repository.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserJpaRepository userRepo;
    private final RoleJpaRepository roleRepo;
    private final ConsultorioJpaRepository consultorioRepo;
    private final ConsultorioEspecialidadBootstrapService consultorioEspecialidadBootstrapService;
    private final ConsultorioAntecedenteBootstrapService consultorioAntecedenteBootstrapService;
    private final CargoEmpleadoCatalogoBootstrapService cargoEmpleadoCatalogoBootstrapService;
    private final PasswordEncoderPort passwordEncoder;
    private final boolean devMode;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public DataSeeder(UserJpaRepository userRepo,
                      RoleJpaRepository roleRepo,
                      ConsultorioJpaRepository consultorioRepo,
                      ConsultorioEspecialidadBootstrapService consultorioEspecialidadBootstrapService,
                      ConsultorioAntecedenteBootstrapService consultorioAntecedenteBootstrapService,
                      CargoEmpleadoCatalogoBootstrapService cargoEmpleadoCatalogoBootstrapService,
                      PasswordEncoderPort passwordEncoder,
                      @Value("${app.seed.dev-users:false}") boolean devMode) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.consultorioRepo = consultorioRepo;
        this.consultorioEspecialidadBootstrapService = consultorioEspecialidadBootstrapService;
        this.consultorioAntecedenteBootstrapService = consultorioAntecedenteBootstrapService;
        this.cargoEmpleadoCatalogoBootstrapService = cargoEmpleadoCatalogoBootstrapService;
        this.passwordEncoder = passwordEncoder;
        this.devMode = devMode;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedAdminUser();
        seedDefaultsForExistingConsultorios();
        if (devMode) {
            seedDevUsers();
        }
    }

    private void seedAdminUser() {
        if (userRepo.existsByEmail(adminEmail)) {
            log.debug("Admin user already exists, skipping seed.");
            return;
        }
        Optional<RoleEntity> adminRole = roleRepo.findByName("ADMIN");
        if (adminRole.isEmpty()) {
            log.warn("ADMIN role not found in DB. Ensure Flyway migration V3 ran successfully.");
            return;
        }
        userRepo.save(buildUser(adminEmail, adminPassword, "Admin", "AKINE", adminRole.get()));
        log.info("Admin user created: {}", adminEmail);
    }

    private void seedDevUsers() {
        record DevUser(String email, String role, String firstName, String lastName) {}

        List<DevUser> devUsers = List.of(
            new DevUser("paciente@akine.com",       "PACIENTE",          "Lucia",    "Torres"),
            new DevUser("profesional@akine.com",    "PROFESIONAL",       "Martin",   "Fernandez"),
            new DevUser("profadmin@akine.com",      "PROFESIONAL_ADMIN", "Sofia",    "Ramirez"),
            new DevUser("administrativo@akine.com", "ADMINISTRATIVO",    "Ricardo",  "Gomez")
        );

        for (DevUser du : devUsers) {
            if (userRepo.existsByEmail(du.email())) continue;
            Optional<RoleEntity> role = roleRepo.findByName(du.role());
            if (role.isEmpty()) {
                log.warn("Role {} not found, skipping dev user {}", du.role(), du.email());
                continue;
            }
            userRepo.save(buildUser(du.email(), "Test1234!", du.firstName(), du.lastName(), role.get()));
            log.info("[DEV] Test user created: {} ({})", du.email(), du.role());
        }
    }

    private UserEntity buildUser(String email, String password,
                                  String firstName, String lastName, RoleEntity role) {
        UserEntity u = new UserEntity();
        u.setId(UUID.randomUUID());
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setStatus("ACTIVE");
        u.setCreatedAt(Instant.now());
        u.setUpdatedAt(Instant.now());
        u.setRoles(Set.of(role));
        return u;
    }

    private void seedDefaultsForExistingConsultorios() {
        cargoEmpleadoCatalogoBootstrapService.ensureDefaults();
        consultorioRepo.findAll().forEach(c -> {
            try {
                consultorioEspecialidadBootstrapService.enableDefaultsForConsultorio(c.getId());
                consultorioAntecedenteBootstrapService.ensureDefaults(c.getId(), "system");
            } catch (Exception ex) {
                log.error("No se pudieron sembrar defaults para consultorio {}", c.getId(), ex);
            }
        });
    }
}
