package com.akine_api.domain.model.convenio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prestacion {
    private UUID id;
    private String codigoNomenclador;
    private String nombre;
    private ModalidadPrestacion modalidad;
    private Boolean esModulo;
    private String codigosIncluidos;
    private Boolean requiereAutBase;
    private Boolean activa;
}
