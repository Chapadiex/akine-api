package com.akine_api.application.dto.convenio;

import lombok.Data;

import java.util.List;

@Data
public class RenovarConvenioRequest {
    private String vigenciaDesde;
    private String vigenciaHasta;
    private String motivoCierre;
    private List<NuevoArancelRequest> aranceles;
}
