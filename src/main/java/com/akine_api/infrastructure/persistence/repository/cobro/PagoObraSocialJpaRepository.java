package com.akine_api.infrastructure.persistence.repository.cobro;

import com.akine_api.infrastructure.persistence.entity.cobro.PagoObraSocialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PagoObraSocialJpaRepository extends JpaRepository<PagoObraSocialEntity, UUID> {
    List<PagoObraSocialEntity> findByConsultorioId(UUID consultorioId);
    List<PagoObraSocialEntity> findByLoteId(UUID loteId);
}
