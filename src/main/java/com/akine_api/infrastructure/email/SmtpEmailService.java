package com.akine_api.infrastructure.email;

import com.akine_api.application.port.output.EmailPort;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
        msg.setSubject("Activá tu cuenta AKINE");
        msg.setText("Hola " + firstName + ",\n\nTu token de activación es: " + activationToken
                + "\n\nVálido por 24 horas.");
        mailSender.send(msg);
    }

    @Override
    public void sendPasswordChangedNotification(String to, String firstName) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Contraseña actualizada - AKINE");
        msg.setText("Hola " + firstName + ",\n\nTu contraseña fue cambiada exitosamente.");
        mailSender.send(msg);
    }
}
