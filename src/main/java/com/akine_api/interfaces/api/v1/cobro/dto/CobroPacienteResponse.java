package com.akine_api.interfaces.api.v1.cobro.dto;

import com.akine_api.domain.model.cobro.EstadoCobroPaciente;
import com.akine_api.domain.model.cobro.MedioPago;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CobroPacienteResponse {
    private UUID id;
    private UUID consultorioId;
    private UUID cajaDiariaId;
    private UUID pacienteId;
    private UUID sesionId;
    private UUID liquidacionSesionId;
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
    private List<DetalleItem> detalles;
    private Long version;

    @Data
    public static class DetalleItem {
        private UUID id;
        private MedioPago medioPago;
        private BigDecimal importe;
        private String referenciaOperacion;
        private Integer cuotas;
        private String banco;
        private String marcaTarjeta;
    }
}
