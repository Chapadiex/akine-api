package com.akine_api.application.dto.convenio;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ConvenioVersionDTO {
    private UUID id;
    private UUID convenioId;
    private Integer versionNum;
    private String vigenciaDesde;
    private String vigenciaHasta;
    private String estado;
    private String motivoCierre;
    private String creadoAt;
    private Integer cantidadLotes;
    private List<ArancelDTO> aranceles;
}
