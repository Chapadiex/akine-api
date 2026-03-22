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
public class LoteFacturacionOsDetalle {

    private UUID id;
    private UUID loteId;
    private UUID liquidacionSesionId;
    private UUID sesionId;
    private UUID pacienteId;
    private BigDecimal importeOs;
    private String observaciones;
    private Instant createdAt;
}
