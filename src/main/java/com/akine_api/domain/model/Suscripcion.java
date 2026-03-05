package com.akine_api.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Suscripcion {

    private final UUID id;
    private final UUID ownerUserId;
    private final UUID empresaId;
    private final UUID consultorioBaseId;
    private String planCode;
    private String billingCycle;
    private String onboardingStep;
    private String paymentReference;
    private String trackingToken;
    private SuscripcionStatus status;
    private final Instant requestedAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private Instant reviewedAt;
    private UUID reviewedByUserId;
    private String rejectionReason;
    private Instant submittedForApprovalAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public Suscripcion(UUID id,
                       UUID ownerUserId,
                       UUID empresaId,
                       UUID consultorioBaseId,
                       String planCode,
                       String billingCycle,
                       String onboardingStep,
                       String paymentReference,
                       String trackingToken,
                       SuscripcionStatus status,
                       Instant requestedAt,
                       LocalDate startDate,
                       LocalDate endDate,
                       Instant reviewedAt,
                       UUID reviewedByUserId,
                       String rejectionReason,
                       Instant submittedForApprovalAt,
                       Instant createdAt,
                       Instant updatedAt) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.empresaId = empresaId;
        this.consultorioBaseId = consultorioBaseId;
        this.planCode = planCode;
        this.billingCycle = billingCycle;
        this.onboardingStep = onboardingStep;
        this.paymentReference = paymentReference;
        this.trackingToken = trackingToken;
        this.status = status;
        this.requestedAt = requestedAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reviewedAt = reviewedAt;
        this.reviewedByUserId = reviewedByUserId;
        this.rejectionReason = rejectionReason;
        this.submittedForApprovalAt = submittedForApprovalAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void markEmailPending() {
        this.status = SuscripcionStatus.EMAIL_PENDING;
        this.onboardingStep = "OWNER";
        this.updatedAt = Instant.now();
    }

    public void markPaymentPending(String paymentReference) {
        this.status = SuscripcionStatus.PAYMENT_PENDING;
        this.paymentReference = paymentReference;
        this.onboardingStep = "PAYMENT";
        this.updatedAt = Instant.now();
    }

    public void markSetupPending() {
        this.status = SuscripcionStatus.SETUP_PENDING;
        this.onboardingStep = "SETUP";
        this.updatedAt = Instant.now();
    }

    public void submitForApproval() {
        this.status = SuscripcionStatus.PENDING_APPROVAL;
        this.submittedForApprovalAt = Instant.now();
        this.onboardingStep = "REVIEW";
        this.updatedAt = Instant.now();
    }

    public void approve(LocalDate startDate, LocalDate endDate, UUID reviewedByUserId) {
        this.status = SuscripcionStatus.ACTIVE;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reviewedAt = Instant.now();
        this.reviewedByUserId = reviewedByUserId;
        this.rejectionReason = null;
        this.onboardingStep = "ACTIVE";
        this.updatedAt = Instant.now();
    }

    public void reject(String reason, UUID reviewedByUserId) {
        this.status = SuscripcionStatus.REJECTED;
        this.reviewedAt = Instant.now();
        this.reviewedByUserId = reviewedByUserId;
        this.rejectionReason = reason;
        this.onboardingStep = "REJECTED";
        this.updatedAt = Instant.now();
    }

    public void suspend(String reason, UUID reviewedByUserId) {
        this.status = SuscripcionStatus.SUSPENDED;
        this.reviewedAt = Instant.now();
        this.reviewedByUserId = reviewedByUserId;
        this.rejectionReason = reason;
        this.onboardingStep = "SUSPENDED";
        this.updatedAt = Instant.now();
    }

    public void reactivate(UUID reviewedByUserId) {
        this.status = SuscripcionStatus.ACTIVE;
        this.reviewedAt = Instant.now();
        this.reviewedByUserId = reviewedByUserId;
        this.onboardingStep = "ACTIVE";
        this.updatedAt = Instant.now();
    }

    public void expire() {
        this.status = SuscripcionStatus.EXPIRED;
        this.onboardingStep = "EXPIRED";
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOwnerUserId() { return ownerUserId; }
    public UUID getEmpresaId() { return empresaId; }
    public UUID getConsultorioBaseId() { return consultorioBaseId; }
    public String getPlanCode() { return planCode; }
    public String getBillingCycle() { return billingCycle; }
    public String getOnboardingStep() { return onboardingStep; }
    public String getPaymentReference() { return paymentReference; }
    public String getTrackingToken() { return trackingToken; }
    public SuscripcionStatus getStatus() { return status; }
    public Instant getRequestedAt() { return requestedAt; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Instant getReviewedAt() { return reviewedAt; }
    public UUID getReviewedByUserId() { return reviewedByUserId; }
    public String getRejectionReason() { return rejectionReason; }
    public Instant getSubmittedForApprovalAt() { return submittedForApprovalAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
