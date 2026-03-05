package com.akine_api.application.port.output;

import com.akine_api.domain.model.SuscripcionAuditoria;

import java.util.List;
import java.util.UUID;

public interface SuscripcionAuditoriaRepositoryPort {
    SuscripcionAuditoria save(SuscripcionAuditoria auditoria);
    List<SuscripcionAuditoria> findBySuscripcionId(UUID suscripcionId);
}
