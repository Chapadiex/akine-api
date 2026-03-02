package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "disponibilidad_profesional")
@Getter
@Setter
@NoArgsConstructor
public class DisponibilidadProfesionalEntity {

    @Id
    private UUID id;

    @Column(name = "profesional_id", nullable = false)
    private UUID profesionalId;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 9)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false, columnDefinition = "TIME")
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false, columnDefinition = "TIME")
    private LocalTime horaFin;

    @Column(nullable = false)
    private boolean activo;
}
