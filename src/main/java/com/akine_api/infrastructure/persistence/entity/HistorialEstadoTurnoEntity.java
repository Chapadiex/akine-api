package com.akine_api.infrastructure.persistence.entity;

import com.akine_api.domain.model.TurnoEstado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "historial_estado_turno")
@Getter
@Setter
@NoArgsConstructor
public class HistorialEstadoTurnoEntity {

    @Id
    private UUID id;

    @Column(name = "turno_id", nullable = false)
    private UUID turnoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 20)
    private TurnoEstado estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 20)
    private TurnoEstado estadoNuevo;

    @Column(name = "cambiado_por_user_id")
    private UUID cambiadoPorUserId;

    @Column(length = 500)
    private String motivo;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant createdAt;
}
