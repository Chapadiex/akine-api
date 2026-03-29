package com.akine_api.application.dto.convenio;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ArancelDTO {
    private UUID id;
    private UUID convenioVersionId;
    private UUID prestacionId;
    private String prestacionCodigo;
    private String prestacionNombre;
    private BigDecimal importeOs;
    private String coseguroTipo;
    private BigDecimal coseguroValor;
    private BigDecimal importeTotal;
    private Integer sesionesMesMax;
    private Integer sesionesAnioMax;
    private Boolean requiereAutOverride;
    private String vigenciaDesde;
    private String vigenciaHasta;
    private Boolean activo;
}
