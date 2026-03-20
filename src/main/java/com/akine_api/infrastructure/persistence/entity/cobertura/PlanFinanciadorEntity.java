package com.akine_api.infrastructure.persistence.entity.cobertura;

import com.akine_api.domain.model.cobertura.TipoPlan;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "coverage_plan_financiador")
@Getter
@Setter
@NoArgsConstructor
public class PlanFinanciadorEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financiador_id", nullable = false)
    private FinanciadorSaludEntity financiador;

    @Column(name = "nombre_plan", nullable = false, length = 255)
    private String nombrePlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_plan", nullable = false, length = 50)
    private TipoPlan tipoPlan;

    @Column(name = "requiere_autorizacion_default", nullable = false)
    private Boolean requiereAutorizacionDefault = false;

    @Column(name = "vigencia_desde")
    private LocalDate vigenciaDesde;

    @Column(name = "vigencia_hasta")
    private LocalDate vigenciaHasta;

    @Column(nullable = false)
    private Boolean activo = true;
}
