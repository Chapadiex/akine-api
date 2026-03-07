package com.akine_api.infrastructure.email;

import com.akine_api.application.port.output.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Profile("dev")
public class LoggingEmailService implements EmailPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailService.class);

    @Override
    public void sendActivationEmail(String to, String firstName, String activationToken) {
        log.info("[DEV EMAIL] Activation for {} ({}): token={}", firstName, to, activationToken);
    }

    @Override
    public void sendAccountReactivatedEmail(String to, String firstName) {
        log.info("[DEV EMAIL] Account reactivated for {} ({})", firstName, to);
    }

    @Override
    public void sendPasswordChangedNotification(String to, String firstName) {
        log.info("[DEV EMAIL] Password changed for {} ({})", firstName, to);
    }

    @Override
    public void sendSubscriptionReceived(String to, String firstName, String subscriptionId) {
        log.info("[DEV EMAIL] Subscription received for {} ({}), id={}", firstName, to, subscriptionId);
    }

    @Override
    public void sendSubscriptionApproved(String to, String firstName, LocalDate startDate, LocalDate endDate) {
        log.info("[DEV EMAIL] Subscription approved for {} ({}) from {} to {}", firstName, to, startDate, endDate);
    }

    @Override
    public void sendSubscriptionRejected(String to, String firstName, String reason) {
        log.info("[DEV EMAIL] Subscription rejected for {} ({}) reason={}", firstName, to, reason);
    }

    @Override
    public void sendSubscriptionSuspended(String to, String firstName, String reason) {
        log.info("[DEV EMAIL] Subscription suspended for {} ({}) reason={}", firstName, to, reason);
    }

    @Override
    public void sendSubscriptionReactivated(String to, String firstName, LocalDate endDate) {
        log.info("[DEV EMAIL] Subscription reactivated for {} ({}) until {}", firstName, to, endDate);
    }
}
