package com.akine_api.infrastructure.persistence.entity.facturacion;

import com.akine_api.domain.model.facturacion.EstadoFacturacion;
import com.akine_api.infrastructure.persistence.entity.PacienteEntity;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "billing_atencion_facturable")
@Getter
@Setter
@NoArgsConstructor
public class AtencionFacturableEntity extends AuditableEntity {

    @Column(name = "atencion_id", nullable = false)
    private UUID atencionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convenio_id", nullable = false)
    private ConvenioFinanciadorEntity convenio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestacion_id", nullable = false)
    private PrestacionArancelableEntity prestacion;

    @Column(name = "importe_unitario_snapshot", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeUnitarioSnapshot;

    @Column(name = "importe_total_snapshot", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeTotalSnapshot;

    @Column(name = "importe_copago_snapshot", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeCopagoSnapshot = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_facturacion", nullable = false, length = 50)
    private EstadoFacturacion estadoFacturacion;

    @Column(nullable = false)
    private Boolean facturable = true;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
