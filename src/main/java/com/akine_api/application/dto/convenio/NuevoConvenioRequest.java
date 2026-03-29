package com.akine_api.application.dto.convenio;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class NuevoConvenioRequest {
    private UUID financiadorId;
    private String plan;
    private String modalidad;
    private String vigenciaDesde;
    private String vigenciaHasta;
    private Integer diaCierre;
    private Boolean requiereAut;
    private Boolean requiereOrden;
    private List<NuevoArancelRequest> aranceles;
}
