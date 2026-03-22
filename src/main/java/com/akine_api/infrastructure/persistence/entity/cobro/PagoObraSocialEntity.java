package com.akine_api.infrastructure.persistence.entity.cobro;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cobro_pago_obra_social")
@Getter
@Setter
@NoArgsConstructor
public class PagoObraSocialEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "lote_id", nullable = false)
    private UUID loteId;

    @Column(name = "financiador_id", nullable = false)
    private UUID financiadorId;

    @Column(name = "importe_esperado", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeEsperado;

    @Column(name = "importe_recibido", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeRecibido;

    @Column(name = "diferencia", nullable = false, precision = 19, scale = 4)
    private BigDecimal diferencia = BigDecimal.ZERO;

    @Column(name = "fecha_notificacion", nullable = false)
    private LocalDate fechaNotificacion;

    @Column(name = "fecha_imputacion")
    private LocalDate fechaImputacion;

    @Column(name = "caja_diaria_id")
    private UUID cajaDiariaId;

    @Column(name = "imputado_por")
    private UUID imputadoPor;

    @Column(name = "imputado_en")
    private Instant imputadoEn;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "registrado_por", nullable = false)
    private UUID registradoPor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID();
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
