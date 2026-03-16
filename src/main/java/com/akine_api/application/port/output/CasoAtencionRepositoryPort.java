package com.akine_api.application.port.output;

import com.akine_api.domain.model.CasoAtencion;
import com.akine_api.domain.model.CasoAtencionEstado;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CasoAtencionRepositoryPort {

    CasoAtencion save(CasoAtencion caso);

    Optional<CasoAtencion> findById(UUID id);

    Optional<CasoAtencion> findByIdAndConsultorioId(UUID id, UUID consultorioId);

    List<CasoAtencion> findByLegajoId(UUID legajoId);

    List<CasoAtencion> findByPacienteIdAndConsultorioId(UUID pacienteId, UUID consultorioId);

    List<CasoAtencion> findByPacienteIdAndConsultorioIdAndEstadoIn(UUID pacienteId, UUID consultorioId,
                                                                    List<CasoAtencionEstado> estados);

    boolean existsById(UUID id);
}
