package com.akine_api.infrastructure.persistence.mapper.facturacion;

import com.akine_api.domain.model.facturacion.PagoFinanciador;
import com.akine_api.infrastructure.persistence.entity.facturacion.PagoFinanciadorEntity;
import com.akine_api.infrastructure.persistence.mapper.cobertura.FinanciadorSaludEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {FinanciadorSaludEntityMapper.class, LiquidacionFinanciadorEntityMapper.class})
public interface PagoFinanciadorEntityMapper {
    PagoFinanciadorEntityMapper INSTANCE = Mappers.getMapper(PagoFinanciadorEntityMapper.class);

    @Mapping(source = "financiador.id", target = "financiadorId")
    @Mapping(source = "liquidacion.id", target = "liquidacionId")
    PagoFinanciador toDomain(PagoFinanciadorEntity entity);

    @Mapping(source = "financiadorId", target = "financiador.id")
    @Mapping(source = "liquidacionId", target = "liquidacion.id")
    PagoFinanciadorEntity toEntity(PagoFinanciador domain);
}
