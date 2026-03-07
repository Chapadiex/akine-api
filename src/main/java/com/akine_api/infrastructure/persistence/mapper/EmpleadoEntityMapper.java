package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.Empleado;
import com.akine_api.infrastructure.persistence.entity.EmpleadoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmpleadoEntityMapper {

    default Empleado toDomain(EmpleadoEntity entity) {
        if (entity == null) return null;
        return new Empleado(
                entity.getId(),
                entity.getConsultorioId(),
                entity.getUserId(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getDni(),
                entity.getFechaNacimiento(),
                entity.getCargo(),
                entity.getEmail(),
                entity.getTelefono(),
                entity.getDireccion(),
                entity.getNotasInternas(),
                entity.getFechaAlta(),
                entity.getFechaBaja(),
                entity.getMotivoBaja(),
                entity.isActivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default EmpleadoEntity toEntity(Empleado domain) {
        if (domain == null) return null;
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setId(domain.getId());
        entity.setConsultorioId(domain.getConsultorioId());
        entity.setUserId(domain.getUserId());
        entity.setNombre(domain.getNombre());
        entity.setApellido(domain.getApellido());
        entity.setDni(domain.getDni());
        entity.setFechaNacimiento(domain.getFechaNacimiento());
        entity.setCargo(domain.getCargo());
        entity.setEmail(domain.getEmail());
        entity.setTelefono(domain.getTelefono());
        entity.setDireccion(domain.getDireccion());
        entity.setNotasInternas(domain.getNotasInternas());
        entity.setFechaAlta(domain.getFechaAlta());
        entity.setFechaBaja(domain.getFechaBaja());
        entity.setMotivoBaja(domain.getMotivoBaja());
        entity.setActivo(domain.isActivo());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
