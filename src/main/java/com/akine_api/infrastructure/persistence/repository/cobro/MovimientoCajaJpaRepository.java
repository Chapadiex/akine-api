package com.akine_api.infrastructure.persistence.repository.cobro;

import com.akine_api.infrastructure.persistence.entity.cobro.MovimientoCajaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovimientoCajaJpaRepository extends JpaRepository<MovimientoCajaEntity, UUID> {
    List<MovimientoCajaEntity> findByCajaDiariaId(UUID cajaDiariaId);
}
