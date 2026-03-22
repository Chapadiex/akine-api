package com.akine_api.interfaces.api.v1.cobro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ReporteFacturadoVsCobradoResponse {
    private UUID loteId;
    private UUID financiadorId;
    private String periodo;
    private String estadoLote;
    private BigDecimal importeFacturado;
    private BigDecimal importeCobrado;
    private BigDecimal diferencia;
    private int cantidadPagos;
}
