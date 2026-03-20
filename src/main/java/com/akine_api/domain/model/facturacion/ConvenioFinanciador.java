package com.akine_api.domain.model.facturacion;

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
public class ConvenioFinanciador {
    private UUID id;
    private UUID financiadorId;
    private String nombre;
    private ModalidadPago modalidadPago;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private Integer diaCierre;
    private Boolean activo;
}
