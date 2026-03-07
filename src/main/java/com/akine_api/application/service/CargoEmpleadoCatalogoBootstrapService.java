package com.akine_api.application.service;

import com.akine_api.application.port.output.CargoEmpleadoCatalogoRepositoryPort;
import com.akine_api.domain.model.CargoEmpleadoCatalogo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CargoEmpleadoCatalogoBootstrapService {

    private record DefaultCargo(String nombre, String slug, int orden) {}

    private static final List<DefaultCargo> DEFAULTS = List.of(
            new DefaultCargo("Recepcionista", "recepcion", 10),
            new DefaultCargo("Administrativo", "administrativo", 20),
            new DefaultCargo("Secretaria / Secretario", "secretaria-secretario", 30),
            new DefaultCargo("Coordinador administrativo", "coordinador", 40),
            new DefaultCargo("Encargado de turnos", "encargado-de-turnos", 50),
            new DefaultCargo("Facturacion / Obras sociales", "facturacion", 60),
            new DefaultCargo("Personal de limpieza", "personal-de-limpieza", 70),
            new DefaultCargo("Mantenimiento", "mantenimiento", 80),
            new DefaultCargo("Asistente de sala", "asistente", 90),
            new DefaultCargo("Auxiliar", "auxiliar", 100)
    );

    private final CargoEmpleadoCatalogoRepositoryPort repo;

    public CargoEmpleadoCatalogoBootstrapService(CargoEmpleadoCatalogoRepositoryPort repo) {
        this.repo = repo;
    }

    public void ensureDefaults() {
        for (DefaultCargo def : DEFAULTS) {
            CargoEmpleadoCatalogo cargo = repo.findBySlug(def.slug()).orElseGet(() -> new CargoEmpleadoCatalogo(
                    stableUuid(def.slug()),
                    def.nombre(),
                    def.slug(),
                    true,
                    def.orden(),
                    Instant.now(),
                    Instant.now()
            ));

            boolean changed = false;
            if (!cargo.getNombre().equals(def.nombre()) || !cargo.getSlug().equals(def.slug())) {
                cargo.rename(def.nombre(), def.slug());
                changed = true;
            }
            if (cargo.getOrden() != def.orden()) {
                cargo.setOrden(def.orden());
                changed = true;
            }
            if (!cargo.isActivo()) {
                cargo.activate();
                changed = true;
            }

            if (changed || repo.findBySlug(def.slug()).isEmpty()) {
                repo.save(cargo);
            }
        }
    }

    private UUID stableUuid(String slug) {
        return UUID.nameUUIDFromBytes(("cargo-empleado:" + slug).getBytes(StandardCharsets.UTF_8));
    }
}
