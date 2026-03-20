package com.akine_api.infrastructure.persistence.mapper.cobertura;

import com.akine_api.domain.model.cobertura.PacienteCobertura;
import com.akine_api.infrastructure.persistence.entity.cobertura.PacienteCoberturaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {FinanciadorSaludEntityMapper.class, PlanFinanciadorEntityMapper.class})
public interface PacienteCoberturaEntityMapper {
    PacienteCoberturaEntityMapper INSTANCE = Mappers.getMapper(PacienteCoberturaEntityMapper.class);

    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "financiador.id", target = "financiadorId")
    @Mapping(source = "plan.id", target = "planId")
    PacienteCobertura toDomain(PacienteCoberturaEntity entity);

    @Mapping(source = "pacienteId", target = "paciente.id")
    @Mapping(source = "financiadorId", target = "financiador.id")
    @Mapping(source = "planId", target = "plan.id")
    PacienteCoberturaEntity toEntity(PacienteCobertura domain);
}
