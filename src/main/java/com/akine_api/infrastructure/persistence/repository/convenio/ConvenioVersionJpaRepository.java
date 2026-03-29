package com.akine_api.infrastructure.persistence.repository.convenio;

import com.akine_api.infrastructure.persistence.entity.convenio.ConvenioVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConvenioVersionJpaRepository extends JpaRepository<ConvenioVersionEntity, UUID> {
    List<ConvenioVersionEntity> findByConvenioIdOrderByVersionNumDesc(UUID convenioId);

    @Query("SELECT v FROM ConvenioVersionEntity v WHERE v.convenio.id = :convenioId AND v.estado = 'VIGENTE'")
    Optional<ConvenioVersionEntity> findVigenteByConvenioId(@Param("convenioId") UUID convenioId);
}
