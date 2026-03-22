package com.akine_api.domain.model.sesion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionAdministrativa {
    private UUID id;
    private UUID sesionId;
    private UUID turnoId;
    private UUID consultorioId;
    private UUID pacienteId;
    private CoberturaTipo coberturaTipo;
    private UUID financiadorId;
    private UUID planId;
    private String numeroAfiliado;
    private Boolean tienePedidoMedico;
    private Boolean tieneOrden;
    private Boolean tieneAutorizacion;
    private String numeroAutorizacion;
    private Boolean asistenciaConfirmada;
    private Boolean documentacionCompleta;
    private String documentacionFaltante;
    private ValidacionCoberturaEstado validacionCoberturaEstado;
    private Boolean esFacturableOs;
    private UUID registradoPor;
    private Instant registradoEn;
    private UUID actualizadoPor;
    private Instant actualizadoEn;
    private Long version;
}
