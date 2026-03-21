package com.akine_api.infrastructure.persistence.entity;

import com.akine_api.domain.model.HistoriaClinicaOrigenRegistro;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;
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
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historia_clinica_sesiones")
@Getter
@Setter
@NoArgsConstructor
public class SesionClinicaEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "profesional_id")
    private UUID profesionalId;

    @Column(name = "turno_id")
    private UUID turnoId;

    @Column(name = "caso_atencion_id")
    private UUID casoAtencionId;

    @Column(name = "box_id")
    private UUID boxId;

    @Column(name = "fecha_atencion", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime fechaAtencion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HistoriaClinicaSesionEstado estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_atencion", nullable = false, length = 30)
    private HistoriaClinicaTipoAtencion tipoAtencion;

    @Column(name = "motivo_consulta", length = 500)
    private String motivoConsulta;

    @Column(name = "resumen_clinico", length = 1000)
    private String resumenClinico;

    @Column(length = 2000)
    private String subjetivo;

    @Column(length = 2000)
    private String objetivo;

    @Column(length = 2000)
    private String evaluacion;

    @Column(name = "plan", length = 2000)
    private String plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen_registro", nullable = false, length = 30)
    private HistoriaClinicaOrigenRegistro origenRegistro;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "closed_by_user_id")
    private UUID closedByUserId;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant updatedAt;

    @Column(name = "closed_at", columnDefinition = "TIMESTAMP")
    private Instant closedAt;
}
