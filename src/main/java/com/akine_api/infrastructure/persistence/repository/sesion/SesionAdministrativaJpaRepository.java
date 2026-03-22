package com.akine_api.infrastructure.persistence.repository.sesion;

import com.akine_api.infrastructure.persistence.entity.sesion.SesionAdministrativaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SesionAdministrativaJpaRepository extends JpaRepository<SesionAdministrativaEntity, UUID> {
    Optional<SesionAdministrativaEntity> findBySesionId(UUID sesionId);
    Optional<SesionAdministrativaEntity> findByTurnoId(UUID turnoId);
    List<SesionAdministrativaEntity> findByConsultorioId(UUID consultorioId);
}
