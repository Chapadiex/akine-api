package com.akine_api.infrastructure.persistence.adapter;

import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.model.Role;
import com.akine_api.domain.model.RoleName;
import com.akine_api.domain.model.User;
import com.akine_api.domain.model.UserStatus;
import com.akine_api.infrastructure.persistence.entity.RoleEntity;
import com.akine_api.infrastructure.persistence.entity.UserEntity;
import com.akine_api.infrastructure.persistence.mapper.UserEntityMapper;
import com.akine_api.infrastructure.persistence.repository.RoleJpaRepository;
import com.akine_api.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepo;
    private final RoleJpaRepository roleJpaRepo;
    private final UserEntityMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository jpaRepo,
                                 RoleJpaRepository roleJpaRepo,
                                 UserEntityMapper mapper) {
        this.jpaRepo = jpaRepo;
        this.roleJpaRepo = roleJpaRepo;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = jpaRepo.findById(user.getId()).orElse(new UserEntity());
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setPhone(user.getPhone());
        entity.setStatus(user.getStatus().name());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        Set<RoleEntity> roleEntities = user.getRoles().stream()
                .map(r -> roleJpaRepo.findByName(r.getName().name()).orElseThrow())
                .collect(Collectors.toSet());
        entity.setRoles(roleEntities);

        return mapper.toDomain(jpaRepo.save(entity));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepo.existsByEmail(email);
    }

    @Override
    public List<User> findAll(int page, int size) {
        return jpaRepo.findAll(PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return jpaRepo.count();
    }
}
