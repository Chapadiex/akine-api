package com.akine_api.application.port.output;

import com.akine_api.domain.model.SuscripcionAuditoria;

public interface SuscripcionAuditoriaRepositoryPort {
    SuscripcionAuditoria save(SuscripcionAuditoria auditoria);
}
