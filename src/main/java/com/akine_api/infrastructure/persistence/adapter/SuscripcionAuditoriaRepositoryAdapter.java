package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.SuscripcionAuditoriaRepositoryPort;
import com.akine_api.domain.model.SuscripcionAuditoria;
import com.akine_api.infrastructure.persistence.entity.SuscripcionAuditoriaEntity;
import com.akine_api.infrastructure.persistence.repository.SuscripcionAuditoriaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SuscripcionAuditoriaRepositoryAdapter implements SuscripcionAuditoriaRepositoryPort {

    private final SuscripcionAuditoriaJpaRepository repo;

    public SuscripcionAuditoriaRepositoryAdapter(SuscripcionAuditoriaJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public SuscripcionAuditoria save(SuscripcionAuditoria auditoria) {
        SuscripcionAuditoriaEntity entity = new SuscripcionAuditoriaEntity();
        entity.setId(auditoria.id());
        entity.setSuscripcionId(auditoria.suscripcionId());
        entity.setAction(auditoria.action());
        entity.setFromStatus(auditoria.fromStatus());
        entity.setToStatus(auditoria.toStatus());
        entity.setActorUserId(auditoria.actorUserId());
        entity.setReason(auditoria.reason());
        entity.setCreatedAt(auditoria.createdAt());
        repo.save(entity);
        return auditoria;
    }

    @Override
    public List<SuscripcionAuditoria> findBySuscripcionId(UUID suscripcionId) {
        return repo.findBySuscripcionIdOrderByCreatedAtDesc(suscripcionId).stream()
                .map(entity -> new SuscripcionAuditoria(
                        entity.getId(),
                        entity.getSuscripcionId(),
                        entity.getAction(),
                        entity.getFromStatus(),
                        entity.getToStatus(),
                        entity.getActorUserId(),
                        entity.getReason(),
                        entity.getCreatedAt()
                ))
                .toList();
    }
}
