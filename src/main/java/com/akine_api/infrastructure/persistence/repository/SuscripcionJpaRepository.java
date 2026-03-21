package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.SuscripcionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SuscripcionJpaRepository extends JpaRepository<SuscripcionEntity, UUID> {

    Optional<SuscripcionEntity> findByConsultorioBaseId(UUID consultorioBaseId);
    Optional<SuscripcionEntity> findByEmpresaId(UUID empresaId);
    Optional<SuscripcionEntity> findTopByOwnerUserIdOrderByCreatedAtDesc(UUID ownerUserId);
    Optional<SuscripcionEntity> findByTrackingToken(String trackingToken);

    Page<SuscripcionEntity> findByStatus(String status, Pageable pageable);

    long countByStatus(String status);

    @Modifying
    @Query("""
            UPDATE SuscripcionEntity s
               SET s.status = 'EXPIRED',
                   s.updatedAt = :now
             WHERE s.status = 'ACTIVE'
               AND s.endDate IS NOT NULL
               AND s.endDate < :today
            """)
    int expireActiveDue(@Param("today") LocalDate today, @Param("now") Instant now);

    @Modifying
    @Query("""
            UPDATE SuscripcionEntity s
               SET s.status = 'PENDING_RENEWAL',
                   s.onboardingStep = 'PENDING_RENEWAL',
                   s.updatedAt = :now
             WHERE s.status = 'ACTIVE'
               AND s.endDate IS NOT NULL
               AND s.endDate BETWEEN :today AND :cutoff
            """)
    int markPendingRenewalDue(@Param("today") LocalDate today,
                              @Param("cutoff") LocalDate cutoff,
                              @Param("now") Instant now);

    @Modifying
    @Query("""
            UPDATE SuscripcionEntity s
               SET s.status = 'EXPIRED',
                   s.onboardingStep = 'EXPIRED',
                   s.updatedAt = :now
             WHERE s.status = 'PENDING_RENEWAL'
               AND s.endDate IS NOT NULL
               AND s.endDate < :today
            """)
    int expirePendingRenewalDue(@Param("today") LocalDate today, @Param("now") Instant now);

    List<SuscripcionEntity> findByStatusAndEndDate(String status, LocalDate endDate);

    @Query("SELECT s.status, COUNT(s) FROM SuscripcionEntity s GROUP BY s.status")
    List<Object[]> countGroupByStatus();

    @Query("SELECT s.planCode, COUNT(s) FROM SuscripcionEntity s WHERE s.status IN ('ACTIVE', 'PENDING_RENEWAL') GROUP BY s.planCode")
    List<Object[]> countActiveGroupByPlanCode();

    @Query("""
            SELECT s FROM SuscripcionEntity s
             WHERE s.status IN ('ACTIVE', 'PENDING_RENEWAL')
               AND s.endDate IS NOT NULL
               AND s.endDate BETWEEN :from AND :to
             ORDER BY s.endDate ASC
            """)
    List<SuscripcionEntity> findExpiringBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    long countByCreatedAtAfter(Instant since);

    long countByStatusAndUpdatedAtAfter(String status, Instant since);
}
