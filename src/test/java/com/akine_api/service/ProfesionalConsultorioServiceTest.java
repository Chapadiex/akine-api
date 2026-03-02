package com.akine_api.service;

import com.akine_api.application.dto.command.AsignarProfesionalCommand;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.ProfesionalConsultorioService;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.Profesional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfesionalConsultorioServiceTest {

    @Mock ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;

    ProfesionalConsultorioService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PROFESIONAL_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ProfesionalConsultorioService(profesionalConsultorioRepo, profesionalRepo, consultorioRepo, userRepo);
    }

    @Test
    void asignar_duplicado_throws() {
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
        when(profesionalRepo.findById(PROFESIONAL_ID))
                .thenReturn(Optional.of(new Profesional(PROFESIONAL_ID, CONSULTORIO_ID, "N", "A", "M", null, null, null, true, Instant.now())));
        when(profesionalConsultorioRepo.findByProfesionalIdAndConsultorioId(PROFESIONAL_ID, CONSULTORIO_ID))
                .thenReturn(Optional.of(new com.akine_api.domain.model.ProfesionalConsultorio(
                        UUID.randomUUID(), PROFESIONAL_ID, CONSULTORIO_ID, true, Instant.now())));

        assertThatThrownBy(() -> service.asignar(
                new AsignarProfesionalCommand(PROFESIONAL_ID, CONSULTORIO_ID), "a", Set.of("ROLE_ADMIN")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
