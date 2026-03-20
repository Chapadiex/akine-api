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
public class AtencionFacturable {
    private UUID id;
    private UUID atencionId;
    private UUID pacienteId;
    private UUID convenioId;
    private UUID prestacionId;
    
    private BigDecimal importeUnitarioSnapshot;
    private BigDecimal importeTotalSnapshot;
    private BigDecimal importeCopagoSnapshot;
    
    private EstadoFacturacion estadoFacturacion;
    private Boolean facturable;
    private String observaciones;
}
