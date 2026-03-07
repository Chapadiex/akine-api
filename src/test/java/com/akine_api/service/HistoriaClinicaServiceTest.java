package com.akine_api.service;

import com.akine_api.application.dto.command.CreateSesionClinicaCommand;
import com.akine_api.application.dto.command.ResolveDiagnosticoClinicoCommand;
import com.akine_api.application.dto.command.UpdateSesionClinicaCommand;
import com.akine_api.application.port.output.AdjuntoClinicoRepositoryPort;
import com.akine_api.application.port.output.AttachmentStoragePort;
import com.akine_api.application.port.output.BoxRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.DiagnosticoClinicoRepositoryPort;
import com.akine_api.application.port.output.PacienteConsultorioRepositoryPort;
import com.akine_api.application.port.output.PacienteRepositoryPort;
import com.akine_api.application.port.output.ProfesionalConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.SesionClinicaRepositoryPort;
import com.akine_api.application.port.output.TurnoRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.HistoriaClinicaService;
import com.akine_api.domain.exception.HistoriaClinicaConflictException;
import com.akine_api.domain.exception.HistoriaClinicaValidationException;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.DiagnosticoClinico;
import com.akine_api.domain.model.DiagnosticoClinicoEstado;
import com.akine_api.domain.model.HistoriaClinicaOrigenRegistro;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;
import com.akine_api.domain.model.Paciente;
import com.akine_api.domain.model.Profesional;
import com.akine_api.domain.model.ProfesionalConsultorio;
import com.akine_api.domain.model.SesionClinica;
import com.akine_api.domain.model.User;
import com.akine_api.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HistoriaClinicaServiceTest {

    @Mock SesionClinicaRepositoryPort sesionRepo;
    @Mock DiagnosticoClinicoRepositoryPort diagnosticoRepo;
    @Mock AdjuntoClinicoRepositoryPort adjuntoRepo;
    @Mock AttachmentStoragePort attachmentStorage;
    @Mock PacienteRepositoryPort pacienteRepo;
    @Mock PacienteConsultorioRepositoryPort pacienteConsultorioRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    @Mock TurnoRepositoryPort turnoRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock BoxRepositoryPort boxRepo;

    private HistoriaClinicaService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PACIENTE_ID = UUID.randomUUID();
    private static final UUID PROFESIONAL_ID = UUID.randomUUID();
    private static final UUID OTHER_PROFESIONAL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SESION_ID = UUID.randomUUID();
    private static final UUID DIAGNOSTICO_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new HistoriaClinicaService(
                sesionRepo,
                diagnosticoRepo,
                adjuntoRepo,
                attachmentStorage,
                pacienteRepo,
                pacienteConsultorioRepo,
                consultorioRepo,
                profesionalRepo,
                profesionalConsultorioRepo,
                turnoRepo,
                userRepo,
                boxRepo
        );
        mockAccessBase();
        when(sesionRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(diagnosticoRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createSesion_asAdmin_createsDraftSession() {
        var result = service.createSesion(
                new CreateSesionClinicaCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        PROFESIONAL_ID,
                        null,
                        null,
                        LocalDateTime.now(),
                        HistoriaClinicaTipoAtencion.EVALUACION,
                        "Motivo",
                        "Resumen",
                        "Subjetivo",
                        "Objetivo",
                        "Evaluacion",
                        "Plan",
                        null
                ),
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        );

        assertThat(result.estado()).isEqualTo(HistoriaClinicaSesionEstado.BORRADOR);
        assertThat(result.pacienteId()).isEqualTo(PACIENTE_ID);
        assertThat(result.profesionalId()).isEqualTo(PROFESIONAL_ID);
    }

    @Test
    void createSesion_asProfessionalForAnotherProfessional_throws403() {
        when(profesionalRepo.findByEmail("prof@test.com"))
                .thenReturn(Optional.of(sampleProfesional(PROFESIONAL_ID, "prof@test.com")));

        assertThatThrownBy(() -> service.createSesion(
                new CreateSesionClinicaCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        OTHER_PROFESIONAL_ID,
                        null,
                        null,
                        LocalDateTime.now(),
                        HistoriaClinicaTipoAtencion.SEGUIMIENTO,
                        "Motivo",
                        "Resumen",
                        null,
                        null,
                        null,
                        null,
                        null
                ),
                "prof@test.com",
                Set.of("ROLE_PROFESIONAL")
        )).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updateSesion_whenClosed_throwsConflict() {
        when(sesionRepo.findById(SESION_ID)).thenReturn(Optional.of(sampleSesion(HistoriaClinicaSesionEstado.CERRADA)));

        assertThatThrownBy(() -> service.updateSesion(
                new UpdateSesionClinicaCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        SESION_ID,
                        PROFESIONAL_ID,
                        null,
                        null,
                        LocalDateTime.now(),
                        HistoriaClinicaTipoAtencion.TRATAMIENTO,
                        "Motivo",
                        "Resumen",
                        null,
                        null,
                        null,
                        null,
                        null
                ),
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        )).isInstanceOf(HistoriaClinicaConflictException.class);
    }

    @Test
    void resolveDiagnostico_changesLifecycleToResolved() {
        when(diagnosticoRepo.findById(DIAGNOSTICO_ID)).thenReturn(Optional.of(sampleDiagnostico(DiagnosticoClinicoEstado.ACTIVO)));

        var result = service.resolveDiagnostico(
                new ResolveDiagnosticoClinicoCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        DIAGNOSTICO_ID,
                        LocalDate.now(),
                        null
                ),
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        );

        assertThat(result.estado()).isEqualTo(DiagnosticoClinicoEstado.RESUELTO);
        assertThat(result.fechaFin()).isEqualTo(LocalDate.now());
    }

    @Test
    void addAdjunto_withInvalidExtension_throwsValidation() {
        when(sesionRepo.findById(SESION_ID)).thenReturn(Optional.of(sampleSesion(HistoriaClinicaSesionEstado.BORRADOR)));

        assertThatThrownBy(() -> service.addAdjunto(
                CONSULTORIO_ID,
                PACIENTE_ID,
                SESION_ID,
                new MockMultipartFile("file", "nota.exe", "application/octet-stream", new byte[] {1, 2, 3}),
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        )).isInstanceOf(HistoriaClinicaValidationException.class);
    }

    private void mockAccessBase() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(new Consultorio(
                CONSULTORIO_ID,
                "Consultorio",
                null,
                null,
                null,
                null,
                "ACTIVE",
                Instant.now()
        )));
        when(pacienteRepo.findById(PACIENTE_ID)).thenReturn(Optional.of(samplePaciente()));
        when(pacienteConsultorioRepo.existsByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID)).thenReturn(true);
        when(profesionalRepo.findById(PROFESIONAL_ID)).thenReturn(Optional.of(sampleProfesional(PROFESIONAL_ID, "admin@test.com")));
        when(profesionalRepo.findById(OTHER_PROFESIONAL_ID)).thenReturn(Optional.of(sampleProfesional(OTHER_PROFESIONAL_ID, "other@test.com")));
        when(profesionalConsultorioRepo.findByProfesionalIdAndConsultorioId(PROFESIONAL_ID, CONSULTORIO_ID))
                .thenReturn(Optional.of(new ProfesionalConsultorio(UUID.randomUUID(), PROFESIONAL_ID, CONSULTORIO_ID, true, Instant.now())));
        when(profesionalConsultorioRepo.findByProfesionalIdAndConsultorioId(OTHER_PROFESIONAL_ID, CONSULTORIO_ID))
                .thenReturn(Optional.of(new ProfesionalConsultorio(UUID.randomUUID(), OTHER_PROFESIONAL_ID, CONSULTORIO_ID, true, Instant.now())));
        when(userRepo.findByEmail("admin@test.com")).thenReturn(Optional.of(new User(
                USER_ID,
                "admin@test.com",
                "hash",
                "Admin",
                "Test",
                null,
                UserStatus.ACTIVE,
                Instant.now()
        )));
        when(userRepo.findByEmail("prof@test.com")).thenReturn(Optional.of(new User(
                USER_ID,
                "prof@test.com",
                "hash",
                "Prof",
                "Test",
                null,
                UserStatus.ACTIVE,
                Instant.now()
        )));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));
        when(diagnosticoRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID)).thenReturn(List.of());
        when(adjuntoRepo.findBySesionId(SESION_ID)).thenReturn(List.of());
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

    private Profesional sampleProfesional(UUID profesionalId, String email) {
        return new Profesional(
                profesionalId,
                CONSULTORIO_ID,
                "Gregory",
                "House",
                "20111222",
                "MN123",
                "Clinica",
                "Clinica",
                email,
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

    private SesionClinica sampleSesion(HistoriaClinicaSesionEstado estado) {
        Instant now = Instant.now();
        return new SesionClinica(
                SESION_ID,
                CONSULTORIO_ID,
                PACIENTE_ID,
                PROFESIONAL_ID,
                null,
                null,
                LocalDateTime.now().minusDays(2),
                estado,
                HistoriaClinicaTipoAtencion.SEGUIMIENTO,
                "Motivo",
                "Resumen",
                null,
                null,
                null,
                null,
                HistoriaClinicaOrigenRegistro.MANUAL,
                USER_ID,
                USER_ID,
                estado == HistoriaClinicaSesionEstado.BORRADOR ? null : USER_ID,
                now,
                now,
                estado == HistoriaClinicaSesionEstado.BORRADOR ? null : now
        );
    }

    private DiagnosticoClinico sampleDiagnostico(DiagnosticoClinicoEstado estado) {
        Instant now = Instant.now();
        return new DiagnosticoClinico(
                DIAGNOSTICO_ID,
                CONSULTORIO_ID,
                PACIENTE_ID,
                PROFESIONAL_ID,
                null,
                "A01",
                "Diagnostico de prueba",
                estado,
                LocalDate.now().minusDays(5),
                null,
                "Notas",
                USER_ID,
                USER_ID,
                now,
                now
        );
    }
}
