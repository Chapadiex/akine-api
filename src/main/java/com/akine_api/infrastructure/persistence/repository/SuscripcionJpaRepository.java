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
import java.util.Optional;
import java.util.UUID;

public interface SuscripcionJpaRepository extends JpaRepository<SuscripcionEntity, UUID> {

    Optional<SuscripcionEntity> findByConsultorioBaseId(UUID consultorioBaseId);
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
}
