package com.akine_api.application.port.output;

import com.akine_api.domain.model.Empresa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepositoryPort {
    Empresa save(Empresa empresa);
    Optional<Empresa> findById(UUID id);
    List<Empresa> findByIds(List<UUID> ids);
    boolean existsByCuit(String cuit);
}
