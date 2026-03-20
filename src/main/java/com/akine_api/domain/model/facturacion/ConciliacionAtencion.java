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
public class ConciliacionAtencion {
    private UUID atencionId;
    private String pacienteNombre;
    private String financiadorNombre;
    private String prestacionNombre;
    
    // Los 4 estados económicos
    private BigDecimal importeSnapshot;    // Lo que el sistema dijo que valía
    private BigDecimal importePresentado;   // Lo que efectivamente se mandó en el lote
    private BigDecimal importeLiquidado;    // Lo que el financiador reconoció (Bruto - Débitos)
    private BigDecimal importePagado;       // Lo que entró al banco
    
    private BigDecimal diferencia;          // Snapshot vs Liquidado
    private String estadoFinal;             // PENDIENTE, CONCILIADO, CON_DIFERENCIA
}
