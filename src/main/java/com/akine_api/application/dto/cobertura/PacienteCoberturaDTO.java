package com.akine_api.application.dto.cobertura;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PacienteCoberturaDTO {
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
