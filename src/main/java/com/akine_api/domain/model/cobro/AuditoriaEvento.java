package com.akine_api.domain.model.cobro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaEvento {
    private UUID id;
    private UUID consultorioId;
    private String entidad;
    private UUID entidadId;
    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;
    private UUID usuarioId;
    private Instant timestamp;
    private String motivo;
}
