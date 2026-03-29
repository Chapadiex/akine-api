package com.akine_api.application.dto.convenio;

import lombok.Data;

import java.util.UUID;

@Data
public class PrestacionDTO {
    private UUID id;
    private String codigoNomenclador;
    private String nombre;
    private String modalidad;
    private Boolean esModulo;
    private String codigosIncluidos;
    private Boolean requiereAutBase;
    private Boolean activa;
}
