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
public class LotePresentacion {
    private UUID id;
    private UUID financiadorId;
    private UUID convenioId;
    private String periodo;
    private LocalDate fechaPresentacion;
    private BigDecimal importeNetoPresentado;
    private EstadoLote estadoLote;
    private String observaciones;
}
