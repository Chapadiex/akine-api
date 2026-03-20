package com.akine_api.application.dto.facturacion;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ConciliacionAtencionDTO {
    private UUID atencionId;
    private String pacienteNombre;
    private String financiadorNombre;
    private String prestacionNombre;
    private BigDecimal importeSnapshot;
    private BigDecimal importePresentado;
    private BigDecimal importeLiquidado;
    private BigDecimal importePagado;
    private BigDecimal diferencia;
    private String estadoFinal;
}
