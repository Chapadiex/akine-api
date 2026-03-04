package com.akine_api.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Suscripcion {

    private final UUID id;
    private final UUID ownerUserId;
    private final UUID empresaId;
    private final UUID consultorioBaseId;
    private SuscripcionStatus status;
    private final Instant requestedAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private Instant reviewedAt;
    private UUID reviewedByUserId;
    private String rejectionReason;
    private final Instant createdAt;
    private Instant updatedAt;

    public Suscripcion(UUID id,
                       UUID ownerUserId,
                       UUID empresaId,
                       UUID consultorioBaseId,
                       SuscripcionStatus status,
                       Instant requestedAt,
                       LocalDate startDate,
                       LocalDate endDate,
                       Instant reviewedAt,
                       UUID reviewedByUserId,
                       String rejectionReason,
                       Instant createdAt,
                       Instant updatedAt) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.empresaId = empresaId;
        this.consultorioBaseId = consultorioBaseId;
        this.status = status;
        this.requestedAt = requestedAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reviewedAt = reviewedAt;
        this.reviewedByUserId = reviewedByUserId;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void approve(LocalDate startDate, LocalDate endDate, UUID reviewedByUserId) {
        this.status = SuscripcionStatus.ACTIVE;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reviewedAt = Instant.now();
        this.reviewedByUserId = reviewedByUserId;
        this.rejectionReason = null;
        this.updatedAt = Instant.now();
    }

    public void reject(String reason, UUID reviewedByUserId) {
        this.status = SuscripcionStatus.REJECTED;
        this.reviewedAt = Instant.now();
        this.reviewedByUserId = reviewedByUserId;
        this.rejectionReason = reason;
        this.updatedAt = Instant.now();
    }

    public void suspend(String reason, UUID reviewedByUserId) {
        this.status = SuscripcionStatus.SUSPENDED;
        this.reviewedAt = Instant.now();
        this.reviewedByUserId = reviewedByUserId;
        this.rejectionReason = reason;
        this.updatedAt = Instant.now();
    }

    public void reactivate(UUID reviewedByUserId) {
        this.status = SuscripcionStatus.ACTIVE;
        this.reviewedAt = Instant.now();
        this.reviewedByUserId = reviewedByUserId;
        this.updatedAt = Instant.now();
    }

    public void expire() {
        this.status = SuscripcionStatus.EXPIRED;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOwnerUserId() { return ownerUserId; }
    public UUID getEmpresaId() { return empresaId; }
    public UUID getConsultorioBaseId() { return consultorioBaseId; }
    public SuscripcionStatus getStatus() { return status; }
    public Instant getRequestedAt() { return requestedAt; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Instant getReviewedAt() { return reviewedAt; }
    public UUID getReviewedByUserId() { return reviewedByUserId; }
    public String getRejectionReason() { return rejectionReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
