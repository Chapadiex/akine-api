package com.akine_api.application.port.output;

import com.akine_api.domain.model.CargoEmpleadoCatalogo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CargoEmpleadoCatalogoRepositoryPort {
    CargoEmpleadoCatalogo save(CargoEmpleadoCatalogo cargoEmpleadoCatalogo);
    Optional<CargoEmpleadoCatalogo> findById(UUID id);
    Optional<CargoEmpleadoCatalogo> findBySlug(String slug);
    List<CargoEmpleadoCatalogo> findAllOrdered();
    List<CargoEmpleadoCatalogo> findActiveOrdered();
    Optional<CargoEmpleadoCatalogo> findBySlugAndActive(String slug);
}
