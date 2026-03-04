package com.akine_api.application.port.output;

import com.akine_api.domain.model.ActivationToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ActivationTokenRepositoryPort {
    ActivationToken save(ActivationToken token);
    Optional<ActivationToken> findByTokenHash(String tokenHash);
    Optional<Instant> findLastCreatedAtByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
