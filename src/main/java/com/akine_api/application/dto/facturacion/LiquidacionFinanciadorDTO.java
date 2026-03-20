package com.akine_api.application.dto.facturacion;

import com.akine_api.domain.model.facturacion.EstadoConciliacion;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LiquidacionFinanciadorDTO {
    private UUID id;
    private UUID financiadorId;
    private UUID convenioId;
    private String numeroLiquidacion;
    private String periodoReferido;
    private BigDecimal importeBruto;
    private BigDecimal importeDebitos;
    private BigDecimal importeNeto;
    private EstadoConciliacion estadoConciliacion;
}
