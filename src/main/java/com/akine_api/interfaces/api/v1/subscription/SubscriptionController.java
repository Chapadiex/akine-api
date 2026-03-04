package com.akine_api.interfaces.api.v1.subscription;

import com.akine_api.application.dto.command.CreateSubscriptionCommand;
import com.akine_api.application.dto.result.SubscriptionSummaryResult;
import com.akine_api.application.service.SuscripcionService;
import com.akine_api.interfaces.api.v1.subscription.dto.CreateSubscriptionRequest;
import com.akine_api.interfaces.api.v1.subscription.dto.CreateSubscriptionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private static final String REQUEST_ACCEPTED_MESSAGE =
            "Su solicitud fue enviada correctamente. La suscripci\u00f3n ser\u00e1 habilitada una vez aprobada por el administrador.";

    private final SuscripcionService suscripcionService;

    public SubscriptionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @PostMapping
    public ResponseEntity<CreateSubscriptionResponse> create(@Valid @RequestBody CreateSubscriptionRequest req) {
        SubscriptionSummaryResult result = suscripcionService.createSubscription(
                new CreateSubscriptionCommand(
                        req.owner().firstName(),
                        req.owner().lastName(),
                        req.owner().documentoFiscal(),
                        req.owner().email(),
                        req.owner().phone(),
                        req.owner().password(),
                        req.company().name(),
                        req.company().cuit(),
                        req.company().address(),
                        req.company().city(),
                        req.company().province(),
                        req.baseConsultorio().name(),
                        req.baseConsultorio().address(),
                        req.baseConsultorio().phone(),
                        req.baseConsultorio().email()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateSubscriptionResponse(
                result.id(),
                result.status(),
                REQUEST_ACCEPTED_MESSAGE
        ));
    }
}
