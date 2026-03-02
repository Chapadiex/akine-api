package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.RefreshTokenRepositoryPort;
import com.akine_api.domain.model.RefreshToken;
import com.akine_api.infrastructure.persistence.entity.RefreshTokenEntity;
import com.akine_api.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpaRepo;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenEntity entity = jpaRepo.findById(token.getId()).orElse(new RefreshTokenEntity());
        entity.setId(token.getId());
        entity.setUserId(token.getUserId());
        entity.setTokenHash(token.getTokenHash());
        entity.setExpiresAt(token.getExpiresAt());
        entity.setRevoked(token.isRevoked());
        entity.setCreatedAt(token.getCreatedAt());
        jpaRepo.save(entity);
        return token;
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepo.findByTokenHash(tokenHash).map(e ->
                new RefreshToken(e.getId(), e.getUserId(), e.getTokenHash(),
                        e.getExpiresAt(), e.isRevoked(), e.getCreatedAt())
        );
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        jpaRepo.revokeAllByUserId(userId);
    }
}
