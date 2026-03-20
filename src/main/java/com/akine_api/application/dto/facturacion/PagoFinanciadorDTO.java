package com.akine_api.application.dto.facturacion;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PagoFinanciadorDTO {
    private UUID id;
    private UUID financiadorId;
    private UUID liquidacionId;
    private LocalDate fechaPago;
    private BigDecimal importePagado;
    private String metodoPago;
    private String referenciaPago;
    private Boolean conciliado;
}
