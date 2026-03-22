package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.SesionClinica;
import com.akine_api.infrastructure.persistence.entity.SesionClinicaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SesionClinicaEntityMapper {

    default SesionClinica toDomain(SesionClinicaEntity entity) {
        if (entity == null) return null;
        SesionClinica domain = new SesionClinica(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getPacienteId(),
                entity.getProfesionalId(),
                entity.getTurnoId(),
                entity.getCasoAtencionId(),
                entity.getBoxId(),
                entity.getFechaAtencion(),
                entity.getEstado(),
                entity.getTipoAtencion(),
                entity.getMotivoConsulta(),
                entity.getResumenClinico(),
                entity.getSubjetivo(),
                entity.getObjetivo(),
                entity.getEvaluacion(),
                entity.getPlan(),
                entity.getOrigenRegistro(),
                entity.getCreatedByUserId(),
                entity.getUpdatedByUserId(),
                entity.getClosedByUserId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getClosedAt()
        );
        domain.setDuracionRealMinutos(entity.getDuracionRealMinutos());
        domain.setTratamientoRealizado(entity.getTratamientoRealizado());
        domain.setResultadoClinico(entity.getResultadoClinico());
        domain.setConductaSiguiente(entity.getConductaSiguiente());
        domain.setRequiereSeguimiento(entity.isRequiereSeguimiento());
        domain.setObservacionesClincias(entity.getObservacionesClincias());
        domain.setCerradaClinicamente(entity.isCerradaClinicamente());
        domain.setFechaCierreClinco(entity.getFechaCierreClinco());
        domain.setCierreClinicoPor(entity.getCierreClinicoPor());
        domain.setEsGrupal(entity.isEsGrupal());
        domain.setGrupoSesionId(entity.getGrupoSesionId());
        return domain;
    }

    default SesionClinicaEntity toEntity(SesionClinica domain) {
        if (domain == null) return null;
        SesionClinicaEntity entity = new SesionClinicaEntity();
        entity.setId(domain.getId());
        entity.setConsultorioId(domain.getConsultorioId());
        entity.setPacienteId(domain.getPacienteId());
        entity.setProfesionalId(domain.getProfesionalId());
        entity.setTurnoId(domain.getTurnoId());
        entity.setCasoAtencionId(domain.getCasoAtencionId());
        entity.setBoxId(domain.getBoxId());
        entity.setFechaAtencion(domain.getFechaAtencion());
        entity.setEstado(domain.getEstado());
        entity.setTipoAtencion(domain.getTipoAtencion());
        entity.setMotivoConsulta(domain.getMotivoConsulta());
        entity.setResumenClinico(domain.getResumenClinico());
        entity.setSubjetivo(domain.getSubjetivo());
        entity.setObjetivo(domain.getObjetivo());
        entity.setEvaluacion(domain.getEvaluacion());
        entity.setPlan(domain.getPlan());
        entity.setOrigenRegistro(domain.getOrigenRegistro());
        entity.setCreatedByUserId(domain.getCreatedByUserId());
        entity.setUpdatedByUserId(domain.getUpdatedByUserId());
        entity.setClosedByUserId(domain.getClosedByUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setClosedAt(domain.getClosedAt());
        entity.setDuracionRealMinutos(domain.getDuracionRealMinutos());
        entity.setTratamientoRealizado(domain.getTratamientoRealizado());
        entity.setResultadoClinico(domain.getResultadoClinico());
        entity.setConductaSiguiente(domain.getConductaSiguiente());
        entity.setRequiereSeguimiento(domain.isRequiereSeguimiento());
        entity.setObservacionesClincias(domain.getObservacionesClincias());
        entity.setCerradaClinicamente(domain.isCerradaClinicamente());
        entity.setFechaCierreClinco(domain.getFechaCierreClinco());
        entity.setCierreClinicoPor(domain.getCierreClinicoPor());
        entity.setEsGrupal(domain.isEsGrupal());
        entity.setGrupoSesionId(domain.getGrupoSesionId());
        return entity;
    }
}
