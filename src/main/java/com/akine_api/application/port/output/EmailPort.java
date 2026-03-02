package com.akine_api.application.port.output;

public interface EmailPort {
    void sendActivationEmail(String to, String firstName, String activationToken);
    void sendPasswordChangedNotification(String to, String firstName);
}
