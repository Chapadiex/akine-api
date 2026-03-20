package com.akine_api.application.dto.facturacion;

import com.akine_api.domain.model.facturacion.UnidadFacturacion;
import lombok.Data;

import java.util.UUID;

@Data
public class PrestacionArancelableDTO {
    private UUID id;
    private String codigoInterno;
    private String nombre;
    private UnidadFacturacion unidadFacturacion;
    private Boolean activo;
}
