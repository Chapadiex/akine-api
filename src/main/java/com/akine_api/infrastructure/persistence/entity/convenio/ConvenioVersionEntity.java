package com.akine_api.infrastructure.persistence.entity.convenio;

import com.akine_api.domain.model.convenio.ConvenioVersionEstado;
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
import java.util.UUID;

@Entity
@Table(name = "convenio_version")
@Getter
@Setter
@NoArgsConstructor
public class ConvenioVersionEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convenio_id", nullable = false)
    private ConvenioEntity convenio;

    @Column(name = "version_num", nullable = false)
    private Integer versionNum;

    @Column(name = "vigencia_desde", nullable = false)
    private LocalDate vigenciaDesde;

    @Column(name = "vigencia_hasta")
    private LocalDate vigenciaHasta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ConvenioVersionEstado estado = ConvenioVersionEstado.VIGENTE;

    @Column(name = "motivo_cierre", length = 500)
    private String motivoCierre;

    @Column(name = "creado_por")
    private UUID creadoPor;
}
