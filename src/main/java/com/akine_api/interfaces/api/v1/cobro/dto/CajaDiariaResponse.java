package com.akine_api.interfaces.api.v1.cobro.dto;

import com.akine_api.domain.model.cobro.CajaDiariaEstado;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CajaDiariaResponse {
    private UUID id;
    private UUID consultorioId;
    private LocalDate fechaOperativa;
    private String turnoCaja;
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
