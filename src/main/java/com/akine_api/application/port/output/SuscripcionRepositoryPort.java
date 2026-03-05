package com.akine_api.application.port.output;

import com.akine_api.domain.model.Suscripcion;
import com.akine_api.domain.model.SuscripcionStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SuscripcionRepositoryPort {
    Suscripcion save(Suscripcion suscripcion);
    Optional<Suscripcion> findById(UUID id);
    Optional<Suscripcion> findByConsultorioBaseId(UUID consultorioBaseId);
    Optional<Suscripcion> findTopByOwnerUserId(UUID ownerUserId);
    Optional<Suscripcion> findByTrackingToken(String trackingToken);
    List<Suscripcion> findAll(int page, int size);
    long countAll();
    List<Suscripcion> findByStatus(SuscripcionStatus status, int page, int size);
    long countByStatus(SuscripcionStatus status);
    int expireActiveDue(LocalDate today);
}
