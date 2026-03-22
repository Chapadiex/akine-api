package com.akine_api.interfaces.api.v1.cobro.dto;

import com.akine_api.domain.model.cobro.MedioPago;
import com.akine_api.domain.model.cobro.OrigenMovimiento;
import com.akine_api.domain.model.cobro.TipoMovimiento;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class MovimientoCajaResponse {
    private UUID id;
    private UUID cajaDiariaId;
    private TipoMovimiento tipoMovimiento;
    private OrigenMovimiento origenMovimiento;
    private UUID origenId;
    private Instant fechaHora;
    private String descripcion;
    private BigDecimal importe;
    private String signo;
    private MedioPago medioPago;
    private Boolean anulado;
}
