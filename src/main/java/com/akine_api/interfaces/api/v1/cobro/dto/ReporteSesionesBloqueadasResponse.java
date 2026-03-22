package com.akine_api.interfaces.api.v1.cobro.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ReporteSesionesBloqueadasResponse {
    private UUID liquidacionId;
    private UUID sesionId;
    private UUID pacienteId;
    private String motivoBloqueo;
    private Instant createdAt;
}
