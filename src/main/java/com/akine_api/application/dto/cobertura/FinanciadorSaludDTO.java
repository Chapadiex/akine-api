package com.akine_api.application.dto.cobertura;

import com.akine_api.domain.model.cobertura.TipoFinanciador;
import lombok.Data;

import java.util.UUID;

@Data
public class FinanciadorSaludDTO {
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
