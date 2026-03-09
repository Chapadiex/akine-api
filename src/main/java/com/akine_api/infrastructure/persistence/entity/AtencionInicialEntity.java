package com.akine_api.infrastructure.persistence.entity;

import com.akine_api.domain.model.AtencionInicialTipoIngreso;
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
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historia_clinica_atenciones_iniciales")
@Getter
@Setter
@NoArgsConstructor
public class AtencionInicialEntity {

    @Id
    private UUID id;

    @Column(name = "legajo_id", nullable = false)
    private UUID legajoId;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "profesional_id", nullable = false)
    private UUID profesionalId;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ingreso", nullable = false, length = 40)
    private AtencionInicialTipoIngreso tipoIngreso;

    @Column(name = "motivo_consulta_breve", length = 500)
    private String motivoConsultaBreve;

    @Column(name = "sintomas_principales", length = 1000)
    private String sintomasPrincipales;

    @Column(name = "tiempo_evolucion", length = 255)
    private String tiempoEvolucion;

    @Column(length = 2000)
    private String observaciones;

    @Column(name = "especialidad_derivante", length = 255)
    private String especialidadDerivante;

    @Column(name = "profesional_derivante", length = 255)
    private String profesionalDerivante;

    @Column(name = "fecha_prescripcion")
    private LocalDate fechaPrescripcion;

    @Column(name = "diagnostico_codigo", length = 100)
    private String diagnosticoCodigo;

    @Column(name = "diagnostico_nombre", length = 255)
    private String diagnosticoNombre;

    @Column(name = "diagnostico_tipo", length = 40)
    private String diagnosticoTipo;

    @Column(name = "diagnostico_categoria_codigo", length = 100)
    private String diagnosticoCategoriaCodigo;

    @Column(name = "diagnostico_categoria_nombre", length = 120)
    private String diagnosticoCategoriaNombre;

    @Column(name = "diagnostico_subcategoria", length = 120)
    private String diagnosticoSubcategoria;

    @Column(name = "diagnostico_region_anatomica", length = 120)
    private String diagnosticoRegionAnatomica;

    @Column(name = "diagnostico_observacion", length = 1000)
    private String diagnosticoObservacion;

    @Column(name = "observaciones_prescripcion", length = 1000)
    private String observacionesPrescripcion;

    @Column(name = "resumen_clinico_inicial", length = 2000)
    private String resumenClinicoInicial;

    @Column(name = "hallazgos_relevantes", length = 2000)
    private String hallazgosRelevantes;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
