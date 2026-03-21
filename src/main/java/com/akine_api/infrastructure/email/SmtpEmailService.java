package com.akine_api.infrastructure.email;

import com.akine_api.application.port.output.EmailPort;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Profile("prod")
public class SmtpEmailService implements EmailPort {

    private final JavaMailSender mailSender;

    public SmtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendActivationEmail(String to, String firstName, String activationToken) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Activate your AKINE account");
        msg.setText("Hello " + firstName + ",\n\nYour activation token is: " + activationToken
                + "\n\nIt is valid for 24 hours.");
        mailSender.send(msg);
    }

    @Override
    public void sendAccountReactivatedEmail(String to, String firstName) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("AKINE account reactivated");
        msg.setText("Hello " + firstName + ",\n\nYour account has been reactivated. You can now log in.");
        mailSender.send(msg);
    }

    @Override
    public void sendPasswordChangedNotification(String to, String firstName) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Password updated - AKINE");
        msg.setText("Hello " + firstName + ",\n\nYour password was changed successfully.");
        mailSender.send(msg);
    }

    @Override
    public void sendSubscriptionReceived(String to, String firstName, String subscriptionId) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("AKINE subscription request received");
        msg.setText("Hello " + firstName + ",\n\nWe received your request. Subscription ID: " + subscriptionId
                + "\n\nYour request is pending admin review.");
        mailSender.send(msg);
    }

    @Override
    public void sendSubscriptionApproved(String to, String firstName, LocalDate startDate, LocalDate endDate) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("AKINE subscription approved");
        msg.setText("Hello " + firstName + ",\n\nYour subscription was approved."
                + "\nStart date: " + startDate
                + "\nEnd date: " + endDate
                + "\n\nYou can now log in.");
        mailSender.send(msg);
    }

    @Override
    public void sendSubscriptionRejected(String to, String firstName, String reason) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("AKINE subscription rejected");
        String detail = reason == null || reason.isBlank() ? "No additional reason provided." : reason;
        msg.setText("Hello " + firstName + ",\n\nYour subscription request was rejected."
                + "\nReason: " + detail);
        mailSender.send(msg);
    }

    @Override
    public void sendSubscriptionSuspended(String to, String firstName, String reason) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("AKINE subscription suspended");
        String detail = reason == null || reason.isBlank() ? "No additional reason provided." : reason;
        msg.setText("Hello " + firstName + ",\n\nYour subscription was suspended."
                + "\nReason: " + detail);
        mailSender.send(msg);
    }

    @Override
    public void sendSubscriptionReactivated(String to, String firstName, LocalDate endDate) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("AKINE subscription reactivated");
        msg.setText("Hello " + firstName + ",\n\nYour subscription was reactivated."
                + "\nCurrent end date: " + endDate);
        mailSender.send(msg);
    }

    @Override
    public void sendRenewalWarning(String to, String firstName, int daysLeft, LocalDate endDate) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Tu suscripción AKINE vence en " + daysLeft + " días");
        msg.setText("Hola " + firstName + ",\n\nTu suscripción vence el " + endDate
                + " (" + daysLeft + " días).\n\nRenová desde tu panel para continuar sin interrupciones.");
        mailSender.send(msg);
    }

    @Override
    public void sendSubscriptionExpiredNotice(String to, String firstName) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Tu suscripción AKINE ha expirado");
        msg.setText("Hola " + firstName + ",\n\nTu suscripción AKINE ha expirado."
                + "\nContactá a soporte o renová tu plan para volver a acceder.");
        mailSender.send(msg);
    }

    @Override
    public void sendSubscriptionRenewed(String to, String firstName, LocalDate newEndDate) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Suscripción AKINE renovada");
        msg.setText("Hola " + firstName + ",\n\nTu suscripción fue renovada exitosamente hasta el " + newEndDate + ".");
        mailSender.send(msg);
    }

    @Override
    public void sendPlanChanged(String to, String firstName, String newPlanName) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Plan AKINE actualizado");
        msg.setText("Hola " + firstName + ",\n\nTu plan fue actualizado a: " + newPlanName + ".");
        mailSender.send(msg);
    }
}
