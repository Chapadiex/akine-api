package com.akine_api.application.dto.convenio;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class NuevoArancelRequest {
    private UUID prestacionId;
    private BigDecimal importeOs;
    private String coseguroTipo = "NINGUNO";
    private BigDecimal coseguroValor;
    private Integer sesionesMesMax;
    private Integer sesionesAnioMax;
    private Boolean requiereAutOverride;
    private String vigenciaDesde;
    private String vigenciaHasta;
}
