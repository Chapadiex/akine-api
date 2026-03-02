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
@Table(name = "consultorio_horarios")
@Getter
@Setter
@NoArgsConstructor
public class ConsultorioHorarioEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 9)
    private DayOfWeek diaSemana;

    @Column(name = "hora_apertura", nullable = false, columnDefinition = "TIME")
    private LocalTime horaApertura;

    @Column(name = "hora_cierre", nullable = false, columnDefinition = "TIME")
    private LocalTime horaCierre;

    @Column(nullable = false)
    private boolean activo;
}
