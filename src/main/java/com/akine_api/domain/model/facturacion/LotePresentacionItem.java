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
public class LotePresentacionItem {
    private UUID id;
    private UUID loteId;
    private UUID atencionFacturableId;
    private BigDecimal importePresentado;
    private EstadoLoteItem estadoItem;
}
