package com.akine_api.service;

import com.akine_api.application.port.output.EmailPort;
import com.akine_api.application.port.output.SuscripcionRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.SuscripcionRenewalScheduler;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuscripcionRenewalSchedulerTest {

    @Mock SuscripcionRepositoryPort suscripcionRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock EmailPort emailPort;

    SuscripcionRenewalScheduler scheduler;

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID SUB_ID = UUID.randomUUID();
    private static final UUID EMPRESA_ID = UUID.randomUUID();
    private static final UUID CONSULTORIO_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        scheduler = new SuscripcionRenewalScheduler(suscripcionRepo, userRepo, emailPort);
    }

    private Suscripcion pendingRenewalSub(LocalDate endDate) {
        return new Suscripcion(SUB_ID, OWNER_ID, EMPRESA_ID, CONSULTORIO_ID,
                "BASICO", "MONTHLY", "PENDING_RENEWAL", null, "token",
                SuscripcionStatus.PENDING_RENEWAL, Instant.now(),
                LocalDate.now().minusDays(20), endDate, null, null,
                null, null, null, null, Instant.now(), Instant.now());
    }

    private Suscripcion expiredSub(LocalDate endDate) {
        return new Suscripcion(SUB_ID, OWNER_ID, EMPRESA_ID, CONSULTORIO_ID,
                "BASICO", "MONTHLY", "EXPIRED", null, "token",
                SuscripcionStatus.EXPIRED, Instant.now(),
                LocalDate.now().minusMonths(2), endDate, null, null,
                null, null, null, null, Instant.now(), Instant.now());
    }

    private User owner() {
        return new User(OWNER_ID, "owner@test.com", "hash", "Owner", "User",
                null, UserStatus.ACTIVE, Instant.now());
    }

    @Test
    void runDailyRenewalCycle_transitionsActiveSubscriptionsToRenewal() {
        when(suscripcionRepo.markPendingRenewalDue(any(), any())).thenReturn(3);
        when(suscripcionRepo.expirePendingRenewalDue(any())).thenReturn(0);
        when(suscripcionRepo.findByStatusAndEndDate(any(), any())).thenReturn(List.of());

        scheduler.runDailyRenewalCycle();

        verify(suscripcionRepo).markPendingRenewalDue(any(), any());
    }

    @Test
    void runDailyRenewalCycle_expiredSubscriptions_sendsExpiryEmail() {
        LocalDate today = LocalDate.now();
        when(suscripcionRepo.markPendingRenewalDue(any(), any())).thenReturn(0);
        when(suscripcionRepo.expirePendingRenewalDue(any())).thenReturn(1);
        when(suscripcionRepo.findByStatusAndEndDate(eq(SuscripcionStatus.EXPIRED), eq(today)))
                .thenReturn(List.of(expiredSub(today)));
        when(suscripcionRepo.findByStatusAndEndDate(eq(SuscripcionStatus.PENDING_RENEWAL), any()))
                .thenReturn(List.of());
        when(userRepo.findById(OWNER_ID)).thenReturn(Optional.of(owner()));

        scheduler.runDailyRenewalCycle();

        verify(emailPort).sendSubscriptionExpiredNotice(eq("owner@test.com"), any());
    }

    @Test
    void runDailyRenewalCycle_pendingRenewalSubscriptions_sendsWarningEmail() {
        LocalDate today = LocalDate.now();
        LocalDate in7Days = today.plusDays(7);

        when(suscripcionRepo.markPendingRenewalDue(any(), any())).thenReturn(0);
        when(suscripcionRepo.expirePendingRenewalDue(any())).thenReturn(0);
        // Only the 7-day warning subscription
        when(suscripcionRepo.findByStatusAndEndDate(eq(SuscripcionStatus.PENDING_RENEWAL), eq(in7Days)))
                .thenReturn(List.of(pendingRenewalSub(in7Days)));
        when(suscripcionRepo.findByStatusAndEndDate(eq(SuscripcionStatus.PENDING_RENEWAL),
                argThat(d -> !d.equals(in7Days)))).thenReturn(List.of());
        when(userRepo.findById(OWNER_ID)).thenReturn(Optional.of(owner()));

        scheduler.runDailyRenewalCycle();

        verify(emailPort).sendRenewalWarning(eq("owner@test.com"), any(), eq(7), eq(in7Days));
    }

    @Test
    void runDailyRenewalCycle_noSubscriptions_sendsNoEmails() {
        when(suscripcionRepo.markPendingRenewalDue(any(), any())).thenReturn(0);
        when(suscripcionRepo.expirePendingRenewalDue(any())).thenReturn(0);
        when(suscripcionRepo.findByStatusAndEndDate(any(), any())).thenReturn(List.of());

        scheduler.runDailyRenewalCycle();

        verifyNoInteractions(emailPort);
    }
}
