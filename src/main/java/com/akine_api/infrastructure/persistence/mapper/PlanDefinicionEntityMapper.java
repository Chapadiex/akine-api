package com.akine_api.infrastructure.persistence.mapper;

import com.akine_api.domain.model.PlanDefinicion;
import com.akine_api.infrastructure.persistence.entity.PlanDefinicionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanDefinicionEntityMapper {

    default PlanDefinicion toDomain(PlanDefinicionEntity entity) {
        if (entity == null) return null;
        return new PlanDefinicion(
                entity.getCodigo(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getPrecioMensual(),
                entity.getPrecioAnual(),
                entity.getMaxConsultorios(),
                entity.getMaxProfesionales(),
                entity.getMaxPacientes(),
                entity.isModuloFacturacion(),
                entity.isModuloHistoriaClinica(),
                entity.isModuloObrasSociales(),
                entity.isModuloColaboradores(),
                entity.isActivo(),
                entity.getOrden()
        );
    }
}
