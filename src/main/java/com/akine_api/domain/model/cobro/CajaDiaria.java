package com.akine_api.domain.model.cobro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CajaDiaria {
    private UUID id;
    private UUID consultorioId;
    private LocalDate fechaOperativa;
    private String turnoCaja;           // MANANA, TARDE, NOCHE, null = único
    private Integer numeroCaja;
    private CajaDiariaEstado estado;
    private BigDecimal saldoInicial;
    private BigDecimal totalIngresosPaciente;
    private BigDecimal totalIngresosOs;
    private BigDecimal totalEgresos;
    private BigDecimal saldoTeoricoCierre;
    private BigDecimal saldoRealCierre;
    private BigDecimal diferenciaCierre;
    private String observacionesApertura;
    private String observacionesCierre;
    private UUID abiertaPor;
    private Instant abiertaEn;
    private UUID cerradaPor;
    private Instant cerradaEn;
    private Long version;
}
