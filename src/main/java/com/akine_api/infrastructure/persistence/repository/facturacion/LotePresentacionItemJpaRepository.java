package com.akine_api.infrastructure.persistence.repository.facturacion;

import com.akine_api.infrastructure.persistence.entity.facturacion.LotePresentacionItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LotePresentacionItemJpaRepository extends JpaRepository<LotePresentacionItemEntity, UUID> {
    List<LotePresentacionItemEntity> findByLoteId(UUID loteId);
}
