package com.akine_api.infrastructure.persistence.repository.convenio;

import com.akine_api.infrastructure.persistence.entity.convenio.PrestacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrestacionJpaRepository extends JpaRepository<PrestacionEntity, UUID> {
    Optional<PrestacionEntity> findByCodigoNomenclador(String codigo);
    List<PrestacionEntity> findByActivaTrue();
}
