package com.akine_api.infrastructure.persistence.entity;

import com.akine_api.domain.model.CasoAtencionEstado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "caso_atencion")
@Getter
@Setter
@NoArgsConstructor
public class CasoAtencionEntity {

    @Id
    private UUID id;

    @Column(name = "legajo_id", nullable = false)
    private UUID legajoId;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "profesional_responsable_id")
    private UUID profesionalResponsableId;

    @Column(name = "tipo_origen", nullable = false, length = 40)
    private String tipoOrigen;

    @Column(name = "fecha_apertura", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime fechaApertura;

    @Column(name = "motivo_consulta", length = 500)
    private String motivoConsulta;

    @Column(name = "diagnostico_medico", length = 500)
    private String diagnosticoMedico;

    @Column(name = "diagnostico_funcional", length = 500)
    private String diagnosticoFuncional;

    @Column(name = "afeccion_principal", length = 255)
    private String afeccionPrincipal;

    @Column(name = "cobertura_id")
    private UUID coberturaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CasoAtencionEstado estado;

    @Column(nullable = false, length = 20)
    private String prioridad;

    @Column(name = "atencion_inicial_id")
    private UUID atencionInicialId;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant updatedAt;
}
