package com.akine_api.domain.model.facturacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionAjuste {
    private UUID id;
    private UUID liquidacionId;
    private UUID loteItemId;
    private TipoAjuste tipoAjuste;
    private BigDecimal importe;
    private String motivo;
    private Boolean resuelto;
}
