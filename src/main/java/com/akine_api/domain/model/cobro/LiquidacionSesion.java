package com.akine_api.domain.model.cobro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionSesion {
    private UUID id;
    private UUID consultorioId;
    private UUID sesionId;
    private UUID pacienteId;

    /** Nullable: null = particular puro (sin convenio) */
    private UUID financiadorId;
    private UUID planId;
    private UUID convenioId;

    private TipoLiquidacion tipoLiquidacion;
    private EstadoLiquidacion estado;
    private String motivoBloqueo;

    private BigDecimal valorBruto;
    private BigDecimal descuentoImporte;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal copagoImporte;
    private BigDecimal coseguroImporte;
    private BigDecimal importePaciente;
    private BigDecimal importeObraSocial;
    private BigDecimal importeTotalLiquidado;

    private boolean documentacionCompleta;
    private String documentacionObs;
    private boolean esFacturableOs;
    private boolean requiereRevisionManual;

    private OrigenTipoCobro origenTipoCobro;

    /** Snapshot JSON del convenio vigente al momento de liquidar */
    private String convenioVigenteSnapshot;

    private Instant recalculadaEn;
    private UUID recalculadaPor;
    private String observaciones;

    private UUID liquidadoPor;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;
}
