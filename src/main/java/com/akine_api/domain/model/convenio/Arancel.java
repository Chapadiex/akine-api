package com.akine_api.domain.model.convenio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Arancel {
    private UUID id;
    private UUID convenioVersionId;
    private UUID prestacionId;
    private String prestacionCodigo;
    private String prestacionNombre;
    private BigDecimal importeOs;
    private CoseguroTipo coseguroTipo;
    private BigDecimal coseguroValor;
    private BigDecimal importeTotal;
    private Integer sesionesMesMax;
    private Integer sesionesAnioMax;
    private Boolean requiereAutOverride;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private Boolean activo;
}
