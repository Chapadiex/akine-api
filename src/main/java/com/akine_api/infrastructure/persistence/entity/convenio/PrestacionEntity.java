package com.akine_api.infrastructure.persistence.entity.convenio;

import com.akine_api.domain.model.convenio.ModalidadPrestacion;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prestacion")
@Getter
@Setter
@NoArgsConstructor
public class PrestacionEntity extends AuditableEntity {

    @Column(name = "codigo_nomenclador", unique = true, nullable = false, length = 50)
    private String codigoNomenclador;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ModalidadPrestacion modalidad;

    @Column(name = "es_modulo", nullable = false)
    private Boolean esModulo = false;

    @Column(name = "codigos_incluidos", length = 500)
    private String codigosIncluidos;

    @Column(name = "requiere_aut_base", nullable = false)
    private Boolean requiereAutBase = false;

    @Column(nullable = false)
    private Boolean activa = true;
}
