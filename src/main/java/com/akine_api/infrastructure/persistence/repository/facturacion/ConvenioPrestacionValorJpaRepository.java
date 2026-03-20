package com.akine_api.infrastructure.persistence.repository.facturacion;

import com.akine_api.infrastructure.persistence.entity.facturacion.ConvenioPrestacionValorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConvenioPrestacionValorJpaRepository extends JpaRepository<ConvenioPrestacionValorEntity, UUID> {
    List<ConvenioPrestacionValorEntity> findByConvenioId(UUID convenioId);
}
