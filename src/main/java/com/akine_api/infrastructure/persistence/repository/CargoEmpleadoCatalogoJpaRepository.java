package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.CargoEmpleadoCatalogoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CargoEmpleadoCatalogoJpaRepository extends JpaRepository<CargoEmpleadoCatalogoEntity, UUID> {
    List<CargoEmpleadoCatalogoEntity> findAllByOrderByOrdenAscNombreAsc();
    List<CargoEmpleadoCatalogoEntity> findByActivoTrueOrderByOrdenAscNombreAsc();
    Optional<CargoEmpleadoCatalogoEntity> findBySlug(String slug);
    Optional<CargoEmpleadoCatalogoEntity> findBySlugAndActivoTrue(String slug);
}
