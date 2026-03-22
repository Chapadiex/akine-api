package com.akine_api.infrastructure.persistence.entity.facturacion;

import com.akine_api.domain.model.facturacion.ModalidadPago;
import com.akine_api.domain.model.facturacion.ModoFacturacion;
import com.akine_api.infrastructure.persistence.entity.cobertura.FinanciadorSaludEntity;
import com.akine_api.infrastructure.persistence.entity.cobertura.PlanFinanciadorEntity;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "billing_convenio_financiador")
@Getter
@Setter
@NoArgsConstructor
public class ConvenioFinanciadorEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financiador_id", nullable = false)
    private FinanciadorSaludEntity financiador;

    @Column(name = "consultorio_id")
    private UUID consultorioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private PlanFinanciadorEntity plan;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad_pago", nullable = false, length = 50)
    private ModalidadPago modalidadPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "modo_facturacion", nullable = false, length = 30)
    private ModoFacturacion modoFacturacion = ModoFacturacion.MENSUAL;

    @Column(name = "vigencia_desde", nullable = false)
    private LocalDate vigenciaDesde;

    @Column(name = "vigencia_hasta")
    private LocalDate vigenciaHasta;

    @Column(name = "dia_cierre")
    private Integer diaCierre;

    @Column(name = "requiere_autorizacion", nullable = false)
    private Boolean requiereAutorizacion = false;

    @Column(name = "requiere_orden", nullable = false)
    private Boolean requiereOrden = false;

    @Column(name = "cantidad_sesiones_autorizadas")
    private Integer cantidadSesionesAutorizadas;

    @Column(nullable = false)
    private Boolean activo = true;
}
