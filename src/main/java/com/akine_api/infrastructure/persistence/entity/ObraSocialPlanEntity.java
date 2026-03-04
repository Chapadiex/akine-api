package com.akine_api.infrastructure.persistence.entity;

import com.akine_api.domain.model.TipoCobertura;
import com.akine_api.domain.model.TipoCoseguro;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "obra_social_planes")
@Getter @Setter @NoArgsConstructor
public class ObraSocialPlanEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obra_social_id", nullable = false)
    private ObraSocialEntity obraSocial;

    @Column(name = "nombre_corto", nullable = false, length = 60)
    private String nombreCorto;

    @Column(name = "nombre_corto_norm", nullable = false, length = 60)
    private String nombreCortoNorm;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cobertura", nullable = false, length = 20)
    private TipoCobertura tipoCobertura;

    @Column(name = "valor_cobertura", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorCobertura;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_coseguro", nullable = false, length = 20)
    private TipoCoseguro tipoCoseguro;

    @Column(name = "valor_coseguro", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorCoseguro;

    @Column(name = "prestaciones_sin_autorizacion", nullable = false)
    private Integer prestacionesSinAutorizacion;

    @Column(length = 1000)
    private String observaciones;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

