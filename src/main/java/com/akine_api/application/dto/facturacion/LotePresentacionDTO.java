package com.akine_api.application.dto.facturacion;

import com.akine_api.domain.model.facturacion.EstadoLote;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class LotePresentacionDTO {
    private UUID id;
    private UUID financiadorId;
    private UUID convenioId;
    private String periodo;
    private LocalDate fechaPresentacion;
    private BigDecimal importeNetoPresentado;
    private EstadoLote estadoLote;
    private String observaciones;
}
