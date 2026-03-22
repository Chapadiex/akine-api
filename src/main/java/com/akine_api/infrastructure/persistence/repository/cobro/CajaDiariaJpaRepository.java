package com.akine_api.infrastructure.persistence.repository.cobro;

import com.akine_api.domain.model.cobro.CajaDiariaEstado;
import com.akine_api.infrastructure.persistence.entity.cobro.CajaDiariaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CajaDiariaJpaRepository extends JpaRepository<CajaDiariaEntity, UUID> {

    Optional<CajaDiariaEntity> findByConsultorioIdAndFechaOperativaAndTurnoCajaAndEstado(
            UUID consultorioId, LocalDate fechaOperativa, String turnoCaja, CajaDiariaEstado estado);

    Optional<CajaDiariaEntity> findByConsultorioIdAndFechaOperativaAndTurnoCajaIsNullAndEstado(
            UUID consultorioId, LocalDate fechaOperativa, CajaDiariaEstado estado);

    List<CajaDiariaEntity> findByConsultorioIdAndFechaOperativa(UUID consultorioId, LocalDate fechaOperativa);

    boolean existsByConsultorioIdAndFechaOperativaAndTurnoCajaAndEstado(
            UUID consultorioId, LocalDate fechaOperativa, String turnoCaja, CajaDiariaEstado estado);

    boolean existsByConsultorioIdAndFechaOperativaAndTurnoCajaIsNullAndEstado(
            UUID consultorioId, LocalDate fechaOperativa, CajaDiariaEstado estado);
}
