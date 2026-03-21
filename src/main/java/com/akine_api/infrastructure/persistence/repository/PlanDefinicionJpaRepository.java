package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.PlanDefinicionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanDefinicionJpaRepository extends JpaRepository<PlanDefinicionEntity, String> {

    List<PlanDefinicionEntity> findAllByActivoTrueOrderByOrdenAsc();
}
