package com.akine_api.service;

import com.akine_api.application.dto.command.AddDuracionTurnoCommand;
import com.akine_api.application.port.output.ConsultorioDuracionTurnoRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.ConsultorioDuracionTurnoService;
import com.akine_api.domain.model.Consultorio;
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
class ConsultorioDuracionTurnoServiceTest {

    @Mock ConsultorioDuracionTurnoRepositoryPort duracionRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;

    ConsultorioDuracionTurnoService service;
    private static final UUID CONSULTORIO_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ConsultorioDuracionTurnoService(duracionRepo, consultorioRepo, userRepo);
    }

    @Test
    void add_duplicada_throws() {
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
        when(duracionRepo.existsByConsultorioIdAndMinutos(CONSULTORIO_ID, 30)).thenReturn(true);

        assertThatThrownBy(() -> service.add(new AddDuracionTurnoCommand(CONSULTORIO_ID, 30), "a", Set.of("ROLE_ADMIN")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_minutosInvalidos_throws() {
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
        when(duracionRepo.existsByConsultorioIdAndMinutos(CONSULTORIO_ID, 10)).thenReturn(false);

        assertThatThrownBy(() -> service.add(new AddDuracionTurnoCommand(CONSULTORIO_ID, 10), "a", Set.of("ROLE_ADMIN")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
