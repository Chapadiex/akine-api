package com.akine_api.service;

import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.service.ConsultorioAntecedenteBootstrapService;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.infrastructure.persistence.entity.ConsultorioAntecedenteCatalogEntity;
import com.akine_api.infrastructure.persistence.repository.ConsultorioAntecedenteCatalogJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultorioAntecedenteBootstrapServiceTest {

    @Mock
    ConsultorioAntecedenteCatalogJpaRepository catalogRepo;
    @Mock
    ConsultorioRepositoryPort consultorioRepo;

    private ConsultorioAntecedenteBootstrapService service;
    private static final UUID CONSULTORIO_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ConsultorioAntecedenteBootstrapService(catalogRepo, consultorioRepo, new ObjectMapper());
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
    }

    @Test
    void ensureDefaults_whenMissing_createsCatalogRow() {
        when(catalogRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.empty());
        when(catalogRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.ensureDefaults(CONSULTORIO_ID, "system");

        verify(catalogRepo).save(any(ConsultorioAntecedenteCatalogEntity.class));
    }

    @Test
    void ensureDefaults_whenExists_doesNothing() {
        ConsultorioAntecedenteCatalogEntity existing = new ConsultorioAntecedenteCatalogEntity();
        existing.setConsultorioId(CONSULTORIO_ID);
        existing.setVersion("1.0.0");
        existing.setCatalogJson("[]");
        existing.setCreatedAt(Instant.now());
        existing.setCreatedBy("system");
        existing.setUpdatedAt(Instant.now());
        existing.setUpdatedBy("system");
        when(catalogRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(existing));

        service.ensureDefaults(CONSULTORIO_ID, "system");

        verify(catalogRepo, never()).save(any());
    }
}
