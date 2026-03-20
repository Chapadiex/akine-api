package com.akine_api.domain.model.cobertura;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanciadorSalud {
    private UUID id;
    private String codigoExterno;
    private TipoFinanciador tipoFinanciador;
    private String subtipoFinanciador;
    private String nombre;
    private String nombreCorto;
    private String ambitoCobertura;
    private UUID consultorioId;
    private Boolean activo;
}
