package com.akine_api.domain.model.cobro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCaja {
    private UUID id;
    private UUID consultorioId;
    private UUID cajaDiariaId;
    private TipoMovimiento tipoMovimiento;
    private OrigenMovimiento origenMovimiento;
    private UUID origenId;
    private Instant fechaHora;
    private String descripcion;
    private BigDecimal importe;
    private String signo;               // PLUS, MINUS
    private MedioPago medioPago;
    private Boolean esAnulable;
    private UUID usuarioId;
    private String observaciones;
    private Boolean anulado;
    private UUID anuladoPor;
    private Instant anuladoEn;
    private String motivoAnulacion;
    private Long version;
}
