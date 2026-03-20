package com.akine_api.infrastructure.persistence.entity.facturacion;

import com.akine_api.infrastructure.persistence.entity.cobertura.PlanFinanciadorEntity;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "billing_convenio_prestacion_valor")
@Getter
@Setter
@NoArgsConstructor
public class ConvenioPrestacionValorEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convenio_id", nullable = false)
    private ConvenioFinanciadorEntity convenio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private PlanFinanciadorEntity plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestacion_id", nullable = false)
    private PrestacionArancelableEntity prestacion;

    @Column(name = "vigencia_desde", nullable = false)
    private LocalDate vigenciaDesde;

    @Column(name = "vigencia_hasta")
    private LocalDate vigenciaHasta;

    @Column(name = "importe_base", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeBase = BigDecimal.ZERO;

    @Column(name = "importe_copago", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeCopago = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean activo = true;
}
