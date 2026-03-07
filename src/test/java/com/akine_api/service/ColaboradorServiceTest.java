package com.akine_api.service;

import com.akine_api.application.dto.command.ChangeColaboradorEstadoCommand;
import com.akine_api.application.dto.command.UpdateColaboradorProfesionalCommand;
import com.akine_api.application.dto.result.ColaboradorProfesionalResult;
import com.akine_api.application.port.output.*;
import com.akine_api.application.service.ColaboradorService;
import com.akine_api.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColaboradorServiceTest {

    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock RoleRepositoryPort roleRepo;
    @Mock MembershipRepositoryPort membershipRepo;
    @Mock ActivationTokenRepositoryPort activationTokenRepo;
    @Mock PasswordEncoderPort passwordEncoder;
    @Mock EmailPort emailPort;
    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock EmpleadoRepositoryPort empleadoRepo;
    @Mock CargoEmpleadoCatalogoRepositoryPort cargoEmpleadoCatalogoRepo;

    private ColaboradorService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PROFESIONAL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");

    @BeforeEach
    void setUp() {
        service = new ColaboradorService(
                consultorioRepo, userRepo, roleRepo, membershipRepo, activationTokenRepo,
                passwordEncoder, emailPort, profesionalRepo, empleadoRepo, cargoEmpleadoCatalogoRepo
        );
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));
    }

    @Test
    void updateProfesional_emailChanged_withLinkedUser_marksPendingAndSendsActivation() {
        Profesional profesional = profesional(USER_ID, "old@akine.com", true);
        User linkedUser = userWithRole(USER_ID, "old@akine.com", UserStatus.ACTIVE, RoleName.PROFESIONAL);

        when(profesionalRepo.findById(PROFESIONAL_ID)).thenReturn(Optional.of(profesional));
        when(profesionalRepo.existsByMatriculaAndConsultorioIdAndIdNot(any(), any(), any())).thenReturn(false);
        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(linkedUser));
        when(userRepo.findByEmail("new@akine.com")).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activationTokenRepo.save(any(ActivationToken.class))).thenAnswer(inv -> inv.getArgument(0));
        when(profesionalRepo.save(any(Profesional.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateColaboradorProfesionalCommand cmd = new UpdateColaboradorProfesionalCommand(
                CONSULTORIO_ID,
                PROFESIONAL_ID,
                "Raul",
                "Arena",
                "12345678",
                "MP-23",
                List.of("Kinesiologia"),
                "new@akine.com",
                "1111",
                "Dir",
                null
        );

        ColaboradorProfesionalResult result = service.updateProfesional(cmd, "admin@akine.com", ADMIN_ROLES);

        assertThat(result.email()).isEqualTo("new@akine.com");
        assertThat(linkedUser.getEmail()).isEqualTo("new@akine.com");
        assertThat(linkedUser.getStatus()).isEqualTo(UserStatus.PENDING);
        verify(activationTokenRepo).deleteByUserId(USER_ID);
        verify(emailPort).sendActivationEmail(eq("new@akine.com"), eq(linkedUser.getFirstName()), anyString());
    }

    @Test
    void changeProfesionalEstado_desactivar_suspendsLinkedUser() {
        Profesional profesional = profesional(USER_ID, "prof@akine.com", true);
        User linkedUser = userWithRole(USER_ID, "prof@akine.com", UserStatus.ACTIVE, RoleName.PROFESIONAL);

        when(profesionalRepo.findById(PROFESIONAL_ID)).thenReturn(Optional.of(profesional));
        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(linkedUser));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(profesionalRepo.save(any(Profesional.class))).thenAnswer(inv -> inv.getArgument(0));

        service.changeProfesionalEstado(
                new ChangeColaboradorEstadoCommand(CONSULTORIO_ID, PROFESIONAL_ID, false, LocalDate.now(), "Baja"),
                "admin@akine.com",
                ADMIN_ROLES
        );

        assertThat(profesional.isActivo()).isFalse();
        assertThat(linkedUser.getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    void changeProfesionalEstado_reactivar_suspendedUser_activatesAndSendsMail() {
        Profesional profesional = profesional(USER_ID, "prof@akine.com", false);
        User linkedUser = userWithRole(USER_ID, "prof@akine.com", UserStatus.SUSPENDED, RoleName.PROFESIONAL);

        when(profesionalRepo.findById(PROFESIONAL_ID)).thenReturn(Optional.of(profesional));
        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(linkedUser));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(profesionalRepo.save(any(Profesional.class))).thenAnswer(inv -> inv.getArgument(0));

        service.changeProfesionalEstado(
                new ChangeColaboradorEstadoCommand(CONSULTORIO_ID, PROFESIONAL_ID, true, null, null),
                "admin@akine.com",
                ADMIN_ROLES
        );

        assertThat(profesional.isActivo()).isTrue();
        assertThat(linkedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(emailPort).sendAccountReactivatedEmail("prof@akine.com", linkedUser.getFirstName());
    }

    @Test
    void crearCuentaProfesional_existingUserFromAnotherConsultorio_reusesAndLinks() {
        Profesional profesional = profesional(null, "exist@akine.com", true);
        User existingUser = userWithRole(USER_ID, "exist@akine.com", UserStatus.ACTIVE, RoleName.PROFESIONAL);

        when(profesionalRepo.findById(PROFESIONAL_ID)).thenReturn(Optional.of(profesional));
        when(userRepo.findByEmail("exist@akine.com")).thenReturn(Optional.of(existingUser));
        when(profesionalRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(empleadoRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(membershipRepo.findByUserIdAndConsultorioId(USER_ID, CONSULTORIO_ID)).thenReturn(Optional.empty());
        when(membershipRepo.save(any(Membership.class))).thenAnswer(inv -> inv.getArgument(0));
        when(profesionalRepo.save(any(Profesional.class))).thenAnswer(inv -> inv.getArgument(0));

        ColaboradorProfesionalResult result = service.crearCuentaProfesional(
                CONSULTORIO_ID,
                PROFESIONAL_ID,
                "exist@akine.com",
                "admin@akine.com",
                ADMIN_ROLES
        );

        assertThat(result.userId()).isEqualTo(USER_ID);
        verify(userRepo, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(membershipRepo).save(any(Membership.class));
    }

    @Test
    void crearCuentaProfesional_existingIncompatibleUserInSameConsultorio_throwsConflict() {
        Profesional profesional = profesional(null, "used@akine.com", true);
        User existingUser = userWithRole(USER_ID, "used@akine.com", UserStatus.ACTIVE, RoleName.PROFESIONAL);
        Empleado empleado = empleadoLinkedToUser(USER_ID, CONSULTORIO_ID);

        when(profesionalRepo.findById(PROFESIONAL_ID)).thenReturn(Optional.of(profesional));
        when(userRepo.findByEmail("used@akine.com")).thenReturn(Optional.of(existingUser));
        when(profesionalRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(empleadoRepo.findByUserId(USER_ID)).thenReturn(Optional.of(empleado));

        assertThatThrownBy(() -> service.crearCuentaProfesional(
                CONSULTORIO_ID,
                PROFESIONAL_ID,
                "used@akine.com",
                "admin@akine.com",
                ADMIN_ROLES
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("incompatible");
    }

    private Consultorio activeConsultorio() {
        return new Consultorio(CONSULTORIO_ID, "Consultorio", null, null, null, null, "ACTIVE", Instant.now());
    }

    private Profesional profesional(UUID userId, String email, boolean activo) {
        return new Profesional(
                PROFESIONAL_ID,
                CONSULTORIO_ID,
                userId,
                "Raul",
                "Arena",
                "12345678",
                "MP-23",
                "Kinesiologia",
                "Kinesiologia",
                email,
                "1111",
                null,
                null,
                LocalDate.now(),
                activo ? null : LocalDate.now(),
                activo ? null : "Baja",
                activo,
                Instant.now()
        );
    }

    private User userWithRole(UUID id, String email, UserStatus status, RoleName roleName) {
        User user = new User(
                id,
                email,
                "hash",
                "Raul",
                "Arena",
                "1111",
                status,
                Instant.now()
        );
        user.addRole(new Role(UUID.randomUUID(), roleName, roleName.name()));
        return user;
    }

    private Empleado empleadoLinkedToUser(UUID userId, UUID consultorioId) {
        return new Empleado(
                UUID.randomUUID(),
                consultorioId,
                userId,
                "Admin",
                "User",
                "123",
                LocalDate.of(1990, 1, 1),
                "Recepcion",
                "emp@akine.com",
                null,
                "Direccion 123",
                null,
                LocalDate.now(),
                null,
                null,
                true,
                Instant.now(),
                Instant.now()
        );
    }
}
