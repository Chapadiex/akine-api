package com.akine_api.infrastructure.persistence.entity.cobertura;

import com.akine_api.domain.model.cobertura.TipoFinanciador;
import com.akine_api.infrastructure.persistence.entity.common.AuditableEntity;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coverage_financiador_salud")
@Getter
@Setter
@NoArgsConstructor
public class FinanciadorSaludEntity extends AuditableEntity {

    @Column(name = "codigo_externo", length = 50)
    private String codigoExterno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_financiador", nullable = false, length = 50)
    private TipoFinanciador tipoFinanciador;

    @Column(name = "subtipo_financiador", length = 50)
    private String subtipoFinanciador;

    @Column(nullable = false, unique = true, length = 255)
    private String nombre;

    @Column(name = "nombre_corto", length = 100)
    private String nombreCorto;

    @Column(name = "ambito_cobertura", length = 50)
    private String ambitoCobertura;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(nullable = false)
    private Boolean activo = true;
}
