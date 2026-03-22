package com.akine_api.infrastructure.persistence.entity.sesion;

import com.akine_api.domain.model.sesion.CoberturaTipo;
import com.akine_api.domain.model.sesion.ValidacionCoberturaEstado;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sesion_administrativa")
@Getter
@Setter
@NoArgsConstructor
public class SesionAdministrativaEntity extends AuditableEntity {

    @Column(name = "sesion_id", nullable = false, unique = true)
    private UUID sesionId;

    @Column(name = "turno_id")
    private UUID turnoId;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "cobertura_tipo", nullable = false, length = 20)
    private CoberturaTipo coberturaTipo;

    @Column(name = "financiador_id")
    private UUID financiadorId;

    @Column(name = "plan_id")
    private UUID planId;

    @Column(name = "numero_afiliado", length = 50)
    private String numeroAfiliado;

    @Column(name = "tiene_pedido_medico", nullable = false)
    private Boolean tienePedidoMedico = false;

    @Column(name = "tiene_orden", nullable = false)
    private Boolean tieneOrden = false;

    @Column(name = "tiene_autorizacion", nullable = false)
    private Boolean tieneAutorizacion = false;

    @Column(name = "numero_autorizacion", length = 50)
    private String numeroAutorizacion;

    @Column(name = "asistencia_confirmada", nullable = false)
    private Boolean asistenciaConfirmada = false;

    @Column(name = "documentacion_completa", nullable = false)
    private Boolean documentacionCompleta = false;

    @Column(name = "documentacion_faltante", length = 500)
    private String documentacionFaltante;

    @Enumerated(EnumType.STRING)
    @Column(name = "validacion_cobertura_estado", nullable = false, length = 20)
    private ValidacionCoberturaEstado validacionCoberturaEstado = ValidacionCoberturaEstado.PENDIENTE;

    @Column(name = "es_facturable_os", nullable = false)
    private Boolean esFacturableOs = false;

    @Column(name = "registrado_por")
    private UUID registradoPor;

    @Column(name = "registrado_en")
    private Instant registradoEn;

    @Column(name = "actualizado_por")
    private UUID actualizadoPor;

    @Column(name = "actualizado_en")
    private Instant actualizadoEn;
}
