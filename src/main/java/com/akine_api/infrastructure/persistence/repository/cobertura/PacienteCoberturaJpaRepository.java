package com.akine_api.infrastructure.persistence.repository.cobertura;

import com.akine_api.infrastructure.persistence.entity.cobertura.PacienteCoberturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PacienteCoberturaJpaRepository extends JpaRepository<PacienteCoberturaEntity, UUID> {
    List<PacienteCoberturaEntity> findByPacienteId(UUID pacienteId);
}
