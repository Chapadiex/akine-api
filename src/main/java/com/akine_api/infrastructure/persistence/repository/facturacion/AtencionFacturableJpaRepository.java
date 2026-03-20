package com.akine_api.infrastructure.persistence.repository.facturacion;

import com.akine_api.infrastructure.persistence.entity.facturacion.AtencionFacturableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AtencionFacturableJpaRepository extends JpaRepository<AtencionFacturableEntity, UUID> {
    Optional<AtencionFacturableEntity> findByAtencionId(UUID atencionId);
    List<AtencionFacturableEntity> findByPacienteId(UUID pacienteId);
}
