package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "suscripcion_auditoria")
@Getter
@Setter
@NoArgsConstructor
public class SuscripcionAuditoriaEntity {

    @Id
    private UUID id;

    @Column(name = "suscripcion_id", nullable = false)
    private UUID suscripcionId;

    @Column(nullable = false, length = 40)
    private String action;

    @Column(name = "from_status", length = 20)
    private String fromStatus;

    @Column(name = "to_status", length = 20)
    private String toStatus;

    @Column(name = "actor_user_id")
    private UUID actorUserId;

    @Column(length = 500)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
