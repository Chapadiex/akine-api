package com.akine_api.domain.repository.facturacion;

import com.akine_api.domain.model.facturacion.ConvenioFinanciador;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConvenioFinanciadorRepositoryPort {
    ConvenioFinanciador save(ConvenioFinanciador convenio);
    Optional<ConvenioFinanciador> findById(UUID id);
    List<ConvenioFinanciador> findByFinanciadorId(UUID financiadorId);
    List<ConvenioFinanciador> findByConsultorioId(UUID consultorioId);

    /**
     * Busca el convenio activo y vigente para una combinación de financiador, plan y consultorio
     * en una fecha dada. Usado por LiquidacionCalculadorService (Fase 3).
     */
    Optional<ConvenioFinanciador> findVigenteByFinanciadorPlanConsultorio(
            UUID financiadorId, UUID planId, UUID consultorioId, LocalDate fecha);

    boolean existsSolapamiento(UUID financiadorId, UUID planId, UUID consultorioId,
                               LocalDate vigenciaDesde, LocalDate vigenciaHasta, UUID excludeId);
}
