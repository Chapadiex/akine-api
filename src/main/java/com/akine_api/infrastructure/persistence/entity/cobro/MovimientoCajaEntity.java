package com.akine_api.infrastructure.persistence.entity.cobro;

import com.akine_api.domain.model.cobro.MedioPago;
import com.akine_api.domain.model.cobro.OrigenMovimiento;
import com.akine_api.domain.model.cobro.TipoMovimiento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cobro_movimiento_caja")
@Getter
@Setter
@NoArgsConstructor
public class MovimientoCajaEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "caja_diaria_id", nullable = false)
    private UUID cajaDiariaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen_movimiento", nullable = false, length = 30)
    private OrigenMovimiento origenMovimiento;

    @Column(name = "origen_id")
    private UUID origenId;

    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal importe;

    @Column(nullable = false, length = 5)
    private String signo = "PLUS";

    @Enumerated(EnumType.STRING)
    @Column(name = "medio_pago", length = 30)
    private MedioPago medioPago;

    @Column(name = "es_anulable", nullable = false)
    private Boolean esAnulable = true;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column
    private String observaciones;

    @Column(nullable = false)
    private Boolean anulado = false;

    @Column(name = "anulado_por")
    private UUID anuladoPor;

    @Column(name = "anulado_en")
    private Instant anuladoEn;

    @Column(name = "motivo_anulacion")
    private String motivoAnulacion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID();
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
        if (fechaHora == null) fechaHora = now;
    }

    @PreUpdate
    protected void onUpdate() { updatedAt = Instant.now(); }
}
