package com.akine_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionConsultorio {
    private UUID id;
    private UUID consultorioId;

    /** Política para turnos no presentados: NO_COBRAR, COBRAR_TOTAL, COBRAR_PORCENTAJE */
    private String politicaNoShow;
    private Integer noShowHorasAviso;

    /** Horas sin cierre clínico antes de mostrar alerta */
    private Integer alertaSesionSinCierreHoras;

    /** Patrón de numeración de recibos: REC-{year}-{seq:06} */
    private String formatoNumeracionRecibo;

    /** Permite múltiples cajas por turno (mañana/tarde/noche) */
    private Boolean habilitarMultiplesCajas;

    private String monedaDefault;
}
