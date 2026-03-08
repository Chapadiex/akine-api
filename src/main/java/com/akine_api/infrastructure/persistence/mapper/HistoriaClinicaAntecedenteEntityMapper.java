package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.HistoriaClinicaAntecedente;
import com.akine_api.infrastructure.persistence.entity.HistoriaClinicaAntecedenteEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistoriaClinicaAntecedenteEntityMapper {

    default HistoriaClinicaAntecedente toDomain(HistoriaClinicaAntecedenteEntity entity) {
        if (entity == null) {
            return null;
        }
        return new HistoriaClinicaAntecedente(
                entity.getId(),
                entity.getLegajoId(),
                entity.getConsultorioId(),
                entity.getPacienteId(),
                entity.getCategoryCode(),
                entity.getCatalogItemCode(),
                entity.getLabel(),
                entity.getValueText(),
                entity.isCritical(),
                entity.getNotes(),
                entity.getCreatedByUserId(),
                entity.getUpdatedByUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default HistoriaClinicaAntecedenteEntity toEntity(HistoriaClinicaAntecedente domain) {
        if (domain == null) {
            return null;
        }
        HistoriaClinicaAntecedenteEntity entity = new HistoriaClinicaAntecedenteEntity();
        entity.setId(domain.getId());
        entity.setLegajoId(domain.getLegajoId());
        entity.setConsultorioId(domain.getConsultorioId());
        entity.setPacienteId(domain.getPacienteId());
        entity.setCategoryCode(domain.getCategoryCode());
        entity.setCatalogItemCode(domain.getCatalogItemCode());
        entity.setLabel(domain.getLabel());
        entity.setValueText(domain.getValueText());
        entity.setCritical(domain.isCritical());
        entity.setNotes(domain.getNotes());
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setUpdatedByUserId(domain.getUpdatedByUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
