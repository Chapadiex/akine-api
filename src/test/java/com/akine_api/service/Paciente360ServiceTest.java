package com.akine_api.service;

import com.akine_api.application.port.output.BoxRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.ObraSocialRepositoryPort;
import com.akine_api.application.port.output.PacienteConsultorioRepositoryPort;
import com.akine_api.application.port.output.PacienteRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.TurnoRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.Paciente360Service;
import com.akine_api.domain.exception.PacienteNotFoundException;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Paciente360ServiceTest {

    @Mock PacienteRepositoryPort pacienteRepo;
    @Mock PacienteConsultorioRepositoryPort pacienteConsultorioRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock TurnoRepositoryPort turnoRepo;
    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock BoxRepositoryPort boxRepo;
    @Mock ObraSocialRepositoryPort obraSocialRepo;

    Paciente360Service service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PACIENTE_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new Paciente360Service(
                pacienteRepo,
                pacienteConsultorioRepo,
                consultorioRepo,
                userRepo,
                turnoRepo,
                profesionalRepo,
                boxRepo,
                obraSocialRepo
        );
    }

    @Test
    void getHeader_whenPacienteNotLinked_throws404() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));
        when(pacienteRepo.findById(PACIENTE_ID)).thenReturn(Optional.of(samplePaciente()));
        when(pacienteConsultorioRepo.existsByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(false);

        assertThatThrownBy(() -> service.getHeader(
                CONSULTORIO_ID,
                PACIENTE_ID,
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        )).isInstanceOf(PacienteNotFoundException.class);
    }

    @Test
    void getPagos_whenNotAdmin_throws403() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));

        assertThatThrownBy(() -> service.getPagos(
                CONSULTORIO_ID,
                PACIENTE_ID,
                0,
                20,
                "administrativo@test.com",
                Set.of("ROLE_ADMINISTRATIVO")
        )).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getHeader_whenLinked_returnsSummary() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));
        when(pacienteRepo.findById(PACIENTE_ID)).thenReturn(Optional.of(samplePaciente()));
        when(pacienteConsultorioRepo.existsByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(true);
        when(turnoRepo.findByPacienteIdAndRange(PACIENTE_ID, java.time.LocalDateTime.of(2000, 1, 1, 0, 0), java.time.LocalDateTime.of(2100, 1, 1, 0, 0)))
                .thenReturn(List.of());

        var result = service.getHeader(
                CONSULTORIO_ID,
                PACIENTE_ID,
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        );

        assertThat(result.dni()).isEqualTo("30111222");
        assertThat(result.coberturaResumen()).isEqualTo("Sin cobertura registrada");
    }

    private Consultorio activeConsultorio() {
        return new Consultorio(CONSULTORIO_ID, "Consultorio", null, null, null, null, "ACTIVE", Instant.now());
    }

    private Paciente samplePaciente() {
        Instant now = Instant.now();
        return new Paciente(
                PACIENTE_ID,
                "30111222",
                "Ana",
                "Perez",
                "1155554444",
                "ana@mail.com",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true,
                null,
                now,
                now
        );
    }
}
