package com.akine_api.infrastructure.persistence.entity.facturacion;

import com.akine_api.domain.model.facturacion.EstadoLote;
import com.akine_api.infrastructure.persistence.entity.cobertura.FinanciadorSaludEntity;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "billing_lote_presentacion")
@Getter
@Setter
@NoArgsConstructor
public class LotePresentacionEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financiador_id", nullable = false)
    private FinanciadorSaludEntity financiador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convenio_id", nullable = false)
    private ConvenioFinanciadorEntity convenio;

    @Column(nullable = false, length = 7)
    private String periodo;

    @Column(name = "fecha_presentacion")
    private LocalDate fechaPresentacion;

    @Column(name = "importe_neto_presentado", nullable = false, precision = 19, scale = 4)
    private BigDecimal importeNetoPresentado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_lote", nullable = false, length = 50)
    private EstadoLote estadoLote;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
