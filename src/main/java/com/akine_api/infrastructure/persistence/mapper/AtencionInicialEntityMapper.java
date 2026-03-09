package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.AtencionInicial;
import com.akine_api.infrastructure.persistence.entity.AtencionInicialEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AtencionInicialEntityMapper {

    default AtencionInicial toDomain(AtencionInicialEntity entity) {
        if (entity == null) return null;
        return new AtencionInicial(
                entity.getId(),
                entity.getLegajoId(),
                entity.getConsultorioId(),
                entity.getPacienteId(),
                entity.getProfesionalId(),
                entity.getFechaHora(),
                entity.getTipoIngreso(),
                entity.getMotivoConsultaBreve(),
                entity.getSintomasPrincipales(),
                entity.getTiempoEvolucion(),
                entity.getObservaciones(),
                entity.getEspecialidadDerivante(),
                entity.getProfesionalDerivante(),
                entity.getFechaPrescripcion(),
                entity.getDiagnosticoCodigo(),
                entity.getDiagnosticoNombre(),
                entity.getDiagnosticoTipo(),
                entity.getDiagnosticoCategoriaCodigo(),
                entity.getDiagnosticoCategoriaNombre(),
                entity.getDiagnosticoSubcategoria(),
                entity.getDiagnosticoRegionAnatomica(),
                entity.getDiagnosticoObservacion(),
                entity.getObservacionesPrescripcion(),
                entity.getResumenClinicoInicial(),
                entity.getHallazgosRelevantes(),
                entity.getCreatedByUserId(),
                entity.getUpdatedByUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default AtencionInicialEntity toEntity(AtencionInicial domain) {
        if (domain == null) return null;
        AtencionInicialEntity entity = new AtencionInicialEntity();
        entity.setId(domain.getId());
        entity.setLegajoId(domain.getLegajoId());
        entity.setConsultorioId(domain.getConsultorioId());
        entity.setPacienteId(domain.getPacienteId());
        entity.setProfesionalId(domain.getProfesionalId());
        entity.setFechaHora(domain.getFechaHora());
        entity.setTipoIngreso(domain.getTipoIngreso());
        entity.setMotivoConsultaBreve(domain.getMotivoConsultaBreve());
        entity.setSintomasPrincipales(domain.getSintomasPrincipales());
        entity.setTiempoEvolucion(domain.getTiempoEvolucion());
        entity.setObservaciones(domain.getObservaciones());
        entity.setEspecialidadDerivante(domain.getEspecialidadDerivante());
        entity.setProfesionalDerivante(domain.getProfesionalDerivante());
        entity.setFechaPrescripcion(domain.getFechaPrescripcion());
        entity.setDiagnosticoCodigo(domain.getDiagnosticoCodigo());
        entity.setDiagnosticoNombre(domain.getDiagnosticoNombre());
        entity.setDiagnosticoTipo(domain.getDiagnosticoTipo());
        entity.setDiagnosticoCategoriaCodigo(domain.getDiagnosticoCategoriaCodigo());
        entity.setDiagnosticoCategoriaNombre(domain.getDiagnosticoCategoriaNombre());
        entity.setDiagnosticoSubcategoria(domain.getDiagnosticoSubcategoria());
        entity.setDiagnosticoRegionAnatomica(domain.getDiagnosticoRegionAnatomica());
        entity.setDiagnosticoObservacion(domain.getDiagnosticoObservacion());
        entity.setObservacionesPrescripcion(domain.getObservacionesPrescripcion());
        entity.setResumenClinicoInicial(domain.getResumenClinicoInicial());
        entity.setHallazgosRelevantes(domain.getHallazgosRelevantes());
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setUpdatedByUserId(domain.getUpdatedByUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
