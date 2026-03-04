package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmpresaJpaRepository extends JpaRepository<EmpresaEntity, UUID> {
    boolean existsByCuit(String cuit);
}
