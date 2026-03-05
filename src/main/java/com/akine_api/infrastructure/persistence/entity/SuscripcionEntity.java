package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "suscripciones")
@Getter
@Setter
@NoArgsConstructor
public class SuscripcionEntity {

    @Id
    private UUID id;

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "consultorio_base_id", nullable = false)
    private UUID consultorioBaseId;

    @Column(name = "plan_code", length = 40)
    private String planCode;

    @Column(name = "billing_cycle", length = 20)
    private String billingCycle;

    @Column(name = "onboarding_step", length = 40)
    private String onboardingStep;

    @Column(name = "payment_reference", length = 120)
    private String paymentReference;

    @Column(name = "tracking_token", length = 120)
    private String trackingToken;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "reviewed_by_user_id")
    private UUID reviewedByUserId;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "submitted_for_approval_at")
    private Instant submittedForApprovalAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
