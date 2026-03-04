package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Membership {

    private final UUID id;
    private final UUID userId;
    private final UUID consultorioId;
    private final MembershipRole roleInConsultorio;
    private MembershipStatus status;
    private final Instant createdAt;

    public Membership(UUID id, UUID userId, UUID consultorioId,
                      MembershipRole roleInConsultorio, MembershipStatus status,
                      Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.consultorioId = consultorioId;
        this.roleInConsultorio = roleInConsultorio;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void activate() {
        this.status = MembershipStatus.ACTIVE;
    }

    public void inactivate() {
        this.status = MembershipStatus.INACTIVE;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getConsultorioId() { return consultorioId; }
    public MembershipRole getRoleInConsultorio() { return roleInConsultorio; }
    public MembershipStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
