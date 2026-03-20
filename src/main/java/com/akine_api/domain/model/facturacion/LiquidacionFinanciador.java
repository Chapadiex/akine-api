package com.akine_api.domain.model.facturacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionFinanciador {
    private UUID id;
    private UUID financiadorId;
    private UUID convenioId;
    private String numeroLiquidacion;
    private String periodoReferido;
    private BigDecimal importeBruto;
    private BigDecimal importeDebitos;
    private BigDecimal importeNeto;
    private EstadoConciliacion estadoConciliacion;
    private String observaciones;
}
