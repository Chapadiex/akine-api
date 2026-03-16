package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.CasoAtencion;
import com.akine_api.infrastructure.persistence.entity.CasoAtencionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CasoAtencionEntityMapper {

    default CasoAtencion toDomain(CasoAtencionEntity entity) {
        if (entity == null) return null;
        return new CasoAtencion(
                entity.getId(),
                entity.getLegajoId(),
                entity.getConsultorioId(),
                entity.getPacienteId(),
                entity.getProfesionalResponsableId(),
                entity.getTipoOrigen(),
                entity.getFechaApertura(),
                entity.getMotivoConsulta(),
                entity.getDiagnosticoMedico(),
                entity.getDiagnosticoFuncional(),
                entity.getAfeccionPrincipal(),
                entity.getCoberturaId(),
                entity.getEstado(),
                entity.getPrioridad(),
                entity.getAtencionInicialId(),
                entity.getCreatedByUserId(),
                entity.getUpdatedByUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default CasoAtencionEntity toEntity(CasoAtencion domain) {
        if (domain == null) return null;
        CasoAtencionEntity e = new CasoAtencionEntity();
        e.setId(domain.getId());
        e.setLegajoId(domain.getLegajoId());
        e.setConsultorioId(domain.getConsultorioId());
        e.setPacienteId(domain.getPacienteId());
        e.setProfesionalResponsableId(domain.getProfesionalResponsableId());
        e.setTipoOrigen(domain.getTipoOrigen());
        e.setFechaApertura(domain.getFechaApertura());
        e.setMotivoConsulta(domain.getMotivoConsulta());
        e.setDiagnosticoMedico(domain.getDiagnosticoMedico());
        e.setDiagnosticoFuncional(domain.getDiagnosticoFuncional());
        e.setAfeccionPrincipal(domain.getAfeccionPrincipal());
        e.setCoberturaId(domain.getCoberturaId());
        e.setEstado(domain.getEstado());
        e.setPrioridad(domain.getPrioridad());
        e.setAtencionInicialId(domain.getAtencionInicialId());
        e.setCreatedByUserId(domain.getCreatedByUserId());
        e.setUpdatedByUserId(domain.getUpdatedByUserId());
        e.setCreatedAt(domain.getCreatedAt());
        e.setUpdatedAt(domain.getUpdatedAt());
        return e;
    }
}
