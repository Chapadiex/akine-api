package com.akine_api.infrastructure.persistence.entity.facturacion;

import com.akine_api.domain.model.facturacion.ModalidadPago;
import com.akine_api.infrastructure.persistence.entity.cobertura.FinanciadorSaludEntity;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "billing_convenio_financiador")
@Getter
@Setter
@NoArgsConstructor
public class ConvenioFinanciadorEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financiador_id", nullable = false)
    private FinanciadorSaludEntity financiador;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad_pago", nullable = false, length = 50)
    private ModalidadPago modalidadPago;

    @Column(name = "vigencia_desde", nullable = false)
    private LocalDate vigenciaDesde;

    @Column(name = "vigencia_hasta")
    private LocalDate vigenciaHasta;

    @Column(name = "dia_cierre")
    private Integer diaCierre;

    @Column(nullable = false)
    private Boolean activo = true;
}
