package com.akine_api.interfaces.api.v1.cobro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LoteFacturacionOsDetalleResponse {
    private UUID id;
    private UUID liquidacionSesionId;
    private UUID sesionId;
    private UUID pacienteId;
    private BigDecimal importeOs;
}
