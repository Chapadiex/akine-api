package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.EmpleadoEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmpleadoJpaRepository extends JpaRepository<EmpleadoEntity, UUID> {
    List<EmpleadoEntity> findByConsultorioId(UUID consultorioId);
    Optional<EmpleadoEntity> findByConsultorioIdAndId(UUID consultorioId, UUID id);
    Optional<EmpleadoEntity> findByUserId(UUID userId);
    boolean existsByConsultorioIdAndEmail(UUID consultorioId, String email);
    boolean existsByConsultorioIdAndDni(UUID consultorioId, String dni);
    boolean existsByConsultorioIdAndEmailAndIdNot(UUID consultorioId, String email, UUID id);
    boolean existsByConsultorioIdAndDniAndIdNot(UUID consultorioId, String dni, UUID id);

    @Modifying
    @Query("""
        UPDATE EmpleadoEntity e
        SET e.cargo = :cargoNuevo
        WHERE LOWER(e.cargo) = LOWER(:cargoAnterior)
    """)
    int updateCargoNombre(@Param("cargoAnterior") String cargoAnterior, @Param("cargoNuevo") String cargoNuevo);
}
