package com.akine_api.domain.model.cobro;

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
public class CobroPacienteDetalle {
    private UUID id;
    private UUID cobroPacienteId;
    private MedioPago medioPago;
    private BigDecimal importe;
    private String referenciaOperacion;
    private Integer cuotas;
    private String banco;
    private String marcaTarjeta;
    private String numeroUltimos4;
    private LocalDate fechaAcreditacion;
    private String observaciones;
}
