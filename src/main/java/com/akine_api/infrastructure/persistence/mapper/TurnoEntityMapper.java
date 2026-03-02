package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Turno;
import com.akine_api.infrastructure.persistence.entity.TurnoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TurnoEntityMapper {

    default Turno toDomain(TurnoEntity entity) {
        if (entity == null) return null;
        return new Turno(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getProfesionalId(),
                entity.getBoxId(),
                entity.getPacienteId(),
                entity.getFechaHoraInicio(),
                entity.getDuracionMinutos(),
                entity.getEstado(),
                entity.getMotivoConsulta(),
                entity.getNotas(),
                entity.getCreatedAt()
        );
    }

    default TurnoEntity toEntity(Turno domain) {
        if (domain == null) return null;
        TurnoEntity e = new TurnoEntity();
        e.setId(domain.getId());
        e.setConsultorioId(domain.getConsultorioId());
        e.setProfesionalId(domain.getProfesionalId());
        e.setBoxId(domain.getBoxId());
        e.setPacienteId(domain.getPacienteId());
        e.setFechaHoraInicio(domain.getFechaHoraInicio());
        e.setDuracionMinutos(domain.getDuracionMinutos());
        e.setEstado(domain.getEstado());
        e.setMotivoConsulta(domain.getMotivoConsulta());
        e.setNotas(domain.getNotas());
        e.setCreatedAt(domain.getCreatedAt());
        e.setUpdatedAt(domain.getUpdatedAt());
        return e;
    }
}
