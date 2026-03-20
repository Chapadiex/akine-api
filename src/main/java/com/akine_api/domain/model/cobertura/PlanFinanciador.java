package com.akine_api.domain.model.cobertura;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanFinanciador {
    private UUID id;
    private UUID financiadorId;
    private String nombrePlan;
    private TipoPlan tipoPlan;
    private Boolean requiereAutorizacionDefault;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private Boolean activo;
}
