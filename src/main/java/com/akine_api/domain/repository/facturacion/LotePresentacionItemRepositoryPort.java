package com.akine_api.domain.repository.facturacion;

import com.akine_api.domain.model.facturacion.LotePresentacionItem;

import java.util.List;
import java.util.UUID;

public interface LotePresentacionItemRepositoryPort {
    LotePresentacionItem save(LotePresentacionItem item);
    List<LotePresentacionItem> findByLoteId(UUID loteId);
}
