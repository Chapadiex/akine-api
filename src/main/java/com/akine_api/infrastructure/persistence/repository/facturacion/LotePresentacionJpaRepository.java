package com.akine_api.infrastructure.persistence.repository.facturacion;

import com.akine_api.infrastructure.persistence.entity.facturacion.LotePresentacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LotePresentacionJpaRepository extends JpaRepository<LotePresentacionEntity, UUID> {
    List<LotePresentacionEntity> findByFinanciadorId(UUID financiadorId);
}
