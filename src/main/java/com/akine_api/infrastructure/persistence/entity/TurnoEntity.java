package com.akine_api.infrastructure.persistence.entity;

import com.akine_api.domain.model.TipoConsulta;
import com.akine_api.domain.model.TurnoEstado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "turnos")
@Getter
@Setter
@NoArgsConstructor
public class TurnoEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "profesional_id")
    private UUID profesionalId;

    @Column(name = "box_id")
    private UUID boxId;

    @Column(name = "paciente_id")
    private UUID pacienteId;

    @Column(name = "fecha_hora_inicio", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "duracion_minutos", nullable = false)
    private int duracionMinutos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TurnoEstado estado;

    @Column(name = "motivo_consulta", length = 500)
    private String motivoConsulta;

    @Column(length = 1000)
    private String notas;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_consulta", length = 20)
    private TipoConsulta tipoConsulta;

    @Column(name = "telefono_contacto", length = 50)
    private String telefonoContacto;

    @Column(name = "creado_por_user_id")
    private UUID creadoPorUserId;

    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    @Column(name = "cancelado_por_user_id")
    private UUID canceladoPorUserId;

    @Column(name = "fecha_hora_inicio_real", columnDefinition = "TIMESTAMP")
    private LocalDateTime fechaHoraInicioReal;

    @Column(name = "fecha_hora_fin_real", columnDefinition = "TIMESTAMP")
    private LocalDateTime fechaHoraFinReal;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant updatedAt;
}
