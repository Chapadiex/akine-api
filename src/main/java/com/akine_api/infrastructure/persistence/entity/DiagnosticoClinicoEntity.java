package com.akine_api.infrastructure.persistence.entity;

import com.akine_api.domain.model.DiagnosticoClinicoEstado;
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
@Table(name = "historia_clinica_diagnosticos")
@Getter
@Setter
@NoArgsConstructor
public class DiagnosticoClinicoEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "profesional_id", nullable = false)
    private UUID profesionalId;

    @Column(name = "sesion_id")
    private UUID sesionId;

    @Column(length = 100)
    private String codigo;

    @Column(nullable = false, length = 500)
    private String descripcion;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiagnosticoClinicoEstado estado;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(length = 1000)
    private String notas;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant updatedAt;
}
