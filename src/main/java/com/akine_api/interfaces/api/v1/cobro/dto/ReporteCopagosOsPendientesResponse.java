package com.akine_api.interfaces.api.v1.cobro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class ReporteCopagosOsPendientesResponse {
    private UUID liquidacionId;
    private UUID sesionId;
    private UUID pacienteId;
    private UUID financiadorId;
    private BigDecimal copagoImporte;
    private BigDecimal importeObraSocial;
    private String estado;
    private Instant createdAt;
}
