package com.akine_api.infrastructure.persistence.repository;

import com.akine_api.infrastructure.persistence.entity.ConsultorioDiagnosticosMedicosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsultorioDiagnosticosMedicosJpaRepository extends JpaRepository<ConsultorioDiagnosticosMedicosEntity, UUID> {
}
