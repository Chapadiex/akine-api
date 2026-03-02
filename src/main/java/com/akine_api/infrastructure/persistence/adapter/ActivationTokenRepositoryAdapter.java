package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.ActivationTokenRepositoryPort;
import com.akine_api.domain.model.ActivationToken;
import com.akine_api.infrastructure.persistence.entity.ActivationTokenEntity;
import com.akine_api.infrastructure.persistence.repository.ActivationTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ActivationTokenRepositoryAdapter implements ActivationTokenRepositoryPort {

    private final ActivationTokenJpaRepository jpaRepo;

    public ActivationTokenRepositoryAdapter(ActivationTokenJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public ActivationToken save(ActivationToken token) {
        ActivationTokenEntity entity = jpaRepo.findById(token.getId()).orElse(new ActivationTokenEntity());
        entity.setId(token.getId());
        entity.setUserId(token.getUserId());
        entity.setTokenHash(token.getTokenHash());
        entity.setExpiresAt(token.getExpiresAt());
        entity.setUsed(token.isUsed());
        entity.setCreatedAt(token.getCreatedAt());
        jpaRepo.save(entity);
        return token;
    }

    @Override
    public Optional<ActivationToken> findByTokenHash(String tokenHash) {
        return jpaRepo.findByTokenHash(tokenHash).map(e ->
                new ActivationToken(e.getId(), e.getUserId(), e.getTokenHash(),
                        e.getExpiresAt(), e.isUsed(), e.getCreatedAt())
        );
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepo.deleteByUserId(userId);
    }
}
