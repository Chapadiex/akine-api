package com.akine_api.infrastructure.persistence.entity.cobro;

import com.akine_api.domain.model.cobro.EstadoLiquidacion;
import com.akine_api.domain.model.cobro.OrigenTipoCobro;
import com.akine_api.domain.model.cobro.TipoLiquidacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cobro_liquidacion_sesion")
@Getter
@Setter
@NoArgsConstructor
public class LiquidacionSesionEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "sesion_id", nullable = false)
    private UUID sesionId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "financiador_id")
    private UUID financiadorId;

    @Column(name = "plan_id")
    private UUID planId;

    @Column(name = "convenio_id")
    private UUID convenioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_liquidacion", nullable = false, length = 20)
    private TipoLiquidacion tipoLiquidacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoLiquidacion estado;

    @Column(name = "motivo_bloqueo")
    private String motivoBloqueo;

    @Column(name = "valor_bruto", nullable = false, precision = 19, scale = 4)
    private BigDecimal valorBruto = BigDecimal.ZERO;

    @Column(name = "descuento_importe", nullable = false, precision = 19, scale = 4)
    private BigDecimal descuentoImporte = BigDecimal.ZERO;

    @Column(name = "descuento_porcentaje", precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje;

    @Column(name = "copago_importe", nullable = false, precision = 19, scale = 4)
    private BigDecimal copagoImporte = BigDecimal.ZERO;

    @Column(name = "coseguro_importe", nullable = false, precision = 19, scale = 4)
    private BigDecimal coseguroImporte = BigDecimal.ZERO;

    @Column(name = "importe_paciente", nullable = false, precision = 19, scale = 4)
    private BigDecimal importePaciente = BigDecimal.ZERO;

    @Column(name = "importe_obra_social", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeObraSocial = BigDecimal.ZERO;

    @Column(name = "importe_total_liquidado", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeTotalLiquidado = BigDecimal.ZERO;

    @Column(name = "documentacion_completa", nullable = false)
    private boolean documentacionCompleta = false;

    @Column(name = "documentacion_obs")
    private String documentacionObs;

    @Column(name = "es_facturable_os", nullable = false)
    private boolean esFacturableOs = false;

    @Column(name = "requiere_revision_manual", nullable = false)
    private boolean requiereRevisionManual = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen_tipo_cobro", nullable = false, length = 30)
    private OrigenTipoCobro origenTipoCobro;

    @Column(name = "convenio_vigente_snapshot", columnDefinition = "TEXT")
    private String convenioVigenteSnapshot;

    @Column(name = "recalculada_en")
    private Instant recalculadaEn;

    @Column(name = "recalculada_por")
    private UUID recalculadaPor;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "liquidado_por", nullable = false)
    private UUID liquidadoPor;

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
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
