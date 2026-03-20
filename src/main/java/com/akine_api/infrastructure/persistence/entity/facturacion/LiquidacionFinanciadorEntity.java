package com.akine_api.infrastructure.persistence.entity.facturacion;

import com.akine_api.domain.model.facturacion.EstadoConciliacion;
import com.akine_api.infrastructure.persistence.entity.cobertura.FinanciadorSaludEntity;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "billing_liquidacion_financiador")
@Getter
@Setter
@NoArgsConstructor
public class LiquidacionFinanciadorEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financiador_id", nullable = false)
    private FinanciadorSaludEntity financiador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convenio_id", nullable = false)
    private ConvenioFinanciadorEntity convenio;

    @Column(name = "numero_liquidacion", nullable = false, length = 100)
    private String numeroLiquidacion;

    @Column(name = "periodo_referido", nullable = false, length = 7)
    private String periodoReferido;

    @Column(name = "importe_bruto", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeBruto = BigDecimal.ZERO;

    @Column(name = "importe_debitos", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeDebitos = BigDecimal.ZERO;

    @Column(name = "importe_neto", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeNeto = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_conciliacion", nullable = false, length = 50)
    private EstadoConciliacion estadoConciliacion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
