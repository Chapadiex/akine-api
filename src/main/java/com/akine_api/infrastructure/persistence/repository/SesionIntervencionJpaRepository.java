package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.SesionIntervencionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SesionIntervencionJpaRepository extends JpaRepository<SesionIntervencionEntity, UUID> {
    List<SesionIntervencionEntity> findBySesionId(UUID sesionId);
    void deleteBySesionId(UUID sesionId);
}
