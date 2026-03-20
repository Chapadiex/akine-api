package com.akine_api.domain.model.facturacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrestacionArancelable {
    private UUID id;
    private String codigoInterno;
    private String nombre;
    private UnidadFacturacion unidadFacturacion;
    private Boolean activo;
}
