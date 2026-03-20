package com.akine_api.domain.repository.facturacion;

import com.akine_api.domain.model.facturacion.AtencionFacturable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AtencionFacturableRepositoryPort {
    AtencionFacturable save(AtencionFacturable atencionFacturable);
    Optional<AtencionFacturable> findById(UUID id);
    Optional<AtencionFacturable> findByAtencionId(UUID atencionId);
    List<AtencionFacturable> findByPacienteId(UUID pacienteId);
}
