package com.akine_api.infrastructure.persistence.mapper.facturacion;

import com.akine_api.domain.model.facturacion.LotePresentacionItem;
import com.akine_api.infrastructure.persistence.entity.facturacion.LotePresentacionItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LotePresentacionItemEntityMapper {
    LotePresentacionItemEntityMapper INSTANCE = Mappers.getMapper(LotePresentacionItemEntityMapper.class);

    @Mapping(source = "lote.id", target = "loteId")
    @Mapping(source = "atencionFacturable.id", target = "atencionFacturableId")
    LotePresentacionItem toDomain(LotePresentacionItemEntity entity);

    @Mapping(source = "loteId", target = "lote.id")
    @Mapping(source = "atencionFacturableId", target = "atencionFacturable.id")
    LotePresentacionItemEntity toEntity(LotePresentacionItem domain);
}
