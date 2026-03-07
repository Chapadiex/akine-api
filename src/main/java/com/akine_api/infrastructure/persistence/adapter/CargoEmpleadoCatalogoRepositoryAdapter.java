package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.CargoEmpleadoCatalogoRepositoryPort;
import com.akine_api.domain.model.CargoEmpleadoCatalogo;
import com.akine_api.infrastructure.persistence.mapper.CargoEmpleadoCatalogoEntityMapper;
import com.akine_api.infrastructure.persistence.repository.CargoEmpleadoCatalogoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CargoEmpleadoCatalogoRepositoryAdapter implements CargoEmpleadoCatalogoRepositoryPort {

    private final CargoEmpleadoCatalogoJpaRepository repo;
    private final CargoEmpleadoCatalogoEntityMapper mapper;

    public CargoEmpleadoCatalogoRepositoryAdapter(CargoEmpleadoCatalogoJpaRepository repo,
                                                  CargoEmpleadoCatalogoEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public CargoEmpleadoCatalogo save(CargoEmpleadoCatalogo cargoEmpleadoCatalogo) {
        return mapper.toDomain(repo.save(mapper.toEntity(cargoEmpleadoCatalogo)));
    }

    @Override
    public Optional<CargoEmpleadoCatalogo> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<CargoEmpleadoCatalogo> findBySlug(String slug) {
        return repo.findBySlug(slug).map(mapper::toDomain);
    }

    @Override
    public List<CargoEmpleadoCatalogo> findAllOrdered() {
        return repo.findAllByOrderByOrdenAscNombreAsc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<CargoEmpleadoCatalogo> findActiveOrdered() {
        return repo.findByActivoTrueOrderByOrdenAscNombreAsc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<CargoEmpleadoCatalogo> findBySlugAndActive(String slug) {
        return repo.findBySlugAndActivoTrue(slug).map(mapper::toDomain);
    }
}
