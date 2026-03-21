package com.akine_api.service;

import com.akine_api.application.dto.command.ChangePlanCommand;
import com.akine_api.application.dto.command.RenewSubscriptionCommand;
import com.akine_api.application.dto.result.SubscriptionSummaryResult;
import com.akine_api.application.port.output.*;
import com.akine_api.application.service.SuscripcionService;
import com.akine_api.domain.exception.PlanDowngradeNotAllowedException;
import com.akine_api.domain.exception.SubscriptionStateException;
import com.akine_api.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuscripcionServicePhase4Test {

    @Mock UserRepositoryPort userRepo;
    @Mock RoleRepositoryPort roleRepo;
    @Mock PasswordEncoderPort passwordEncoder;
    @Mock EmpresaRepositoryPort empresaRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock MembershipRepositoryPort membershipRepo;
    @Mock SuscripcionRepositoryPort suscripcionRepo;
    @Mock SuscripcionAuditoriaRepositoryPort auditoriaRepo;
    @Mock PlanDefinicionRepositoryPort planRepo;
    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock PacienteConsultorioRepositoryPort pacienteConsultorioRepo;
    @Mock EmailPort emailPort;

    SuscripcionService service;

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID EMPRESA_ID = UUID.randomUUID();
    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID SUB_ID = UUID.randomUUID();
    private static final String OWNER_EMAIL = "owner@test.com";
    private static final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void setUp() {
        service = new SuscripcionService(
                userRepo, roleRepo, passwordEncoder, empresaRepo,
                consultorioRepo, membershipRepo, suscripcionRepo,
                auditoriaRepo, planRepo, profesionalRepo, pacienteConsultorioRepo, emailPort
        );
    }

    private Suscripcion activeSuscripcion() {
        return new Suscripcion(SUB_ID, OWNER_ID, EMPRESA_ID, CONSULTORIO_ID,
                "BASICO", "MONTHLY", "ACTIVE", null, "token",
                SuscripcionStatus.ACTIVE, Instant.now(),
                TODAY, TODAY.plusMonths(1), null, null,
                null, null, null, null, Instant.now(), Instant.now());
    }

    private Suscripcion pendingRenewalSuscripcion() {
        return new Suscripcion(SUB_ID, OWNER_ID, EMPRESA_ID, CONSULTORIO_ID,
                "BASICO", "MONTHLY", "PENDING_RENEWAL", null, "token",
                SuscripcionStatus.PENDING_RENEWAL, Instant.now(),
                TODAY.minusDays(5), TODAY.plusDays(10), null, null,
                null, null, null, null, Instant.now(), Instant.now());
    }

    private Suscripcion expiredSuscripcion() {
        return new Suscripcion(SUB_ID, OWNER_ID, EMPRESA_ID, CONSULTORIO_ID,
                "BASICO", "MONTHLY", "EXPIRED", null, "token",
                SuscripcionStatus.EXPIRED, Instant.now(),
                TODAY.minusMonths(2), TODAY.minusDays(1), null, null,
                null, null, null, null, Instant.now(), Instant.now());
    }

    private User owner() {
        return new User(OWNER_ID, OWNER_EMAIL, "hash", "Owner", "User",
                null, UserStatus.ACTIVE, Instant.now());
    }

    private Empresa empresa() {
        return new Empresa(EMPRESA_ID, "Empresa Test", "CUIT-TEST", "Test", "TestCity", "BA", Instant.now());
    }

    private Consultorio consultorio() {
        return new Consultorio(CONSULTORIO_ID, "Consultorio Test", null, "20123456789",
                "Razon Social", "owner@mail.com", "ACTIVE", EMPRESA_ID, Instant.now());
    }

    private PlanDefinicion basicoPlan() {
        return new PlanDefinicion("BASICO", "Plan Basico", null,
                new BigDecimal("15000"), null, 1, 3, 500,
                false, false, false, false, true, 1);
    }

    private PlanDefinicion profesionalPlan() {
        return new PlanDefinicion("PROFESIONAL", "Plan Profesional", null,
                new BigDecimal("35000"), null, 3, 10, null,
                true, true, true, true, true, 2);
    }

    // ---- getMySuscripcion ----

    @Test
    void getMySuscripcion_returnsLatestSubscription() {
        when(userRepo.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner()));
        when(suscripcionRepo.findTopByOwnerUserId(OWNER_ID)).thenReturn(Optional.of(activeSuscripcion()));
        when(userRepo.findById(OWNER_ID)).thenReturn(Optional.of(owner()));
        when(empresaRepo.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa()));
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));

        SubscriptionSummaryResult result = service.getMySuscripcion(OWNER_EMAIL);

        assertThat(result.status()).isEqualTo("ACTIVE");
        assertThat(result.planCode()).isEqualTo("BASICO");
    }

    // ---- renew ----

    @Test
    void renew_activeSubscription_extendsEndDateByOneMonth() {
        Suscripcion sub = activeSuscripcion();
        LocalDate expectedEnd = sub.getEndDate().plusMonths(1);

        when(suscripcionRepo.findById(SUB_ID)).thenReturn(Optional.of(sub));
        when(userRepo.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner()));
        when(suscripcionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.findById(OWNER_ID)).thenReturn(Optional.of(owner()));
        when(empresaRepo.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa()));
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));

        SubscriptionSummaryResult result = service.renew(
                new RenewSubscriptionCommand(SUB_ID, OWNER_EMAIL, false));

        assertThat(result.status()).isEqualTo("ACTIVE");
        assertThat(result.endDate()).isEqualTo(expectedEnd);
        verify(emailPort).sendSubscriptionRenewed(eq(OWNER_EMAIL), any(), eq(expectedEnd));
    }

    @Test
    void renew_pendingRenewal_transitionsToActive() {
        Suscripcion sub = pendingRenewalSuscripcion();
        when(suscripcionRepo.findById(SUB_ID)).thenReturn(Optional.of(sub));
        when(userRepo.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner()));
        when(suscripcionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.findById(OWNER_ID)).thenReturn(Optional.of(owner()));
        when(empresaRepo.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa()));
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));

        SubscriptionSummaryResult result = service.renew(
                new RenewSubscriptionCommand(SUB_ID, OWNER_EMAIL, false));

        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    void renew_annualBillingCycle_extendsEndDateByOneYear() {
        Suscripcion sub = new Suscripcion(SUB_ID, OWNER_ID, EMPRESA_ID, CONSULTORIO_ID,
                "BASICO", "ANNUAL", "ACTIVE", null, "token",
                SuscripcionStatus.ACTIVE, Instant.now(),
                TODAY, TODAY.plusYears(1), null, null,
                null, null, null, null, Instant.now(), Instant.now());
        LocalDate expectedEnd = sub.getEndDate().plusYears(1);

        when(suscripcionRepo.findById(SUB_ID)).thenReturn(Optional.of(sub));
        when(userRepo.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner()));
        when(suscripcionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.findById(OWNER_ID)).thenReturn(Optional.of(owner()));
        when(empresaRepo.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa()));
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));

        SubscriptionSummaryResult result = service.renew(
                new RenewSubscriptionCommand(SUB_ID, OWNER_EMAIL, false));

        assertThat(result.endDate()).isEqualTo(expectedEnd);
    }

    @Test
    void renew_expiredSubscription_throws() {
        when(suscripcionRepo.findById(SUB_ID)).thenReturn(Optional.of(expiredSuscripcion()));

        assertThatThrownBy(() -> service.renew(
                new RenewSubscriptionCommand(SUB_ID, OWNER_EMAIL, true)))
                .isInstanceOf(SubscriptionStateException.class);
    }

    @Test
    void renew_notOwner_throws() {
        User otherUser = new User(UUID.randomUUID(), "other@test.com", "hash", "Other", "User",
                null, UserStatus.ACTIVE, Instant.now());
        when(suscripcionRepo.findById(SUB_ID)).thenReturn(Optional.of(activeSuscripcion()));
        when(userRepo.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> service.renew(
                new RenewSubscriptionCommand(SUB_ID, "other@test.com", false)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void renew_asAdmin_bypassesOwnerCheck() {
        Suscripcion sub = activeSuscripcion();
        when(suscripcionRepo.findById(SUB_ID)).thenReturn(Optional.of(sub));
        when(suscripcionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.findById(OWNER_ID)).thenReturn(Optional.of(owner()));
        when(empresaRepo.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa()));
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));

        SubscriptionSummaryResult result = service.renew(
                new RenewSubscriptionCommand(SUB_ID, "admin@test.com", true));

        assertThat(result.status()).isEqualTo("ACTIVE");
        verify(userRepo, never()).findByEmail(any()); // no owner check
    }

    // ---- changePlan ----

    @Test
    void changePlan_upgrade_succeeds() {
        Suscripcion sub = activeSuscripcion();
        when(suscripcionRepo.findById(SUB_ID)).thenReturn(Optional.of(sub));
        when(userRepo.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner()));
        when(planRepo.findByCodigo("PROFESIONAL")).thenReturn(Optional.of(profesionalPlan()));
        when(consultorioRepo.countByEmpresaId(EMPRESA_ID)).thenReturn(1L);
        when(profesionalRepo.countByConsultorioId(CONSULTORIO_ID)).thenReturn(2L);
        when(suscripcionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.findById(OWNER_ID)).thenReturn(Optional.of(owner()));
        when(empresaRepo.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa()));
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));

        SubscriptionSummaryResult result = service.changePlan(
                new ChangePlanCommand(SUB_ID, "PROFESIONAL", OWNER_EMAIL, false));

        assertThat(result.planCode()).isEqualTo("PROFESIONAL");
        verify(emailPort).sendPlanChanged(eq(OWNER_EMAIL), any(), eq("Plan Profesional"));
    }

    @Test
    void changePlan_downgrade_exceedsConsultorioLimit_throws() {
        Suscripcion sub = new Suscripcion(SUB_ID, OWNER_ID, EMPRESA_ID, CONSULTORIO_ID,
                "PROFESIONAL", "MONTHLY", "ACTIVE", null, "token",
                SuscripcionStatus.ACTIVE, Instant.now(),
                TODAY, TODAY.plusMonths(1), null, null,
                null, null, null, null, Instant.now(), Instant.now());

        when(suscripcionRepo.findById(SUB_ID)).thenReturn(Optional.of(sub));
        when(userRepo.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner()));
        when(planRepo.findByCodigo("BASICO")).thenReturn(Optional.of(basicoPlan())); // max 1 consultorio
        when(consultorioRepo.countByEmpresaId(EMPRESA_ID)).thenReturn(2L); // tiene 2

        assertThatThrownBy(() -> service.changePlan(
                new ChangePlanCommand(SUB_ID, "BASICO", OWNER_EMAIL, false)))
                .isInstanceOf(PlanDowngradeNotAllowedException.class)
                .hasMessageContaining("consultorios");
    }

    @Test
    void changePlan_downgrade_withinLimits_succeeds() {
        Suscripcion sub = new Suscripcion(SUB_ID, OWNER_ID, EMPRESA_ID, CONSULTORIO_ID,
                "PROFESIONAL", "MONTHLY", "ACTIVE", null, "token",
                SuscripcionStatus.ACTIVE, Instant.now(),
                TODAY, TODAY.plusMonths(1), null, null,
                null, null, null, null, Instant.now(), Instant.now());

        when(suscripcionRepo.findById(SUB_ID)).thenReturn(Optional.of(sub));
        when(userRepo.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner()));
        when(planRepo.findByCodigo("BASICO")).thenReturn(Optional.of(basicoPlan()));
        when(consultorioRepo.countByEmpresaId(EMPRESA_ID)).thenReturn(1L);
        when(profesionalRepo.countByConsultorioId(CONSULTORIO_ID)).thenReturn(2L);
        when(pacienteConsultorioRepo.countByConsultorioId(CONSULTORIO_ID)).thenReturn(100L);
        when(suscripcionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepo.findById(OWNER_ID)).thenReturn(Optional.of(owner()));
        when(empresaRepo.findById(EMPRESA_ID)).thenReturn(Optional.of(empresa()));
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));

        SubscriptionSummaryResult result = service.changePlan(
                new ChangePlanCommand(SUB_ID, "BASICO", OWNER_EMAIL, false));

        assertThat(result.planCode()).isEqualTo("BASICO");
    }

    @Test
    void changePlan_inactivePlan_throws() {
        PlanDefinicion inactivePlan = new PlanDefinicion("OLD", "Old Plan", null,
                BigDecimal.ZERO, null, null, null, null,
                false, false, false, false, false, 99);

        when(suscripcionRepo.findById(SUB_ID)).thenReturn(Optional.of(activeSuscripcion()));
        when(userRepo.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner()));
        when(planRepo.findByCodigo("OLD")).thenReturn(Optional.of(inactivePlan));

        assertThatThrownBy(() -> service.changePlan(
                new ChangePlanCommand(SUB_ID, "OLD", OWNER_EMAIL, false)))
                .isInstanceOf(SubscriptionStateException.class)
                .hasMessageContaining("no está activo");
    }
}
