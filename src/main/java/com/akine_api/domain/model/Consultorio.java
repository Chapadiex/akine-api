package com.akine_api.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Consultorio {

    private final UUID id;
    private String name;
    private String description;
    private String logoUrl;
    private String cuit;
    private String legalName;
    private String address;
    private String geoAddress;
    private String accessReference;
    private String floorUnit;
    private String phone;
    private String email;
    private String administrativeContact;
    private String internalNotes;
    private BigDecimal mapLatitude;
    private BigDecimal mapLongitude;
    private String googleMapsUrl;
    private String documentDisplayName;
    private String documentSubtitle;
    private String documentLogoUrl;
    private String documentFooter;
    private Boolean documentShowAddress;
    private Boolean documentShowPhone;
    private Boolean documentShowEmail;
    private Boolean documentShowCuit;
    private Boolean documentShowLegalName;
    private Boolean documentShowLogo;
    private String licenseNumber;
    private String licenseType;
    private LocalDate licenseExpirationDate;
    private String professionalDirectorName;
    private String professionalDirectorLicense;
    private String legalDocumentSummary;
    private String legalNotes;
    private String status;
    private UUID empresaId;
    private final Instant createdAt;
    private Instant updatedAt;

    public Consultorio(UUID id, String name, String cuit, String address,
                       String phone, String email, String status, Instant createdAt) {
        this(
                id, name, null, null, cuit, null, address, null, null, null, phone, email, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, status, null, createdAt
        );
    }

    public Consultorio(UUID id, String name, String cuit, String address,
                       String phone, String email, BigDecimal mapLatitude, BigDecimal mapLongitude,
                       String googleMapsUrl, String status, Instant createdAt) {
        this(
                id, name, null, null, cuit, null, address, null, null, null, phone, email, null, null,
                mapLatitude, mapLongitude, googleMapsUrl, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, status, null,
                createdAt
        );
    }

    public Consultorio(UUID id, String name, String cuit, String address,
                       String phone, String email, String status, UUID empresaId, Instant createdAt) {
        this(
                id, name, null, null, cuit, null, address, null, null, null, phone, email, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, status, empresaId, createdAt
        );
    }

    public Consultorio(
            UUID id,
            String name,
            String description,
            String logoUrl,
            String cuit,
            String legalName,
            String address,
            String geoAddress,
            String accessReference,
            String floorUnit,
            String phone,
            String email,
            String administrativeContact,
            String internalNotes,
            BigDecimal mapLatitude,
            BigDecimal mapLongitude,
            String googleMapsUrl,
            String documentDisplayName,
            String documentSubtitle,
            String documentLogoUrl,
            String documentFooter,
            Boolean documentShowAddress,
            Boolean documentShowPhone,
            Boolean documentShowEmail,
            Boolean documentShowCuit,
            Boolean documentShowLegalName,
            Boolean documentShowLogo,
            String licenseNumber,
            String licenseType,
            LocalDate licenseExpirationDate,
            String professionalDirectorName,
            String professionalDirectorLicense,
            String legalDocumentSummary,
            String legalNotes,
            String status,
            UUID empresaId,
            Instant createdAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.logoUrl = logoUrl;
        this.cuit = cuit;
        this.legalName = legalName;
        this.address = address;
        this.geoAddress = geoAddress;
        this.accessReference = accessReference;
        this.floorUnit = floorUnit;
        this.phone = phone;
        this.email = email;
        this.administrativeContact = administrativeContact;
        this.internalNotes = internalNotes;
        this.mapLatitude = mapLatitude;
        this.mapLongitude = mapLongitude;
        this.googleMapsUrl = googleMapsUrl;
        this.documentDisplayName = documentDisplayName;
        this.documentSubtitle = documentSubtitle;
        this.documentLogoUrl = documentLogoUrl;
        this.documentFooter = documentFooter;
        this.documentShowAddress = documentShowAddress;
        this.documentShowPhone = documentShowPhone;
        this.documentShowEmail = documentShowEmail;
        this.documentShowCuit = documentShowCuit;
        this.documentShowLegalName = documentShowLegalName;
        this.documentShowLogo = documentShowLogo;
        this.licenseNumber = licenseNumber;
        this.licenseType = licenseType;
        this.licenseExpirationDate = licenseExpirationDate;
        this.professionalDirectorName = professionalDirectorName;
        this.professionalDirectorLicense = professionalDirectorLicense;
        this.legalDocumentSummary = legalDocumentSummary;
        this.legalNotes = legalNotes;
        this.status = status;
        this.empresaId = empresaId;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void update(
            String name,
            String description,
            String logoUrl,
            String cuit,
            String legalName,
            String address,
            String geoAddress,
            String accessReference,
            String floorUnit,
            String phone,
            String email,
            String administrativeContact,
            String internalNotes,
            BigDecimal mapLatitude,
            BigDecimal mapLongitude,
            String googleMapsUrl,
            String documentDisplayName,
            String documentSubtitle,
            String documentLogoUrl,
            String documentFooter,
            Boolean documentShowAddress,
            Boolean documentShowPhone,
            Boolean documentShowEmail,
            Boolean documentShowCuit,
            Boolean documentShowLegalName,
            Boolean documentShowLogo,
            String licenseNumber,
            String licenseType,
            LocalDate licenseExpirationDate,
            String professionalDirectorName,
            String professionalDirectorLicense,
            String legalDocumentSummary,
            String legalNotes,
            String status
    ) {
        this.name = name;
        this.description = description;
        this.logoUrl = logoUrl;
        this.cuit = cuit;
        this.legalName = legalName;
        this.address = address;
        this.geoAddress = geoAddress;
        this.accessReference = accessReference;
        this.floorUnit = floorUnit;
        this.phone = phone;
        this.email = email;
        this.administrativeContact = administrativeContact;
        this.internalNotes = internalNotes;
        this.mapLatitude = mapLatitude;
        this.mapLongitude = mapLongitude;
        this.googleMapsUrl = googleMapsUrl;
        this.documentDisplayName = documentDisplayName;
        this.documentSubtitle = documentSubtitle;
        this.documentLogoUrl = documentLogoUrl;
        this.documentFooter = documentFooter;
        this.documentShowAddress = documentShowAddress;
        this.documentShowPhone = documentShowPhone;
        this.documentShowEmail = documentShowEmail;
        this.documentShowCuit = documentShowCuit;
        this.documentShowLegalName = documentShowLegalName;
        this.documentShowLogo = documentShowLogo;
        this.licenseNumber = licenseNumber;
        this.licenseType = licenseType;
        this.licenseExpirationDate = licenseExpirationDate;
        this.professionalDirectorName = professionalDirectorName;
        this.professionalDirectorLicense = professionalDirectorLicense;
        this.legalDocumentSummary = legalDocumentSummary;
        this.legalNotes = legalNotes;
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public void inactivate() {
        this.status = "INACTIVE";
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.status = "ACTIVE";
        this.updatedAt = Instant.now();
    }

    public void assignEmpresa(UUID empresaId) {
        this.empresaId = empresaId;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() { return "ACTIVE".equals(this.status); }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLogoUrl() { return logoUrl; }
    public String getCuit() { return cuit; }
    public String getLegalName() { return legalName; }
    public String getAddress() { return address; }
    public String getGeoAddress() { return geoAddress; }
    public String getAccessReference() { return accessReference; }
    public String getFloorUnit() { return floorUnit; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAdministrativeContact() { return administrativeContact; }
    public String getInternalNotes() { return internalNotes; }
    public BigDecimal getMapLatitude() { return mapLatitude; }
    public BigDecimal getMapLongitude() { return mapLongitude; }
    public String getGoogleMapsUrl() { return googleMapsUrl; }
    public String getDocumentDisplayName() { return documentDisplayName; }
    public String getDocumentSubtitle() { return documentSubtitle; }
    public String getDocumentLogoUrl() { return documentLogoUrl; }
    public String getDocumentFooter() { return documentFooter; }
    public Boolean getDocumentShowAddress() { return documentShowAddress; }
    public Boolean getDocumentShowPhone() { return documentShowPhone; }
    public Boolean getDocumentShowEmail() { return documentShowEmail; }
    public Boolean getDocumentShowCuit() { return documentShowCuit; }
    public Boolean getDocumentShowLegalName() { return documentShowLegalName; }
    public Boolean getDocumentShowLogo() { return documentShowLogo; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getLicenseType() { return licenseType; }
    public LocalDate getLicenseExpirationDate() { return licenseExpirationDate; }
    public String getProfessionalDirectorName() { return professionalDirectorName; }
    public String getProfessionalDirectorLicense() { return professionalDirectorLicense; }
    public String getLegalDocumentSummary() { return legalDocumentSummary; }
    public String getLegalNotes() { return legalNotes; }
    public String getStatus() { return status; }
    public UUID getEmpresaId() { return empresaId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
