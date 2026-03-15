package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "consultorios")
@Getter
@Setter
@NoArgsConstructor
public class ConsultorioEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(length = 13)
    private String cuit;

    @Column(name = "legal_name", length = 255)
    private String legalName;

    @Column(length = 500)
    private String address;

    @Column(name = "access_reference", length = 255)
    private String accessReference;

    @Column(name = "floor_unit", length = 120)
    private String floorUnit;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(name = "administrative_contact", length = 255)
    private String administrativeContact;

    @Column(name = "internal_notes", length = 1000)
    private String internalNotes;

    @Column(name = "map_latitude", precision = 9, scale = 6)
    private BigDecimal mapLatitude;

    @Column(name = "map_longitude", precision = 10, scale = 6)
    private BigDecimal mapLongitude;

    @Column(name = "google_maps_url", length = 500)
    private String googleMapsUrl;

    @Column(name = "document_display_name", length = 255)
    private String documentDisplayName;

    @Column(name = "document_subtitle", length = 255)
    private String documentSubtitle;

    @Column(name = "document_logo_url", length = 500)
    private String documentLogoUrl;

    @Column(name = "document_footer", length = 1000)
    private String documentFooter;

    @Column(name = "document_show_address")
    private Boolean documentShowAddress;

    @Column(name = "document_show_phone")
    private Boolean documentShowPhone;

    @Column(name = "document_show_email")
    private Boolean documentShowEmail;

    @Column(name = "document_show_cuit")
    private Boolean documentShowCuit;

    @Column(name = "document_show_legal_name")
    private Boolean documentShowLegalName;

    @Column(name = "document_show_logo")
    private Boolean documentShowLogo;

    @Column(name = "license_number", length = 120)
    private String licenseNumber;

    @Column(name = "license_type", length = 120)
    private String licenseType;

    @Column(name = "license_expiration_date")
    private LocalDate licenseExpirationDate;

    @Column(name = "professional_director_name", length = 255)
    private String professionalDirectorName;

    @Column(name = "professional_director_license", length = 120)
    private String professionalDirectorLicense;

    @Column(name = "legal_document_summary", length = 1000)
    private String legalDocumentSummary;

    @Column(name = "legal_notes", length = 1000)
    private String legalNotes;

    @Column(name = "empresa_id")
    private UUID empresaId;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
