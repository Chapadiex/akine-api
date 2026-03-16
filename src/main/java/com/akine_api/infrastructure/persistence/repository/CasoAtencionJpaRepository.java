package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.domain.model.CasoAtencionEstado;
import com.akine_api.infrastructure.persistence.entity.CasoAtencionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CasoAtencionJpaRepository extends JpaRepository<CasoAtencionEntity, UUID> {

    Optional<CasoAtencionEntity> findByIdAndConsultorioId(UUID id, UUID consultorioId);

    List<CasoAtencionEntity> findByLegajoIdOrderByFechaAperturaDesc(UUID legajoId);

    List<CasoAtencionEntity> findByPacienteIdAndConsultorioIdOrderByFechaAperturaDesc(UUID pacienteId,
                                                                                       UUID consultorioId);

    List<CasoAtencionEntity> findByPacienteIdAndConsultorioIdAndEstadoInOrderByFechaAperturaDesc(
            UUID pacienteId, UUID consultorioId, List<CasoAtencionEstado> estados);
}
