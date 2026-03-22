package com.akine_api.infrastructure.persistence.entity;

import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "cobro_configuracion_consultorio")
@Getter
@Setter
@NoArgsConstructor
public class ConfiguracionConsultorioEntity extends AuditableEntity {

    @Column(name = "consultorio_id", nullable = false, unique = true)
    private UUID consultorioId;

    @Column(name = "politica_no_show", nullable = false, length = 30)
    private String politicaNoShow = "NO_COBRAR";

    @Column(name = "no_show_horas_aviso")
    private Integer noShowHorasAviso;

    @Column(name = "alerta_sesion_sin_cierre_horas", nullable = false)
    private Integer alertaSesionSinCierreHoras = 24;

    @Column(name = "formato_numeracion_recibo", nullable = false, length = 50)
    private String formatoNumeracionRecibo = "REC-{year}-{seq:06}";

    @Column(name = "habilitar_multiples_cajas", nullable = false)
    private Boolean habilitarMultiplesCajas = false;

    @Column(name = "moneda_default", nullable = false, length = 5)
    private String monedaDefault = "ARS";
}
