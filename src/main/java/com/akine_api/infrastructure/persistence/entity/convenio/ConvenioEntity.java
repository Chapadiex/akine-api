package com.akine_api.infrastructure.persistence.entity.convenio;

import com.akine_api.domain.model.convenio.ModalidadConvenio;
import com.akine_api.infrastructure.persistence.entity.cobertura.FinanciadorSaludEntity;
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

import java.util.UUID;

@Entity
@Table(name = "convenio")
@Getter
@Setter
@NoArgsConstructor
public class ConvenioEntity extends AuditableEntity {

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financiador_id", nullable = false)
    private FinanciadorSaludEntity financiador;

    @Column(length = 100)
    private String plan;

    @Column(name = "sigla_display", length = 150)
    private String siglaDisplay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ModalidadConvenio modalidad;

    @Column(name = "dia_cierre")
    private Integer diaCierre;

    @Column(name = "requiere_aut", nullable = false)
    private Boolean requiereAut = false;

    @Column(name = "requiere_orden", nullable = false)
    private Boolean requiereOrden = false;
}
