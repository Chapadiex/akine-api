package com.akine_api.infrastructure.persistence.repository.cobro;

import com.akine_api.infrastructure.persistence.entity.cobro.CobroPacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CobroPacienteJpaRepository extends JpaRepository<CobroPacienteEntity, UUID> {
    List<CobroPacienteEntity> findByCajaDiariaId(UUID cajaDiariaId);
    List<CobroPacienteEntity> findByPacienteIdAndConsultorioId(UUID pacienteId, UUID consultorioId);
    List<CobroPacienteEntity> findBySesionId(UUID sesionId);
}
