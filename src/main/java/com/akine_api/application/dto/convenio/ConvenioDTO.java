package com.akine_api.application.dto.convenio;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ConvenioDTO {
    private UUID id;
    private UUID consultorioId;
    private UUID financiadorId;
    private String financiadorNombre;
    private String financiadorSigla;
    private String plan;
    private String siglaDisplay;
    private String modalidad;
    private Integer diaCierre;
    private Boolean requiereAut;
    private Boolean requiereOrden;
    private ConvenioVersionDTO versionActual;
    private List<ArancelResumenDTO> arancelesResumen;
}
