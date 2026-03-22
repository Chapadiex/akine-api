package com.akine_api.application.dto.facturacion;

import com.akine_api.domain.model.facturacion.ModalidadPago;
import com.akine_api.domain.model.facturacion.ModoFacturacion;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ConvenioFinanciadorDTO {
    private UUID id;
    private UUID financiadorId;
    private UUID consultorioId;
    private UUID planId;
    private String nombre;
    private ModalidadPago modalidadPago;
    private ModoFacturacion modoFacturacion;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private Integer diaCierre;
    private Boolean requiereAutorizacion;
    private Boolean requiereOrden;
    private Integer cantidadSesionesAutorizadas;
    private Boolean activo;
}
