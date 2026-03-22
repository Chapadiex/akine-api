package com.akine_api.interfaces.api.v1.sesion.dto;

import com.akine_api.domain.model.sesion.CoberturaTipo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SesionAdministrativaRequest {
    @NotNull
    private UUID sesionId;
    private UUID turnoId;
    @NotNull
    private UUID pacienteId;
    @NotNull
    private CoberturaTipo coberturaTipo;
    private UUID financiadorId;
    private UUID planId;
    private String numeroAfiliado;
    private Boolean tienePedidoMedico;
    private Boolean tieneOrden;
    private Boolean tieneAutorizacion;
    private String numeroAutorizacion;
}
