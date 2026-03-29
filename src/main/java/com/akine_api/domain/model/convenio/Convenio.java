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
public class Convenio {
    private UUID id;
    private UUID consultorioId;
    private UUID financiadorId;
    private String plan;
    private String siglaDisplay;
    private ModalidadConvenio modalidad;
    private Integer diaCierre;
    private Boolean requiereAut;
    private Boolean requiereOrden;
}
