package com.akine_api.application.dto.convenio;

import lombok.Data;

@Data
public class ActualizarConvenioRequest {
    private String modalidad;
    private Integer diaCierre;
    private Boolean requiereAut;
    private Boolean requiereOrden;
    private String vigenciaHasta;
}
