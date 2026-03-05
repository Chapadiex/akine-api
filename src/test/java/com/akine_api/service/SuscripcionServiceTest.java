package com.akine_api.service;

import com.akine_api.application.dto.command.ApproveSubscriptionCommand;
import com.akine_api.application.dto.command.CreateSubscriptionCommand;
import com.akine_api.application.dto.result.SubscriptionSummaryResult;
import com.akine_api.application.port.output.*;
import com.akine_api.application.service.SuscripcionService;
import com.akine_api.domain.exception.SubscriptionStateException;
import com.akine_api.domain.exception.UserAlreadyExistsException;
import com.akine_api.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuscripcionServiceTest {

    @Mock UserRepositoryPort userRepo;
    @Mock RoleRepositoryPort roleRepo;
    @Mock PasswordEncoderPort passwordEncoder;
    @Mock EmpresaRepositoryPort empresaRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock MembershipRepositoryPort membershipRepo;
    @Mock SuscripcionRepositoryPort suscripcionRepo;
    @Mock SuscripcionAuditoriaRepositoryPort auditoriaRepo;
    @Mock EmailPort emailPort;

    SuscripcionService service;

    @BeforeEach
    void setUp() {
        service = new SuscripcionService(
                userRepo,
                roleRepo,
                passwordEncoder,
                empresaRepo,
                consultorioRepo,
                membershipRepo,
                suscripcionRepo,
                auditoriaRepo,
                emailPort
        );
    }

    @Test
    void createSubscription_createsPendingEntitiesAndSendsEmail() {
        AtomicReference<User> savedUser = new AtomicReference<>();
        AtomicReference<Empresa> savedEmpresa = new AtomicReference<>();
        AtomicReference<Consultorio> savedConsultorio = new AtomicReference<>();
        AtomicReference<Suscripcion> savedSubscription = new AtomicReference<>();

        when(userRepo.existsByEmail("owner@test.com")).thenReturn(false);
        when(empresaRepo.existsByCuit("30711222334")).thenReturn(false);
        when(roleRepo.findByName(RoleName.PROFESIONAL_ADMIN))
                .thenReturn(Optional.of(new Role(UUID.randomUUID(), RoleName.PROFESIONAL_ADMIN, "Profesional admin")));
        when(passwordEncoder.encode("Pass12345")).thenReturn("hashed-password");

        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            savedUser.set(user);
            return user;
        });
        when(empresaRepo.save(any(Empresa.class))).thenAnswer(inv -> {
            Empresa empresa = inv.getArgument(0);
            savedEmpresa.set(empresa);
            return empresa;
        });
        when(consultorioRepo.save(any(Consultorio.class))).thenAnswer(inv -> {
            Consultorio consultorio = inv.getArgument(0);
            savedConsultorio.set(consultorio);
            return consultorio;
        });
        when(membershipRepo.save(any(Membership.class))).thenAnswer(inv -> inv.getArgument(0));
        when(suscripcionRepo.save(any(Suscripcion.class))).thenAnswer(inv -> {
            Suscripcion subscription = inv.getArgument(0);
            savedSubscription.set(subscription);
            return subscription;
        });
        when(auditoriaRepo.save(any(SuscripcionAuditoria.class))).thenAnswer(inv -> inv.getArgument(0));

        when(userRepo.findById(any())).thenAnswer(inv -> Optional.of(savedUser.get()));
        when(empresaRepo.findById(any())).thenAnswer(inv -> Optional.of(savedEmpresa.get()));
        when(consultorioRepo.findById(any())).thenAnswer(inv -> Optional.of(savedConsultorio.get()));

        SubscriptionSummaryResult result = service.createSubscription(new CreateSubscriptionCommand(
                "STARTER",
                "TRIAL",
                "Juan",
                "Perez",
                "20333444556",
                "owner@test.com",
                "+54 11 1234-5678",
                "Pass12345",
                "Empresa SA",
                "30711222334",
                "Calle 123",
                "CABA",
                "Buenos Aires",
                "Consultorio Central",
                "Av Siempre Viva 742",
                "+54 11 5555-5555",
                "contacto@empresa.com"
        ));

        assertThat(result.status()).isEqualTo("PENDING_APPROVAL");
        assertThat(savedUser.get().getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(savedConsultorio.get().getStatus()).isEqualTo("INACTIVE");
        assertThat(savedSubscription.get().getStatus()).isEqualTo(SuscripcionStatus.PENDING_APPROVAL);
        verify(emailPort).sendSubscriptionReceived(eq("owner@test.com"), eq("Juan"), anyString());
        verify(auditoriaRepo).save(any(SuscripcionAuditoria.class));
    }

    @Test
    void createSubscription_duplicateEmail_throwsConflict() {
        when(userRepo.existsByEmail("owner@test.com")).thenReturn(true);

        assertThatThrownBy(() -> service.createSubscription(new CreateSubscriptionCommand(
                "STARTER", "TRIAL",
                "Juan", "Perez", "20333444556", "owner@test.com", "1", "Pass12345",
                "Empresa SA", "30711222334", "Dir", "Ciudad", "Prov",
                "Consultorio", "Dir", "1", "email@test.com"
        ))).isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void approve_pendingSubscription_activatesOwnerConsultorioAndMembership() {
        UUID ownerId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        UUID consultorioId = UUID.randomUUID();
        UUID subscriptionId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(1);

        Suscripcion subscription = new Suscripcion(
                subscriptionId,
                ownerId,
                empresaId,
                consultorioId,
                "STARTER",
                "TRIAL",
                "REVIEW",
                "SIM-123",
                "tracking-token",
                SuscripcionStatus.PENDING_APPROVAL,
                Instant.now(),
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now(),
                Instant.now()
        );
        User owner = new User(
                ownerId,
                "owner@test.com",
                "hash",
                "Owner",
                "One",
                null,
                UserStatus.PENDING,
                Instant.now()
        );
        Consultorio consultorio = new Consultorio(
                consultorioId,
                "Central",
                null,
                "Address",
                "Phone",
                "contact@test.com",
                "INACTIVE",
                empresaId,
                Instant.now()
        );
        Membership membership = new Membership(
                UUID.randomUUID(),
                ownerId,
                consultorioId,
                MembershipRole.PROFESIONAL_ADMIN,
                MembershipStatus.INACTIVE,
                Instant.now()
        );
        Empresa empresa = new Empresa(
                empresaId,
                "Empresa",
                "30711222334",
                "Address",
                "City",
                "Province",
                Instant.now()
        );

        when(suscripcionRepo.expireActiveDue(any())).thenReturn(0);
        when(suscripcionRepo.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(suscripcionRepo.save(any(Suscripcion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.findById(ownerId)).thenReturn(Optional.of(owner));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(consultorioRepo.findById(consultorioId)).thenReturn(Optional.of(consultorio));
        when(consultorioRepo.save(any(Consultorio.class))).thenAnswer(inv -> inv.getArgument(0));
        when(membershipRepo.findByUserIdAndConsultorioId(ownerId, consultorioId)).thenReturn(Optional.of(membership));
        when(membershipRepo.save(any(Membership.class))).thenAnswer(inv -> inv.getArgument(0));
        when(auditoriaRepo.save(any(SuscripcionAuditoria.class))).thenAnswer(inv -> inv.getArgument(0));
        when(empresaRepo.findById(empresaId)).thenReturn(Optional.of(empresa));

        SubscriptionSummaryResult result = service.approve(
                new ApproveSubscriptionCommand(subscriptionId, actorId, startDate, endDate)
        );

        assertThat(result.status()).isEqualTo("ACTIVE");
        assertThat(owner.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(consultorio.getStatus()).isEqualTo("ACTIVE");
        assertThat(membership.getStatus()).isEqualTo(MembershipStatus.ACTIVE);
        verify(emailPort).sendSubscriptionApproved("owner@test.com", "Owner", startDate, endDate);
    }

    @Test
    void approve_withInvalidDateRange_throwsSubscriptionState() {
        assertThatThrownBy(() -> service.approve(new ApproveSubscriptionCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                LocalDate.now()
        ))).isInstanceOf(SubscriptionStateException.class);
    }
}
