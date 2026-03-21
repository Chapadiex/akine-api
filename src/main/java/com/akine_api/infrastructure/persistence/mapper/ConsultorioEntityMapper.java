package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Consultorio;
import com.akine_api.infrastructure.persistence.entity.ConsultorioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsultorioEntityMapper {

    default Consultorio toDomain(ConsultorioEntity entity) {
        if (entity == null) return null;
        return new Consultorio(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getLogoUrl(),
                entity.getCuit(),
                entity.getLegalName(),
                entity.getAddress(),
                entity.getGeoAddress(),
                entity.getAccessReference(),
                entity.getFloorUnit(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getAdministrativeContact(),
                entity.getInternalNotes(),
                entity.getMapLatitude(),
                entity.getMapLongitude(),
                entity.getGoogleMapsUrl(),
                entity.getDocumentDisplayName(),
                entity.getDocumentSubtitle(),
                entity.getDocumentLogoUrl(),
                entity.getDocumentFooter(),
                entity.getDocumentShowAddress(),
                entity.getDocumentShowPhone(),
                entity.getDocumentShowEmail(),
                entity.getDocumentShowCuit(),
                entity.getDocumentShowLegalName(),
                entity.getDocumentShowLogo(),
                entity.getLicenseNumber(),
                entity.getLicenseType(),
                entity.getLicenseExpirationDate(),
                entity.getProfessionalDirectorName(),
                entity.getProfessionalDirectorLicense(),
                entity.getLegalDocumentSummary(),
                entity.getLegalNotes(),
                entity.getStatus(),
                entity.getEmpresaId(),
                entity.getNroConsultorio(),
                entity.getSlug(),
                entity.getCreatedAt()
        );
    }

    default ConsultorioEntity toEntity(Consultorio domain) {
        if (domain == null) return null;
        ConsultorioEntity entity = new ConsultorioEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setLogoUrl(domain.getLogoUrl());
        entity.setCuit(domain.getCuit());
        entity.setLegalName(domain.getLegalName());
        entity.setAddress(domain.getAddress());
        entity.setGeoAddress(domain.getGeoAddress());
        entity.setAccessReference(domain.getAccessReference());
        entity.setFloorUnit(domain.getFloorUnit());
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail());
        entity.setAdministrativeContact(domain.getAdministrativeContact());
        entity.setInternalNotes(domain.getInternalNotes());
        entity.setMapLatitude(domain.getMapLatitude());
        entity.setMapLongitude(domain.getMapLongitude());
        entity.setGoogleMapsUrl(domain.getGoogleMapsUrl());
        entity.setDocumentDisplayName(domain.getDocumentDisplayName());
        entity.setDocumentSubtitle(domain.getDocumentSubtitle());
        entity.setDocumentLogoUrl(domain.getDocumentLogoUrl());
        entity.setDocumentFooter(domain.getDocumentFooter());
        entity.setDocumentShowAddress(domain.getDocumentShowAddress());
        entity.setDocumentShowPhone(domain.getDocumentShowPhone());
        entity.setDocumentShowEmail(domain.getDocumentShowEmail());
        entity.setDocumentShowCuit(domain.getDocumentShowCuit());
        entity.setDocumentShowLegalName(domain.getDocumentShowLegalName());
        entity.setDocumentShowLogo(domain.getDocumentShowLogo());
        entity.setLicenseNumber(domain.getLicenseNumber());
        entity.setLicenseType(domain.getLicenseType());
        entity.setLicenseExpirationDate(domain.getLicenseExpirationDate());
        entity.setProfessionalDirectorName(domain.getProfessionalDirectorName());
        entity.setProfessionalDirectorLicense(domain.getProfessionalDirectorLicense());
        entity.setLegalDocumentSummary(domain.getLegalDocumentSummary());
        entity.setLegalNotes(domain.getLegalNotes());
        entity.setEmpresaId(domain.getEmpresaId());
        entity.setNroConsultorio(domain.getNroConsultorio());
        entity.setSlug(domain.getSlug());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
