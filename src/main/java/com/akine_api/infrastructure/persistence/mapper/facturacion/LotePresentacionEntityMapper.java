package com.akine_api.infrastructure.persistence.mapper.facturacion;

import com.akine_api.domain.model.facturacion.LotePresentacion;
import com.akine_api.infrastructure.persistence.entity.facturacion.LotePresentacionEntity;
import com.akine_api.infrastructure.persistence.mapper.cobertura.FinanciadorSaludEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {FinanciadorSaludEntityMapper.class, ConvenioFinanciadorEntityMapper.class})
public interface LotePresentacionEntityMapper {
    LotePresentacionEntityMapper INSTANCE = Mappers.getMapper(LotePresentacionEntityMapper.class);

    @Mapping(source = "financiador.id", target = "financiadorId")
    @Mapping(source = "convenio.id", target = "convenioId")
    LotePresentacion toDomain(LotePresentacionEntity entity);

    @Mapping(source = "financiadorId", target = "financiador.id")
    @Mapping(source = "convenioId", target = "convenio.id")
    LotePresentacionEntity toEntity(LotePresentacion domain);
}
