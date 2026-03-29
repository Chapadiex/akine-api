package com.akine_api.infrastructure.persistence.entity.convenio;

import com.akine_api.domain.model.convenio.CoseguroTipo;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "arancel")
@Getter
@Setter
@NoArgsConstructor
public class ArancelEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convenio_version_id", nullable = false)
    private ConvenioVersionEntity convenioVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestacion_id", nullable = false)
    private PrestacionEntity prestacion;

    @Column(name = "prestacion_codigo", length = 50)
    private String prestacionCodigo;

    @Column(name = "prestacion_nombre", length = 255)
    private String prestacionNombre;

    @Column(name = "importe_os", precision = 12, scale = 2, nullable = false)
    private BigDecimal importeOs;

    @Enumerated(EnumType.STRING)
    @Column(name = "coseguro_tipo", nullable = false, length = 30)
    private CoseguroTipo coseguroTipo = CoseguroTipo.NINGUNO;

    @Column(name = "coseguro_valor", precision = 10, scale = 2)
    private BigDecimal coseguroValor;

    @Column(name = "importe_total", precision = 12, scale = 2)
    private BigDecimal importeTotal;

    @Column(name = "sesiones_mes_max")
    private Integer sesionesMesMax;

    @Column(name = "sesiones_anio_max")
    private Integer sesionesAnioMax;

    @Column(name = "requiere_aut_override")
    private Boolean requiereAutOverride;

    @Column(name = "vigencia_desde", nullable = false)
    private LocalDate vigenciaDesde;

    @Column(name = "vigencia_hasta")
    private LocalDate vigenciaHasta;

    @Column(nullable = false)
    private Boolean activo = true;
}
