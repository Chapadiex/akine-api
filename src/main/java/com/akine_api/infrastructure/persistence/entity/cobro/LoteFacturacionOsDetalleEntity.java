package com.akine_api.infrastructure.persistence.entity.cobro;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cobro_lote_facturacion_os_detalle")
@Getter
@Setter
@NoArgsConstructor
public class LoteFacturacionOsDetalleEntity {

    @Id
    private UUID id;

    /** FK referencing LoteFacturacionOsEntity.id — stored as plain column (not @ManyToOne) */
    @Column(name = "lote_id", nullable = false)
    private UUID loteId;

    @Column(name = "liquidacion_sesion_id", nullable = false)
    private UUID liquidacionSesionId;

    @Column(name = "sesion_id", nullable = false)
    private UUID sesionId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "importe_os", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeOs;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
    }
}
