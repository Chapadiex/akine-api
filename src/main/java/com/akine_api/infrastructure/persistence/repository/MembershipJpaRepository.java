package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.MembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MembershipJpaRepository extends JpaRepository<MembershipEntity, UUID> {

    @Query("SELECT m.consultorioId FROM MembershipEntity m WHERE m.userId = :userId AND m.status = 'ACTIVE'")
    List<UUID> findConsultorioIdsByUserId(@Param("userId") UUID userId);
}
