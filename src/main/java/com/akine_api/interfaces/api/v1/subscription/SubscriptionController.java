package com.akine_api.interfaces.api.v1.subscription;

import com.akine_api.application.dto.command.ChangePlanCommand;
import com.akine_api.application.dto.command.CreateSubscriptionCommand;
import com.akine_api.application.dto.command.CreateSubscriptionDraftCommand;
import com.akine_api.application.dto.command.RenewSubscriptionCommand;
import com.akine_api.application.dto.command.SimulateSubscriptionPaymentCommand;
import com.akine_api.application.dto.command.SubmitSubscriptionForApprovalCommand;
import com.akine_api.application.dto.command.UpdateSubscriptionClinicCommand;
import com.akine_api.application.dto.command.UpdateSubscriptionCompanyCommand;
import com.akine_api.application.dto.command.UpdateSubscriptionOwnerCommand;
import com.akine_api.application.dto.result.SubscriptionSummaryResult;
import com.akine_api.application.service.SuscripcionService;
import com.akine_api.interfaces.api.v1.subscription.dto.ChangePlanRequest;
import com.akine_api.interfaces.api.v1.subscription.dto.CreateSubscriptionDraftRequest;
import com.akine_api.interfaces.api.v1.subscription.dto.CreateSubscriptionRequest;
import com.akine_api.interfaces.api.v1.subscription.dto.CreateSubscriptionResponse;
import com.akine_api.interfaces.api.v1.subscription.dto.SimulatePaymentRequest;
import com.akine_api.interfaces.api.v1.subscription.dto.SubscriptionStatusResponse;
import com.akine_api.interfaces.api.v1.subscription.dto.UpdateSubscriptionClinicRequest;
import com.akine_api.interfaces.api.v1.subscription.dto.UpdateSubscriptionCompanyRequest;
import com.akine_api.interfaces.api.v1.subscription.dto.UpdateSubscriptionOwnerRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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
                        req.planCode(),
                        req.billingCycle(),
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
                REQUEST_ACCEPTED_MESSAGE,
                result.trackingToken()
        ));
    }

    @PostMapping("/draft")
    public ResponseEntity<CreateSubscriptionResponse> createDraft(@Valid @RequestBody CreateSubscriptionDraftRequest req) {
        SubscriptionSummaryResult result = suscripcionService.createDraft(
                new CreateSubscriptionDraftCommand(
                        req.planCode(),
                        req.billingCycle(),
                        req.ownerEmail(),
                        req.ownerPassword()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateSubscriptionResponse(
                result.id(),
                result.status(),
                "Borrador creado. Continúa con el onboarding.",
                result.trackingToken()
        ));
    }

    @PatchMapping("/{id}/owner")
    public ResponseEntity<SubscriptionStatusResponse> updateOwner(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSubscriptionOwnerRequest req
    ) {
        SubscriptionSummaryResult result = suscripcionService.updateOwner(
                new UpdateSubscriptionOwnerCommand(
                        id,
                        req.firstName(),
                        req.lastName(),
                        req.documentoFiscal(),
                        req.email(),
                        req.phone(),
                        req.password()
                )
        );
        return ResponseEntity.ok(toStatusResponse(result));
    }

    @PatchMapping("/{id}/company")
    public ResponseEntity<SubscriptionStatusResponse> updateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSubscriptionCompanyRequest req
    ) {
        SubscriptionSummaryResult result = suscripcionService.updateCompany(
                new UpdateSubscriptionCompanyCommand(
                        id,
                        req.name(),
                        req.cuit(),
                        req.address(),
                        req.city(),
                        req.province()
                )
        );
        return ResponseEntity.ok(toStatusResponse(result));
    }

    @PatchMapping("/{id}/clinic")
    public ResponseEntity<SubscriptionStatusResponse> updateClinic(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSubscriptionClinicRequest req
    ) {
        SubscriptionSummaryResult result = suscripcionService.updateClinic(
                new UpdateSubscriptionClinicCommand(
                        id,
                        req.name(),
                        req.address(),
                        req.phone(),
                        req.email()
                )
        );
        return ResponseEntity.ok(toStatusResponse(result));
    }

    @PatchMapping("/{id}/payment-simulate")
    public ResponseEntity<SubscriptionStatusResponse> simulatePayment(
            @PathVariable UUID id,
            @Valid @RequestBody SimulatePaymentRequest req
    ) {
        SubscriptionSummaryResult result = suscripcionService.simulatePayment(
                new SimulateSubscriptionPaymentCommand(id, req.paymentReference())
        );
        return ResponseEntity.ok(toStatusResponse(result));
    }

    @PostMapping("/{id}/submit-approval")
    public ResponseEntity<SubscriptionStatusResponse> submitApproval(@PathVariable UUID id) {
        SubscriptionSummaryResult result = suscripcionService.submitForApproval(
                new SubmitSubscriptionForApprovalCommand(id)
        );
        return ResponseEntity.ok(toStatusResponse(result));
    }

    @GetMapping("/status/{trackingToken}")
    public ResponseEntity<SubscriptionStatusResponse> status(@PathVariable String trackingToken) {
        return suscripcionService.getByTrackingToken(trackingToken)
                .map(this::toStatusResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/my")
    public ResponseEntity<SubscriptionStatusResponse> getMySuscripcion(Authentication auth) {
        SubscriptionSummaryResult result = suscripcionService.getMySuscripcion(auth.getName());
        return ResponseEntity.ok(toStatusResponse(result));
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<SubscriptionStatusResponse> renew(
            @PathVariable UUID id,
            Authentication auth
    ) {
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        SubscriptionSummaryResult result = suscripcionService.renew(
                new RenewSubscriptionCommand(id, auth.getName(), isAdmin));
        return ResponseEntity.ok(toStatusResponse(result));
    }

    @PostMapping("/{id}/change-plan")
    public ResponseEntity<SubscriptionStatusResponse> changePlan(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePlanRequest req,
            Authentication auth
    ) {
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        SubscriptionSummaryResult result = suscripcionService.changePlan(
                new ChangePlanCommand(id, req.planCode(), auth.getName(), isAdmin));
        return ResponseEntity.ok(toStatusResponse(result));
    }

    private SubscriptionStatusResponse toStatusResponse(SubscriptionSummaryResult result) {
        return new SubscriptionStatusResponse(
                result.id(),
                result.status(),
                result.planCode(),
                result.billingCycle(),
                result.onboardingStep(),
                result.trackingToken(),
                result.submittedForApprovalAt(),
                result.requestedAt(),
                result.startDate(),
                result.endDate(),
                result.rejectionReason()
        );
    }
}
