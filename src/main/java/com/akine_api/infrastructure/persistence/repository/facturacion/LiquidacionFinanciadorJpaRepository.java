package com.akine_api.infrastructure.persistence.repository.facturacion;

import com.akine_api.infrastructure.persistence.entity.facturacion.LiquidacionFinanciadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LiquidacionFinanciadorJpaRepository extends JpaRepository<LiquidacionFinanciadorEntity, UUID> {
}
