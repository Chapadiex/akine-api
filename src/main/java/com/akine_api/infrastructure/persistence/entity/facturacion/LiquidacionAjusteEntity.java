package com.akine_api.infrastructure.persistence.entity.facturacion;

import com.akine_api.domain.model.facturacion.TipoAjuste;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "billing_liquidacion_ajuste")
@Getter
@Setter
@NoArgsConstructor
public class LiquidacionAjusteEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liquidacion_id", nullable = false)
    private LiquidacionFinanciadorEntity liquidacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_item_id")
    private LotePresentacionItemEntity loteItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ajuste", nullable = false, length = 50)
    private TipoAjuste tipoAjuste;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal importe;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(nullable = false)
    private Boolean resuelto = false;

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
