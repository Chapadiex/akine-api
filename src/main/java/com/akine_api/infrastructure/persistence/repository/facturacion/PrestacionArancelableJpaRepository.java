package com.akine_api.infrastructure.persistence.repository.facturacion;

import com.akine_api.infrastructure.persistence.entity.facturacion.PrestacionArancelableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrestacionArancelableJpaRepository extends JpaRepository<PrestacionArancelableEntity, UUID> {
    Optional<PrestacionArancelableEntity> findByCodigoInterno(String codigoInterno);
}
