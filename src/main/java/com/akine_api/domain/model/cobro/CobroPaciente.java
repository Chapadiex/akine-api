package com.akine_api.domain.model.cobro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CobroPaciente {
    private UUID id;
    private UUID consultorioId;
    private UUID liquidacionSesionId;   // nullable — se completa en Fase 3
    private UUID sesionId;
    private UUID pacienteId;
    private UUID cajaDiariaId;
    private EstadoCobroPaciente estado;
    private LocalDate fechaCobro;
    private BigDecimal importeTotal;
    private Boolean esPagoMixto;
    private String comprobanteNumero;
    private Boolean reciboEmitido;
    private String observaciones;
    private UUID cobradoPor;
    private UUID anuladoPor;
    private Instant anuladoEn;
    private String motivoAnulacion;
    private List<CobroPacienteDetalle> detalles;
    private Long version;
}
