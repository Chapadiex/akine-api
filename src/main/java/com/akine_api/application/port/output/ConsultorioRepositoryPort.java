package com.akine_api.application.port.output;

import com.akine_api.domain.model.Consultorio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultorioRepositoryPort {
    Consultorio save(Consultorio consultorio);
    Optional<Consultorio> findById(UUID id);
    List<Consultorio> findAll();
    List<Consultorio> findByIds(List<UUID> ids);
    List<UUID> findConsultorioIdsByUserId(UUID userId);
    /** Cuenta cuántos consultorios pertenecen a una empresa. */
    long countByEmpresaId(UUID empresaId);
    /** Genera el próximo nro_consultorio en formato AKN-000001 usando la secuencia de DB. */
    String generateNroConsultorio();
}
