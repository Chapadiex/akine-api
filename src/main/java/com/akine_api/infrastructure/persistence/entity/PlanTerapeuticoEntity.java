package com.akine_api.infrastructure.persistence.entity;

import com.akine_api.domain.model.PlanTerapeuticoEstado;
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
import java.util.UUID;

@Entity
@Table(name = "historia_clinica_planes_terapeuticos")
@Getter
@Setter
@NoArgsConstructor
public class PlanTerapeuticoEntity {

    @Id
    private UUID id;

    @Column(name = "atencion_inicial_id", nullable = false)
    private UUID atencionInicialId;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "profesional_id", nullable = false)
    private UUID profesionalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlanTerapeuticoEstado estado;

    @Column(name = "observaciones_generales", length = 2000)
    private String observacionesGenerales;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
