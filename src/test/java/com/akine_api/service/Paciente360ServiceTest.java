package com.akine_api.service;

import com.akine_api.application.port.output.BoxRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.DiagnosticoClinicoRepositoryPort;
import com.akine_api.application.port.output.ObraSocialRepositoryPort;
import com.akine_api.application.port.output.PacienteConsultorioRepositoryPort;
import com.akine_api.application.port.output.PacienteRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.SesionClinicaRepositoryPort;
import com.akine_api.application.port.output.TurnoRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.Paciente360Service;
import com.akine_api.domain.exception.PacienteNotFoundException;
import com.akine_api.domain.model.Box;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.DiagnosticoClinico;
import com.akine_api.domain.model.DiagnosticoClinicoEstado;
import com.akine_api.domain.model.HistoriaClinicaOrigenRegistro;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;
import com.akine_api.domain.model.Paciente;
import com.akine_api.domain.model.Profesional;
import com.akine_api.domain.model.SesionClinica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.time.LocalDate;
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
    @Mock SesionClinicaRepositoryPort sesionClinicaRepo;
    @Mock DiagnosticoClinicoRepositoryPort diagnosticoClinicoRepo;

    Paciente360Service service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PACIENTE_ID = UUID.randomUUID();
    private static final UUID SESION_ID = UUID.randomUUID();

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
                obraSocialRepo,
                sesionClinicaRepo,
                diagnosticoClinicoRepo
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

    @Test
    void getHistoriaClinica_readsClinicalSessionsInsteadOfTurnos() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));
        when(pacienteRepo.findById(PACIENTE_ID)).thenReturn(Optional.of(samplePaciente()));
        when(pacienteConsultorioRepo.existsByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(true);
        when(sesionClinicaRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(List.of(sampleSesionClinica()));

        var result = service.getHistoriaClinica(
                CONSULTORIO_ID,
                PACIENTE_ID,
                null,
                null,
                null,
                null,
                0,
                20,
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        );

        assertThat(result.total()).isEqualTo(1);
        assertThat(result.items()).singleElement().satisfies(item -> {
            assertThat(item.tipo()).isEqualTo("SESION");
            assertThat(item.resumen()).isEqualTo("Resumen clinico");
        });
    }

    @Test
    void getDiagnosticos_readsClinicalDiagnosticos() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));
        when(pacienteRepo.findById(PACIENTE_ID)).thenReturn(Optional.of(samplePaciente()));
        when(pacienteConsultorioRepo.existsByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(true);
        when(profesionalRepo.findByConsultorioId(CONSULTORIO_ID)).thenReturn(List.of(sampleProfesional()));
        when(sesionClinicaRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(List.of(sampleSesionClinica()));
        when(diagnosticoClinicoRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(List.of(sampleDiagnosticoClinico()));

        var result = service.getDiagnosticos(
                CONSULTORIO_ID,
                PACIENTE_ID,
                0,
                20,
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        );

        assertThat(result.totalActivos()).isEqualTo(1);
        assertThat(result.items()).singleElement().satisfies(item -> {
            assertThat(item.nombre()).isEqualTo("Diagnostico clinico");
            assertThat(item.estado()).isEqualTo("ACTIVO");
            assertThat(item.ultimaAtencionResumen()).isEqualTo("Resumen clinico");
        });
    }

    @Test
    void getAtenciones_readsClinicalSessions() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));
        when(pacienteRepo.findById(PACIENTE_ID)).thenReturn(Optional.of(samplePaciente()));
        when(pacienteConsultorioRepo.existsByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(true);
        when(profesionalRepo.findByConsultorioId(CONSULTORIO_ID)).thenReturn(List.of(sampleProfesional()));
        when(boxRepo.findByConsultorioId(CONSULTORIO_ID)).thenReturn(List.of(sampleBox()));
        when(sesionClinicaRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(List.of(sampleSesionClinica()));

        var result = service.getAtenciones(
                CONSULTORIO_ID,
                PACIENTE_ID,
                null,
                null,
                null,
                0,
                20,
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        );

        assertThat(result.total()).isEqualTo(1);
        assertThat(result.items()).singleElement().satisfies(item -> {
            assertThat(item.consultorioNombre()).isEqualTo("Consultorio");
            assertThat(item.boxNombre()).isEqualTo("Box 1");
            assertThat(item.resumen()).isEqualTo("Resumen clinico");
        });
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

    private SesionClinica sampleSesionClinica() {
        Instant now = Instant.now();
        return new SesionClinica(
                SESION_ID,
                CONSULTORIO_ID,
                PACIENTE_ID,
                sampleProfesional().getId(),
                null,
                sampleBox().getId(),
                java.time.LocalDateTime.now().minusDays(1),
                HistoriaClinicaSesionEstado.CERRADA,
                HistoriaClinicaTipoAtencion.SEGUIMIENTO,
                "Motivo",
                "Resumen clinico",
                null,
                null,
                "Evaluacion",
                "Plan",
                HistoriaClinicaOrigenRegistro.MANUAL,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                now,
                now,
                now
        );
    }

    private DiagnosticoClinico sampleDiagnosticoClinico() {
        Instant now = Instant.now();
        return new DiagnosticoClinico(
                UUID.randomUUID(),
                CONSULTORIO_ID,
                PACIENTE_ID,
                sampleProfesional().getId(),
                SESION_ID,
                "A01",
                "Diagnostico clinico",
                null,
                null,
                null,
                null,
                null,
                DiagnosticoClinicoEstado.ACTIVO,
                LocalDate.now().minusDays(5),
                null,
                "Notas",
                UUID.randomUUID(),
                UUID.randomUUID(),
                now,
                now
        );
    }

    private Profesional sampleProfesional() {
        return new Profesional(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                CONSULTORIO_ID,
                "Gregory",
                "House",
                "20111222",
                "MN123",
                "Clinica",
                "Clinica",
                "house@test.com",
                "1155551111",
                null,
                null,
                LocalDate.now().minusYears(1),
                null,
                null,
                true,
                Instant.now()
        );
    }

    private Box sampleBox() {
        return new Box(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                CONSULTORIO_ID,
                "Box 1",
                "B1",
                null,
                null,
                null,
                true,
                Instant.now()
        );
    }
}
