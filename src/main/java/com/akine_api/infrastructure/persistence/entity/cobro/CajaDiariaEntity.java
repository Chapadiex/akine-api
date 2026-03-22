package com.akine_api.infrastructure.persistence.entity.cobro;

import com.akine_api.domain.model.cobro.CajaDiariaEstado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cobro_caja_diaria")
@Getter
@Setter
@NoArgsConstructor
public class CajaDiariaEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "fecha_operativa", nullable = false)
    private LocalDate fechaOperativa;

    @Column(name = "turno_caja", length = 20)
    private String turnoCaja;

    @Column(name = "numero_caja")
    private Integer numeroCaja;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CajaDiariaEstado estado = CajaDiariaEstado.ABIERTA;

    @Column(name = "saldo_inicial", nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(name = "total_ingresos_paciente", precision = 19, scale = 4)
    private BigDecimal totalIngresosPaciente;

    @Column(name = "total_ingresos_os", precision = 19, scale = 4)
    private BigDecimal totalIngresosOs;

    @Column(name = "total_egresos", precision = 19, scale = 4)
    private BigDecimal totalEgresos;

    @Column(name = "saldo_teorico_cierre", precision = 19, scale = 4)
    private BigDecimal saldoTeoricoCierre;

    @Column(name = "saldo_real_cierre", precision = 19, scale = 4)
    private BigDecimal saldoRealCierre;

    @Column(name = "diferencia_cierre", precision = 19, scale = 4)
    private BigDecimal diferenciaCierre;

    @Column(name = "observaciones_apertura")
    private String observacionesApertura;

    @Column(name = "observaciones_cierre")
    private String observacionesCierre;

    @Column(name = "abierta_por", nullable = false)
    private UUID abiertaPor;

    @Column(name = "abierta_en", nullable = false)
    private Instant abiertaEn;

    @Column(name = "cerrada_por")
    private UUID cerradaPor;

    @Column(name = "cerrada_en")
    private Instant cerradaEn;

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
        if (abiertaEn == null) abiertaEn = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
