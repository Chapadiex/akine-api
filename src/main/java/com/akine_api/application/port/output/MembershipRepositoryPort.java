package com.akine_api.application.port.output;

import com.akine_api.domain.model.Membership;

public interface MembershipRepositoryPort {
    Membership save(Membership membership);
}
