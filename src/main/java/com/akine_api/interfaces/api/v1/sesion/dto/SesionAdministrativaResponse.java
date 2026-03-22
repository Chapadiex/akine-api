package com.akine_api.interfaces.api.v1.sesion.dto;

import com.akine_api.domain.model.sesion.CoberturaTipo;
import com.akine_api.domain.model.sesion.ValidacionCoberturaEstado;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class SesionAdministrativaResponse {
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
    private Long version;
}
