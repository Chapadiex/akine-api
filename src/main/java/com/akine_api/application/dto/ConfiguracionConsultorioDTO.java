package com.akine_api.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ConfiguracionConsultorioDTO {
    private UUID id;
    private UUID consultorioId;
    private String politicaNoShow;
    private Integer noShowHorasAviso;
    private Integer alertaSesionSinCierreHoras;
    private String formatoNumeracionRecibo;
    private Boolean habilitarMultiplesCajas;
    private String monedaDefault;
    private BigDecimal arancelParticularPorSesion;
}
