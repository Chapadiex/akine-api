package com.akine_api.application.port.output;

import java.time.LocalDate;

public interface EmailPort {
    void sendActivationEmail(String to, String firstName, String activationToken);
    void sendAccountReactivatedEmail(String to, String firstName);
    void sendPasswordChangedNotification(String to, String firstName);
    void sendSubscriptionReceived(String to, String firstName, String subscriptionId);
    void sendSubscriptionApproved(String to, String firstName, LocalDate startDate, LocalDate endDate);
    void sendSubscriptionRejected(String to, String firstName, String reason);
    void sendSubscriptionSuspended(String to, String firstName, String reason);
    void sendSubscriptionReactivated(String to, String firstName, LocalDate endDate);
    void sendRenewalWarning(String to, String firstName, int daysLeft, LocalDate endDate);
    void sendSubscriptionExpiredNotice(String to, String firstName);
    void sendSubscriptionRenewed(String to, String firstName, LocalDate newEndDate);
    void sendPlanChanged(String to, String firstName, String newPlanName);
}
