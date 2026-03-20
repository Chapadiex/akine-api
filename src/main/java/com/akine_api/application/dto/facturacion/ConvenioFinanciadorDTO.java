package com.akine_api.application.dto.facturacion;

import com.akine_api.domain.model.facturacion.ModalidadPago;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ConvenioFinanciadorDTO {
    private UUID id;
    private UUID financiadorId;
    private String nombre;
    private ModalidadPago modalidadPago;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private Integer diaCierre;
    private Boolean activo;
}
