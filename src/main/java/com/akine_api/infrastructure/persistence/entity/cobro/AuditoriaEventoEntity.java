package com.akine_api.infrastructure.persistence.entity.cobro;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cobro_auditoria_evento")
@Getter
@Setter
@NoArgsConstructor
public class AuditoriaEventoEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(nullable = false, length = 100)
    private String entidad;

    @Column(name = "entidad_id", nullable = false)
    private UUID entidadId;

    @Column(nullable = false, length = 30)
    private String accion;

    @Column(name = "estado_anterior", length = 2000)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", length = 2000)
    private String estadoNuevo;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column
    private String motivo;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID();
        if (timestamp == null) timestamp = Instant.now();
    }
}
