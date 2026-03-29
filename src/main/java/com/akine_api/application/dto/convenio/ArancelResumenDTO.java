package com.akine_api.application.dto.convenio;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArancelResumenDTO {
    private String codigoNomenclador;
    private String nombrePrestacion;
    private BigDecimal importeTotal;
}
