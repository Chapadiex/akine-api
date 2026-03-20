package com.akine_api.application.dto.cobertura;

import com.akine_api.domain.model.cobertura.TipoPlan;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PlanFinanciadorDTO {
    private UUID id;
    private UUID financiadorId;
    private String nombrePlan;
    private TipoPlan tipoPlan;
    private Boolean requiereAutorizacionDefault;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private Boolean activo;
}
