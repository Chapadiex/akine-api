package com.akine_api.infrastructure.persistence.entity.cobro;

import com.akine_api.domain.model.cobro.EstadoLoteOs;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cobro_lote_facturacion_os")
@Getter
@Setter
@NoArgsConstructor
public class LoteFacturacionOsEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "financiador_id", nullable = false)
    private UUID financiadorId;

    @Column(name = "plan_id")
    private UUID planId;

    @Column(name = "convenio_id")
    private UUID convenioId;

    @Column(nullable = false, length = 7)
    private String periodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoLoteOs estado;

    @Column(name = "cantidad_sesiones", nullable = false)
    private int cantidadSesiones = 0;

    @Column(name = "importe_total_os", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeTotalOs = BigDecimal.ZERO;

    @Column(name = "importe_neto", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeNeto = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "cerrado_en")
    private Instant cerradoEn;

    @Column(name = "cerrado_por")
    private UUID cerradoPor;

    @Column(name = "presentado_en")
    private Instant presentadoEn;

    @Column(name = "presentado_por")
    private UUID presentadoPor;

    @Column(name = "creado_por", nullable = false)
    private UUID creadoPor;

    @OneToMany(mappedBy = "loteId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LoteFacturacionOsDetalleEntity> detalles;

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
