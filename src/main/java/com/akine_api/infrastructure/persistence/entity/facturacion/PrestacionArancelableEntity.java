package com.akine_api.infrastructure.persistence.entity.facturacion;

import com.akine_api.domain.model.facturacion.UnidadFacturacion;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "billing_prestacion_arancelable")
@Getter
@Setter
@NoArgsConstructor
public class PrestacionArancelableEntity extends AuditableEntity {

    @Column(name = "codigo_interno", nullable = false, unique = true, length = 50)
    private String codigoInterno;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_facturacion", nullable = false, length = 50)
    private UnidadFacturacion unidadFacturacion;

    @Column(nullable = false)
    private Boolean activo = true;
}
