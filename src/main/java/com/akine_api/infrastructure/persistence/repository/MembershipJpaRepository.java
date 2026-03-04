package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.MembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipJpaRepository extends JpaRepository<MembershipEntity, UUID> {

    Optional<MembershipEntity> findByUserIdAndConsultorioId(UUID userId, UUID consultorioId);

    @Query("""
           SELECT DISTINCT m.consultorioId
             FROM MembershipEntity m
             JOIN SuscripcionEntity s ON s.consultorioBaseId = m.consultorioId
            WHERE m.userId = :userId
              AND m.status = 'ACTIVE'
              AND s.status = 'ACTIVE'
              AND (s.startDate IS NULL OR s.startDate <= :today)
              AND (s.endDate IS NULL OR s.endDate >= :today)
           """)
    List<UUID> findConsultorioIdsByUserId(@Param("userId") UUID userId, @Param("today") LocalDate today);
}
