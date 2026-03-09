package com.akine_api.infrastructure.persistence.entity;

import com.akine_api.domain.model.PlanTratamientoCaracter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "historia_clinica_plan_tratamientos_detalle")
@Getter
@Setter
@NoArgsConstructor
public class PlanTratamientoDetalleEntity {

    @Id
    private UUID id;

    @Column(name = "plan_terapeutico_id", nullable = false)
    private UUID planTerapeuticoId;

    @Column(name = "tratamiento_id", nullable = false, length = 100)
    private String tratamientoId;

    @Column(name = "tratamiento_nombre_snapshot", nullable = false, length = 255)
    private String tratamientoNombreSnapshot;

    @Column(name = "tratamiento_categoria_codigo_snapshot", length = 100)
    private String tratamientoCategoriaCodigoSnapshot;

    @Column(name = "tratamiento_categoria_nombre_snapshot", length = 120)
    private String tratamientoCategoriaNombreSnapshot;

    @Column(name = "tratamiento_tipo_snapshot", length = 30)
    private String tratamientoTipoSnapshot;

    @Column(name = "tratamiento_requiere_autorizacion_snapshot")
    private Boolean tratamientoRequiereAutorizacionSnapshot;

    @Column(name = "tratamiento_requiere_prescripcion_medica_snapshot")
    private Boolean tratamientoRequierePrescripcionMedicaSnapshot;

    @Column(name = "tratamiento_duracion_sugerida_minutos_snapshot")
    private Integer tratamientoDuracionSugeridaMinutosSnapshot;

    @Column(name = "cantidad_sesiones", nullable = false)
    private int cantidadSesiones;

    @Column(name = "frecuencia_sugerida", length = 120)
    private String frecuenciaSugerida;

    @Enumerated(EnumType.STRING)
    @Column(name = "caracter_caso", nullable = false, length = 30)
    private PlanTratamientoCaracter caracterCaso;

    @Column(name = "fecha_estimada_inicio")
    private LocalDate fechaEstimadaInicio;

    @Column(name = "requiere_autorizacion", nullable = false)
    private boolean requiereAutorizacion;

    @Column(length = 1000)
    private String observaciones;

    @Column(name = "observaciones_administrativas", length = 1000)
    private String observacionesAdministrativas;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
