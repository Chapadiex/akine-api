package com.akine_api.application.dto.facturacion;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ConvenioPrestacionValorDTO {
    private UUID id;
    private UUID convenioId;
    private UUID planId;
    private UUID prestacionId;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private BigDecimal importeBase;
    private BigDecimal importeCopago;
    private BigDecimal copajoPorcentaje;
    private BigDecimal coseguroImporte;
    private BigDecimal topeCobertura;
    private Boolean activo;
}
