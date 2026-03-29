package com.akine_api.domain.model.convenio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvenioVersion {
    private UUID id;
    private UUID convenioId;
    private Integer versionNum;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private ConvenioVersionEstado estado;
    private String motivoCierre;
    private UUID creadoPor;
    private Instant creadoAt;
    private Integer cantidadLotes;
}
