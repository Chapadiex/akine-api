package com.akine_api.application.dto.convenio;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ActualizarArancelesRequest {
    private List<UUID> convenioIds;
    private List<UUID> prestacionIds;
    private String metodo;
    private BigDecimal valor;
    private List<String> camposActualizar;
    private String vigenciaDesde;
    private String vigenciaHasta;
}
