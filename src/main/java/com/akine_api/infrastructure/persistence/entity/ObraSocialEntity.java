package com.akine_api.infrastructure.persistence.entity;

import com.akine_api.domain.model.ObraSocialEstado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "obras_sociales")
@Getter @Setter @NoArgsConstructor
public class ObraSocialEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(nullable = false, length = 20)
    private String acronimo;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Column(nullable = false, length = 13)
    private String cuit;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String telefono;

    @Column(name = "telefono_alternativo", length = 30)
    private String telefonoAlternativo;

    @Column(length = 120)
    private String representante;

    @Column(name = "observaciones_internas", length = 1000)
    private String observacionesInternas;

    @Column(name = "direccion_linea", length = 255)
    private String direccionLinea;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ObraSocialEstado estado;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "obraSocial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObraSocialPlanEntity> planes = new ArrayList<>();
}

