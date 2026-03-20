package com.akine_api.domain.model.cobertura;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteCobertura {
    private UUID id;
    private UUID pacienteId;
    private UUID financiadorId;
    private UUID planId;
    private String numeroAfiliado;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;
    private Boolean principal;
    private Boolean activo;
}
