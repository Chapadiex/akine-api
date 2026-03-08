package com.akine_api.application.port.output;

import com.akine_api.domain.model.AtencionInicial;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AtencionInicialRepositoryPort {
    AtencionInicial save(AtencionInicial atencionInicial);
    Optional<AtencionInicial> findById(UUID id);
    Optional<AtencionInicial> findLatestByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId);
    List<AtencionInicial> findByConsultorioIdAndPacienteId(UUID consultorioId, UUID pacienteId);
}
