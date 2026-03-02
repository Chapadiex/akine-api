package com.akine_api.infrastructure.email;

import com.akine_api.application.port.output.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class LoggingEmailService implements EmailPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailService.class);

    @Override
    public void sendActivationEmail(String to, String firstName, String activationToken) {
        log.info("[DEV EMAIL] Activación para {} ({}): token={}", firstName, to, activationToken);
    }

    @Override
    public void sendPasswordChangedNotification(String to, String firstName) {
        log.info("[DEV EMAIL] Contraseña cambiada para {} ({})", firstName, to);
    }
}
