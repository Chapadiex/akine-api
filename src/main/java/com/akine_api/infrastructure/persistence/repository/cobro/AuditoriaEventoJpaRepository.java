package com.akine_api.infrastructure.persistence.repository.cobro;

import com.akine_api.infrastructure.persistence.entity.cobro.AuditoriaEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditoriaEventoJpaRepository extends JpaRepository<AuditoriaEventoEntity, UUID> {
    List<AuditoriaEventoEntity> findByEntidadAndEntidadId(String entidad, UUID entidadId);
}
