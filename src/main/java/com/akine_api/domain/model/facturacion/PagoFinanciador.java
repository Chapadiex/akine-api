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
public class PagoFinanciador {
    private UUID id;
    private UUID financiadorId;
    private UUID liquidacionId;
    private LocalDate fechaPago;
    private BigDecimal importePagado;
    private String metodoPago;
    private String referenciaPago;
    private Boolean conciliado;
}
