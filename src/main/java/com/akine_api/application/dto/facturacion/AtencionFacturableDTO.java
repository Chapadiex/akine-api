package com.akine_api.application.dto.facturacion;

import com.akine_api.domain.model.facturacion.EstadoFacturacion;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AtencionFacturableDTO {
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
