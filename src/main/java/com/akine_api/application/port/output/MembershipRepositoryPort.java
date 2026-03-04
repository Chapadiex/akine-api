package com.akine_api.application.port.output;

import com.akine_api.domain.model.Membership;

import java.util.Optional;
import java.util.UUID;

public interface MembershipRepositoryPort {
    Membership save(Membership membership);
    Optional<Membership> findByUserIdAndConsultorioId(UUID userId, UUID consultorioId);
}
