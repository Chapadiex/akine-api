package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "memberships")
@Getter @Setter @NoArgsConstructor
public class MembershipEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "role_in_consultorio", nullable = false, length = 50)
    private String roleInConsultorio;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
