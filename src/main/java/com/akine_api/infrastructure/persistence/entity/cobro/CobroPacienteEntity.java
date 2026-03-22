package com.akine_api.infrastructure.persistence.entity.cobro;

import com.akine_api.domain.model.cobro.EstadoCobroPaciente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cobro_cobro_paciente")
@Getter
@Setter
@NoArgsConstructor
public class CobroPacienteEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "liquidacion_sesion_id")
    private UUID liquidacionSesionId;

    @Column(name = "sesion_id")
    private UUID sesionId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "caja_diaria_id", nullable = false)
    private UUID cajaDiariaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoCobroPaciente estado = EstadoCobroPaciente.PENDIENTE;

    @Column(name = "fecha_cobro", nullable = false)
    private LocalDate fechaCobro;

    @Column(name = "importe_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeTotal;

    @Column(name = "es_pago_mixto", nullable = false)
    private Boolean esPagoMixto = false;

    @Column(name = "comprobante_numero", length = 50)
    private String comprobanteNumero;

    @Column(name = "recibo_emitido", nullable = false)
    private Boolean reciboEmitido = false;

    @Column
    private String observaciones;

    @Column(name = "cobrado_por", nullable = false)
    private UUID cobradoPor;

    @Column(name = "anulado_por")
    private UUID anuladoPor;

    @Column(name = "anulado_en")
    private Instant anuladoEn;

    @Column(name = "motivo_anulacion")
    private String motivoAnulacion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "cobro_paciente_id")
    private List<CobroPacienteDetalleEntity> detalles;

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
    }

    @PreUpdate
    protected void onUpdate() { updatedAt = Instant.now(); }
}
