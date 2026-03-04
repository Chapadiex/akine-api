package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ActivationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ActivationTokenJpaRepository extends JpaRepository<ActivationTokenEntity, UUID> {
    Optional<ActivationTokenEntity> findByTokenHash(String tokenHash);
    Optional<ActivationTokenEntity> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

    @Modifying
    @Query("DELETE FROM ActivationTokenEntity t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
