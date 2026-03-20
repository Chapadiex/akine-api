package com.akine_api.domain.model.facturacion;

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
public class ConvenioPrestacionValor {
    private UUID id;
    private UUID convenioId;
    private UUID planId;
    private UUID prestacionId;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private BigDecimal importeBase;
    private BigDecimal importeCopago;
    private Boolean activo;
}
