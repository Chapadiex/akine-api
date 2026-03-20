package com.akine_api.infrastructure.persistence.mapper.facturacion;

import com.akine_api.domain.model.facturacion.LiquidacionFinanciador;
import com.akine_api.infrastructure.persistence.entity.facturacion.LiquidacionFinanciadorEntity;
import com.akine_api.infrastructure.persistence.mapper.cobertura.FinanciadorSaludEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {FinanciadorSaludEntityMapper.class, ConvenioFinanciadorEntityMapper.class})
public interface LiquidacionFinanciadorEntityMapper {
    LiquidacionFinanciadorEntityMapper INSTANCE = Mappers.getMapper(LiquidacionFinanciadorEntityMapper.class);

    @Mapping(source = "financiador.id", target = "financiadorId")
    @Mapping(source = "convenio.id", target = "convenioId")
    LiquidacionFinanciador toDomain(LiquidacionFinanciadorEntity entity);

    @Mapping(source = "financiadorId", target = "financiador.id")
    @Mapping(source = "convenioId", target = "convenio.id")
    LiquidacionFinanciadorEntity toEntity(LiquidacionFinanciador domain);
}
