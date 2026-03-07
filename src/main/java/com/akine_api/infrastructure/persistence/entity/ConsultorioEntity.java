package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "consultorios")
@Getter @Setter @NoArgsConstructor
public class ConsultorioEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 13)
    private String cuit;

    @Column(length = 500)
    private String address;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(name = "map_latitude", precision = 9, scale = 6)
    private BigDecimal mapLatitude;

    @Column(name = "map_longitude", precision = 10, scale = 6)
    private BigDecimal mapLongitude;

    @Column(name = "google_maps_url", length = 500)
    private String googleMapsUrl;

    @Column(name = "empresa_id")
    private UUID empresaId;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
