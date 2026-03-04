package com.akine_api.interfaces.api.v1.admin;

import com.akine_api.application.dto.command.*;
import com.akine_api.application.dto.result.PagedResult;
import com.akine_api.application.dto.result.SubscriptionSummaryResult;
import com.akine_api.application.service.SuscripcionService;
import com.akine_api.interfaces.api.v1.admin.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.akine_api.infrastructure.persistence.repository.UserJpaRepository;
import com.akine_api.domain.exception.UserNotFoundException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/subscriptions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubscriptionController {

    private final SuscripcionService suscripcionService;
    private final UserJpaRepository userJpaRepository;

    public AdminSubscriptionController(SuscripcionService suscripcionService,
                                       UserJpaRepository userJpaRepository) {
        this.suscripcionService = suscripcionService;
        this.userJpaRepository = userJpaRepository;
    }

    @GetMapping
    public ResponseEntity<PagedSubscriptionListResponse> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResult<SubscriptionSummaryResult> result = suscripcionService.listSubscriptions(status, page, size);
        return ResponseEntity.ok(new PagedSubscriptionListResponse(
                result.content().stream().map(this::toResponse).toList(),
                result.page(),
                result.size(),
                result.total()
        ));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<SubscriptionSummaryResponse> approve(
            @PathVariable UUID id,
            @Valid @RequestBody ApproveSubscriptionRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        SubscriptionSummaryResult result = suscripcionService.approve(new ApproveSubscriptionCommand(
                id,
                resolveActorUserId(principal),
                req.startDate(),
                req.endDate()
        ));
        return ResponseEntity.ok(toResponse(result));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<SubscriptionSummaryResponse> reject(
            @PathVariable UUID id,
            @Valid @RequestBody RejectSubscriptionRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        SubscriptionSummaryResult result = suscripcionService.reject(new RejectSubscriptionCommand(
                id,
                resolveActorUserId(principal),
                req.reason()
        ));
        return ResponseEntity.ok(toResponse(result));
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<SubscriptionSummaryResponse> suspend(
            @PathVariable UUID id,
            @Valid @RequestBody SuspendSubscriptionRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        SubscriptionSummaryResult result = suscripcionService.suspend(new SuspendSubscriptionCommand(
                id,
                resolveActorUserId(principal),
                req.reason()
        ));
        return ResponseEntity.ok(toResponse(result));
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<SubscriptionSummaryResponse> reactivate(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        SubscriptionSummaryResult result = suscripcionService.reactivate(new ReactivateSubscriptionCommand(
                id,
                resolveActorUserId(principal)
        ));
        return ResponseEntity.ok(toResponse(result));
    }

    private UUID resolveActorUserId(UserDetails principal) {
        return userJpaRepository.findByEmail(principal.getUsername())
                .map(e -> e.getId())
                .orElseThrow(() -> new UserNotFoundException(principal.getUsername()));
    }

    private SubscriptionSummaryResponse toResponse(SubscriptionSummaryResult r) {
        return new SubscriptionSummaryResponse(
                r.id(),
                r.status(),
                r.requestedAt(),
                r.startDate(),
                r.endDate(),
                r.reviewedAt(),
                r.reviewedByUserId(),
                r.rejectionReason(),
                new SubscriptionSummaryResponse.OwnerInfo(
                        r.ownerUserId(),
                        r.ownerFirstName(),
                        r.ownerLastName(),
                        r.ownerEmail()
                ),
                new SubscriptionSummaryResponse.CompanyInfo(
                        r.empresaId(),
                        r.empresaName(),
                        r.empresaCuit(),
                        r.empresaCity(),
                        r.empresaProvince()
                ),
                new SubscriptionSummaryResponse.ConsultorioInfo(
                        r.consultorioBaseId(),
                        r.consultorioBaseName(),
                        r.consultorioBaseAddress()
                )
        );
    }
}
