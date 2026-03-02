package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.RoleRepositoryPort;
import com.akine_api.domain.model.Role;
import com.akine_api.domain.model.RoleName;
import com.akine_api.infrastructure.persistence.mapper.RoleEntityMapper;
import com.akine_api.infrastructure.persistence.repository.RoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository jpaRepo;
    private final RoleEntityMapper mapper;

    public RoleRepositoryAdapter(RoleJpaRepository jpaRepo, RoleEntityMapper mapper) {
        this.jpaRepo = jpaRepo;
        this.mapper = mapper;
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        return jpaRepo.findByName(name.name()).map(mapper::toDomain);
    }
}
