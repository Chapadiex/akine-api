package com.akine_api.infrastructure.persistence.mapper.facturacion;

import com.akine_api.domain.model.facturacion.ConvenioFinanciador;
import com.akine_api.infrastructure.persistence.entity.facturacion.ConvenioFinanciadorEntity;
import com.akine_api.infrastructure.persistence.mapper.cobertura.FinanciadorSaludEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {FinanciadorSaludEntityMapper.class})
public interface ConvenioFinanciadorEntityMapper {
    ConvenioFinanciadorEntityMapper INSTANCE = Mappers.getMapper(ConvenioFinanciadorEntityMapper.class);

    @Mapping(source = "financiador.id", target = "financiadorId")
    ConvenioFinanciador toDomain(ConvenioFinanciadorEntity entity);

    @Mapping(source = "financiadorId", target = "financiador.id")
    ConvenioFinanciadorEntity toEntity(ConvenioFinanciador domain);
}
