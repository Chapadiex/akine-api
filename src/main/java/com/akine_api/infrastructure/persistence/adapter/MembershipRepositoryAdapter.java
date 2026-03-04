package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.MembershipRepositoryPort;
import com.akine_api.domain.model.Membership;
import com.akine_api.domain.model.MembershipRole;
import com.akine_api.domain.model.MembershipStatus;
import com.akine_api.infrastructure.persistence.entity.MembershipEntity;
import com.akine_api.infrastructure.persistence.repository.MembershipJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class MembershipRepositoryAdapter implements MembershipRepositoryPort {

    private final MembershipJpaRepository jpaRepo;

    public MembershipRepositoryAdapter(MembershipJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public Membership save(Membership membership) {
        MembershipEntity entity = jpaRepo.findById(membership.getId()).orElse(new MembershipEntity());
        entity.setId(membership.getId());
        entity.setUserId(membership.getUserId());
        entity.setConsultorioId(membership.getConsultorioId());
        entity.setRoleInConsultorio(membership.getRoleInConsultorio().name());
        entity.setStatus(membership.getStatus().name());
        entity.setCreatedAt(membership.getCreatedAt());
        jpaRepo.save(entity);
        return membership;
    }

    @Override
    public Optional<Membership> findByUserIdAndConsultorioId(UUID userId, UUID consultorioId) {
        return jpaRepo.findByUserIdAndConsultorioId(userId, consultorioId)
                .map(e -> new Membership(
                        e.getId(),
                        e.getUserId(),
                        e.getConsultorioId(),
                        MembershipRole.valueOf(e.getRoleInConsultorio()),
                        MembershipStatus.valueOf(e.getStatus()),
                        e.getCreatedAt()
                ));
    }
}
