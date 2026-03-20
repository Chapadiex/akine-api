package com.akine_api.infrastructure.persistence.entity.facturacion;

import com.akine_api.domain.model.facturacion.EstadoLoteItem;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "billing_lote_presentacion_item")
@Getter
@Setter
@NoArgsConstructor
public class LotePresentacionItemEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private LotePresentacionEntity lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atencion_facturable_id", nullable = false)
    private AtencionFacturableEntity atencionFacturable;

    @Column(name = "importe_presentado", nullable = false, precision = 19, scale = 4)
    private BigDecimal importePresentado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_item", nullable = false, length = 50)
    private EstadoLoteItem estadoItem;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.Instant createdAt;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID();
        createdAt = java.time.Instant.now();
    }
}
