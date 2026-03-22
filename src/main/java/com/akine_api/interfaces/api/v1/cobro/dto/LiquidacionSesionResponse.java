package com.akine_api.interfaces.api.v1.cobro.dto;

import com.akine_api.domain.model.cobro.EstadoLiquidacion;
import com.akine_api.domain.model.cobro.OrigenTipoCobro;
import com.akine_api.domain.model.cobro.TipoLiquidacion;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class LiquidacionSesionResponse {
    private UUID id;
    private UUID consultorioId;
    private UUID sesionId;
    private UUID pacienteId;
    private UUID financiadorId;
    private UUID planId;
    private UUID convenioId;
    private TipoLiquidacion tipoLiquidacion;
    private EstadoLiquidacion estado;
    private String motivoBloqueo;
    private BigDecimal valorBruto;
    private BigDecimal descuentoImporte;
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
    private Instant recalculadaEn;
    private UUID recalculadaPor;
    private String observaciones;
    private UUID liquidadoPor;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;
}
