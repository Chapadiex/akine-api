package com.akine_api.application.service;

import com.akine_api.application.port.output.EmailPort;
import com.akine_api.application.port.output.SuscripcionRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.model.Suscripcion;
import com.akine_api.domain.model.SuscripcionStatus;
import com.akine_api.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Ejecuta diariamente el ciclo de vida de renovación de suscripciones:
 * 1. Transiciona ACTIVE → PENDING_RENEWAL cuando endDate <= hoy+30 días.
 * 2. Transiciona PENDING_RENEWAL → EXPIRED cuando endDate < hoy.
 * 3. Envía avisos de renovación a 30, 7 y 1 día del vencimiento.
 * 4. Envía aviso de expiración el día del vencimiento.
 */
@Component
public class SuscripcionRenewalScheduler {

    private static final Logger log = LoggerFactory.getLogger(SuscripcionRenewalScheduler.class);
    private static final ZoneId OPERATIVE_ZONE = ZoneId.of("America/Argentina/Buenos_Aires");
    private static final int[] REMINDER_DAYS = {30, 7, 1};

    private final SuscripcionRepositoryPort suscripcionRepo;
    private final UserRepositoryPort userRepo;
    private final EmailPort emailPort;

    public SuscripcionRenewalScheduler(SuscripcionRepositoryPort suscripcionRepo,
                                       UserRepositoryPort userRepo,
                                       EmailPort emailPort) {
        this.suscripcionRepo = suscripcionRepo;
        this.userRepo = userRepo;
        this.emailPort = emailPort;
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Argentina/Buenos_Aires")
    @Transactional
    public void runDailyRenewalCycle() {
        LocalDate today = LocalDate.now(OPERATIVE_ZONE);

        // 1. ACTIVE → PENDING_RENEWAL (30 días antes del vencimiento)
        int transitioned = suscripcionRepo.markPendingRenewalDue(today, today.plusDays(30));
        if (transitioned > 0) {
            log.info("[RENEWAL] {} suscripciones transicionadas a PENDING_RENEWAL", transitioned);
        }

        // 2. PENDING_RENEWAL → EXPIRED
        int expired = suscripcionRepo.expirePendingRenewalDue(today);
        if (expired > 0) {
            log.info("[RENEWAL] {} suscripciones expiradas (PENDING_RENEWAL → EXPIRED)", expired);
            sendExpiryNotices(today);
        }

        // 3. Avisos de renovación por días restantes
        for (int daysLeft : REMINDER_DAYS) {
            sendRenewalWarningsFor(today.plusDays(daysLeft), daysLeft);
        }
    }

    private void sendExpiryNotices(LocalDate today) {
        List<Suscripcion> expired = suscripcionRepo.findByStatusAndEndDate(SuscripcionStatus.EXPIRED, today);
        for (Suscripcion s : expired) {
            userRepo.findById(s.getOwnerUserId()).ifPresent(owner ->
                    emailPort.sendSubscriptionExpiredNotice(owner.getEmail(), owner.getFirstName()));
        }
    }

    private void sendRenewalWarningsFor(LocalDate targetDate, int daysLeft) {
        List<Suscripcion> nearExpiry = suscripcionRepo.findByStatusAndEndDate(
                SuscripcionStatus.PENDING_RENEWAL, targetDate);
        for (Suscripcion s : nearExpiry) {
            userRepo.findById(s.getOwnerUserId()).ifPresent((User owner) ->
                    emailPort.sendRenewalWarning(owner.getEmail(), owner.getFirstName(),
                            daysLeft, s.getEndDate()));
        }
    }
}
