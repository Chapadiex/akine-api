package com.akine_api.application.port.output;

import com.akine_api.domain.model.Suscripcion;
import com.akine_api.domain.model.SuscripcionStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface SuscripcionRepositoryPort {
    Suscripcion save(Suscripcion suscripcion);
    Optional<Suscripcion> findById(UUID id);
    Optional<Suscripcion> findByConsultorioBaseId(UUID consultorioBaseId);
    Optional<Suscripcion> findByEmpresaId(UUID empresaId);
    Optional<Suscripcion> findTopByOwnerUserId(UUID ownerUserId);
    Optional<Suscripcion> findByTrackingToken(String trackingToken);
    List<Suscripcion> findAll(int page, int size);
    long countAll();
    List<Suscripcion> findByStatus(SuscripcionStatus status, int page, int size);
    long countByStatus(SuscripcionStatus status);
    int expireActiveDue(LocalDate today);
    /** Transiciona ACTIVE → PENDING_RENEWAL para suscripciones cuyo endDate cae entre today y cutoff (inclusive). */
    int markPendingRenewalDue(LocalDate today, LocalDate cutoff);
    /** Transiciona PENDING_RENEWAL → EXPIRED para suscripciones cuyo endDate < today. */
    int expirePendingRenewalDue(LocalDate today);
    /** Suscripciones ACTIVE o PENDING_RENEWAL cuyo endDate cae exactamente en targetDate (para emails de aviso). */
    List<Suscripcion> findByStatusAndEndDate(SuscripcionStatus status, LocalDate targetDate);

    // ── Métricas SaaS ──────────────────────────────────────────────────────
    /** Count de suscripciones agrupado por status. */
    Map<String, Long> countGroupByStatus();
    /** Count de suscripciones ACTIVE/PENDING_RENEWAL agrupado por planCode. */
    Map<String, Long> countActiveGroupByPlanCode();
    /** Suscripciones activas/pending cuyo endDate cae entre from y to (para panel de vencimientos próximos). */
    List<Suscripcion> findExpiringBetween(LocalDate from, LocalDate to);
    /** Suscripciones creadas después de since. */
    long countCreatedAfter(Instant since);
    /** Suscripciones que pasaron a EXPIRED después de since (churn aproximado por updatedAt). */
    long countExpiredSince(Instant since);
}
