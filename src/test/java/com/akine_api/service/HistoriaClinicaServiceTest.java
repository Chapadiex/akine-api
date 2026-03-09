package com.akine_api.service;

import com.akine_api.application.dto.command.CreateHistoriaClinicaLegajoCommand;
import com.akine_api.application.dto.command.CreateAtencionInicialCommand;
import com.akine_api.application.dto.command.CreateSesionClinicaCommand;
import com.akine_api.application.dto.command.HistoriaClinicaAntecedenteItemCommand;
import com.akine_api.application.dto.command.PlanTratamientoDetalleCommand;
import com.akine_api.application.dto.command.ResolveDiagnosticoClinicoCommand;
import com.akine_api.application.dto.command.UpdateHistoriaClinicaAntecedentesCommand;
import com.akine_api.application.dto.command.UpdateSesionClinicaCommand;
import com.akine_api.application.port.output.AdjuntoClinicoRepositoryPort;
import com.akine_api.application.port.output.AtencionInicialEvaluacionRepositoryPort;
import com.akine_api.application.port.output.AtencionInicialRepositoryPort;
import com.akine_api.application.port.output.AttachmentStoragePort;
import com.akine_api.application.port.output.BoxRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.DiagnosticoClinicoRepositoryPort;
import com.akine_api.application.port.output.HistoriaClinicaAntecedenteRepositoryPort;
import com.akine_api.application.port.output.HistoriaClinicaLegajoRepositoryPort;
import com.akine_api.application.port.output.PacienteConsultorioRepositoryPort;
import com.akine_api.application.port.output.PacienteRepositoryPort;
import com.akine_api.application.port.output.PlanTerapeuticoRepositoryPort;
import com.akine_api.application.port.output.PlanTratamientoDetalleRepositoryPort;
import com.akine_api.application.port.output.ProfesionalConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.SesionClinicaRepositoryPort;
import com.akine_api.application.port.output.TurnoRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.HistoriaClinicaService;
import com.akine_api.application.service.ConsultorioDiagnosticosMedicosService;
import com.akine_api.application.service.ConsultorioTratamientoCatalogService;
import com.akine_api.domain.exception.HistoriaClinicaConflictException;
import com.akine_api.domain.exception.HistoriaClinicaValidationException;
import com.akine_api.domain.exception.SesionClinicaNotFoundException;
import com.akine_api.domain.model.AdjuntoClinico;
import com.akine_api.domain.model.Box;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.DiagnosticoClinico;
import com.akine_api.domain.model.DiagnosticoClinicoEstado;
import com.akine_api.domain.model.HistoriaClinicaAntecedente;
import com.akine_api.domain.model.HistoriaClinicaLegajo;
import com.akine_api.domain.model.HistoriaClinicaOrigenRegistro;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.HistoriaClinicaTimelineEventType;
import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;
import com.akine_api.domain.model.AtencionInicialTipoIngreso;
import com.akine_api.domain.model.Paciente;
import com.akine_api.domain.model.PlanTratamientoCaracter;
import com.akine_api.domain.model.PlanTratamientoDetalle;
import com.akine_api.domain.model.Profesional;
import com.akine_api.domain.model.ProfesionalConsultorio;
import com.akine_api.domain.model.SesionClinica;
import com.akine_api.domain.model.Turno;
import com.akine_api.domain.model.TurnoEstado;
import com.akine_api.domain.model.TipoConsulta;
import com.akine_api.domain.model.User;
import com.akine_api.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HistoriaClinicaServiceTest {

    @Mock SesionClinicaRepositoryPort sesionRepo;
    @Mock DiagnosticoClinicoRepositoryPort diagnosticoRepo;
    @Mock AdjuntoClinicoRepositoryPort adjuntoRepo;
    @Mock AtencionInicialRepositoryPort atencionInicialRepo;
    @Mock AtencionInicialEvaluacionRepositoryPort atencionEvaluacionRepo;
    @Mock HistoriaClinicaLegajoRepositoryPort legajoRepo;
    @Mock HistoriaClinicaAntecedenteRepositoryPort antecedenteRepo;
    @Mock PlanTerapeuticoRepositoryPort planTerapeuticoRepo;
    @Mock PlanTratamientoDetalleRepositoryPort planDetalleRepo;
    @Mock AttachmentStoragePort attachmentStorage;
    @Mock PacienteRepositoryPort pacienteRepo;
    @Mock PacienteConsultorioRepositoryPort pacienteConsultorioRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    @Mock TurnoRepositoryPort turnoRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock BoxRepositoryPort boxRepo;
    @Mock ConsultorioDiagnosticosMedicosService diagnosticosMedicosService;
    @Mock ConsultorioTratamientoCatalogService tratamientoCatalogService;

    private HistoriaClinicaService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID OTHER_CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PACIENTE_ID = UUID.randomUUID();
    private static final UUID PROFESIONAL_ID = UUID.randomUUID();
    private static final UUID OTHER_PROFESIONAL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SESION_ID = UUID.randomUUID();
    private static final UUID DIAGNOSTICO_ID = UUID.randomUUID();
    private static final UUID TURNO_ID = UUID.randomUUID();
    private static final UUID BOX_ID = UUID.randomUUID();
    private static final UUID ADJUNTO_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new HistoriaClinicaService(
                sesionRepo,
                diagnosticoRepo,
                adjuntoRepo,
                atencionInicialRepo,
                atencionEvaluacionRepo,
                legajoRepo,
                antecedenteRepo,
                planTerapeuticoRepo,
                planDetalleRepo,
                attachmentStorage,
                pacienteRepo,
                pacienteConsultorioRepo,
                consultorioRepo,
                profesionalRepo,
                profesionalConsultorioRepo,
                turnoRepo,
                userRepo,
                boxRepo,
                diagnosticosMedicosService,
                tratamientoCatalogService
        );
        mockAccessBase();
        when(sesionRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(diagnosticoRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(legajoRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(antecedenteRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(atencionInicialRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(planTerapeuticoRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(planDetalleRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
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

    @Test
    void closeSesion_asProfessionalForAnotherSession_throws403() {
        when(sesionRepo.findById(SESION_ID))
                .thenReturn(Optional.of(sampleSesion(SESION_ID, CONSULTORIO_ID, PACIENTE_ID, OTHER_PROFESIONAL_ID, HistoriaClinicaSesionEstado.BORRADOR)));

        assertThatThrownBy(() -> service.closeSesion(
                new com.akine_api.application.dto.command.ChangeSesionClinicaEstadoCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        SESION_ID,
                        null
                ),
                "prof@test.com",
                Set.of("ROLE_PROFESIONAL")
        )).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getSesion_crossConsultorio_throwsNotFound() {
        when(sesionRepo.findById(SESION_ID))
                .thenReturn(Optional.of(sampleSesion(SESION_ID, OTHER_CONSULTORIO_ID, PACIENTE_ID, PROFESIONAL_ID, HistoriaClinicaSesionEstado.BORRADOR)));

        assertThatThrownBy(() -> service.getSesion(
                CONSULTORIO_ID,
                PACIENTE_ID,
                SESION_ID,
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        )).isInstanceOf(SesionClinicaNotFoundException.class);
    }

    @Test
    void createSesion_withReusedTurno_throwsConflict() {
        when(turnoRepo.findById(TURNO_ID))
                .thenReturn(Optional.of(sampleTurno(TURNO_ID, CONSULTORIO_ID, PACIENTE_ID, PROFESIONAL_ID, null)));
        when(sesionRepo.findByTurnoId(TURNO_ID))
                .thenReturn(Optional.of(sampleSesion(UUID.randomUUID(), CONSULTORIO_ID, PACIENTE_ID, PROFESIONAL_ID, HistoriaClinicaSesionEstado.BORRADOR)));

        assertThatThrownBy(() -> service.createSesion(
                new CreateSesionClinicaCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        PROFESIONAL_ID,
                        TURNO_ID,
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
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        )).isInstanceOf(HistoriaClinicaConflictException.class);
    }

    @Test
    void createSesion_withBoxOutsideConsultorio_throwsValidation() {
        when(boxRepo.findById(BOX_ID))
                .thenReturn(Optional.of(new Box(
                        BOX_ID,
                        OTHER_CONSULTORIO_ID,
                        "Box externo",
                        "B-2",
                        null,
                        null,
                        null,
                        true,
                        Instant.now()
                )));

        assertThatThrownBy(() -> service.createSesion(
                new CreateSesionClinicaCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        PROFESIONAL_ID,
                        null,
                        BOX_ID,
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
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        )).isInstanceOf(HistoriaClinicaValidationException.class);
    }

    @Test
    void addAdjunto_overLimit_throwsValidation() {
        when(sesionRepo.findById(SESION_ID)).thenReturn(Optional.of(sampleSesion(HistoriaClinicaSesionEstado.BORRADOR)));

        assertThatThrownBy(() -> service.addAdjunto(
                CONSULTORIO_ID,
                PACIENTE_ID,
                SESION_ID,
                new MockMultipartFile("file", "nota.pdf", "application/pdf", new byte[(10 * 1024 * 1024) + 1]),
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        )).isInstanceOf(HistoriaClinicaValidationException.class);
    }

    @Test
    void deleteAdjunto_whenSessionNotEditable_throwsConflict() {
        when(adjuntoRepo.findById(ADJUNTO_ID))
                .thenReturn(Optional.of(sampleAdjunto(CONSULTORIO_ID, PACIENTE_ID, SESION_ID)));
        when(sesionRepo.findById(SESION_ID)).thenReturn(Optional.of(sampleSesion(HistoriaClinicaSesionEstado.CERRADA)));

        assertThatThrownBy(() -> service.deleteAdjunto(
                CONSULTORIO_ID,
                PACIENTE_ID,
                ADJUNTO_ID,
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        )).isInstanceOf(HistoriaClinicaConflictException.class);
    }

    @Test
    void resolveDiagnostico_whenAlreadyResolved_throwsConflict() {
        when(diagnosticoRepo.findById(DIAGNOSTICO_ID))
                .thenReturn(Optional.of(sampleDiagnostico(DiagnosticoClinicoEstado.RESUELTO)));

        assertThatThrownBy(() -> service.resolveDiagnostico(
                new ResolveDiagnosticoClinicoCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        DIAGNOSTICO_ID,
                        LocalDate.now(),
                        null
                ),
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        )).isInstanceOf(HistoriaClinicaConflictException.class);
    }

    @Test
    void getOverview_withoutLegajoOrContext_returnsEmptyLegajoStatus() {
        when(sesionRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID)).thenReturn(List.of());
        when(diagnosticoRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID)).thenReturn(List.of());
        when(antecedenteRepo.findByConsultorioIdAndPacienteId(CONSULTORIO_ID, PACIENTE_ID)).thenReturn(List.of());
        when(legajoRepo.findByConsultorioIdAndPacienteId(CONSULTORIO_ID, PACIENTE_ID)).thenReturn(Optional.empty());

        var result = service.getOverview(
                CONSULTORIO_ID,
                PACIENTE_ID,
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        );

        assertThat(result.legajo().exists()).isFalse();
        assertThat(result.casosActivos()).isEmpty();
        assertThat(result.ultimaSesion()).isNull();
    }

    @Test
    void createAtencionInicial_persistsTreatmentSnapshotFromCatalog() {
        when(tratamientoCatalogService.requireActiveTreatment(CONSULTORIO_ID, "TMN001"))
                .thenReturn(new ConsultorioTratamientoCatalogService.TratamientoSnapshot(
                        "TMN001",
                        "Terapia manual",
                        "TERAPIA_MANUAL",
                        "Terapia manual",
                        "TECNICA",
                        true,
                        false,
                        20
                ));

        service.createAtencionInicial(
                new CreateAtencionInicialCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        PROFESIONAL_ID,
                        LocalDateTime.now(),
                        AtencionInicialTipoIngreso.CONSULTA_PARTICULAR,
                        "Dolor lumbar",
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
                        null,
                        null,
                        List.of(),
                        "Plan inicial",
                        List.of(new PlanTratamientoDetalleCommand(
                                "TMN001",
                                10,
                                "2 veces por semana",
                                PlanTratamientoCaracter.PARCIAL,
                                LocalDate.now().plusDays(1),
                                true,
                                "Observacion",
                                "Administrativa"
                        )),
                        USER_ID
                ),
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        );

        ArgumentCaptor<List<PlanTratamientoDetalle>> captor = ArgumentCaptor.forClass(List.class);
        verify(planDetalleRepo).saveAll(captor.capture());
        PlanTratamientoDetalle detalle = captor.getValue().get(0);

        assertThat(detalle.getTratamientoNombreSnapshot()).isEqualTo("Terapia manual");
        assertThat(detalle.getTratamientoCategoriaCodigoSnapshot()).isEqualTo("TERAPIA_MANUAL");
        assertThat(detalle.getTratamientoCategoriaNombreSnapshot()).isEqualTo("Terapia manual");
        assertThat(detalle.getTratamientoTipoSnapshot()).isEqualTo("TECNICA");
        assertThat(detalle.isTratamientoRequiereAutorizacionSnapshot()).isTrue();
        assertThat(detalle.isTratamientoRequierePrescripcionMedicaSnapshot()).isFalse();
        assertThat(detalle.getTratamientoDuracionSugeridaMinutosSnapshot()).isEqualTo(20);
    }

    @Test
    void createAtencionInicial_whenTreatmentIsInactive_throws() {
        when(tratamientoCatalogService.requireActiveTreatment(CONSULTORIO_ID, "TMN001"))
                .thenThrow(new IllegalArgumentException("El tratamiento seleccionado esta inactivo"));

        assertThatThrownBy(() -> service.createAtencionInicial(
                new CreateAtencionInicialCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        PROFESIONAL_ID,
                        LocalDateTime.now(),
                        AtencionInicialTipoIngreso.CONSULTA_PARTICULAR,
                        "Dolor lumbar",
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
                        null,
                        null,
                        List.of(),
                        "Plan inicial",
                        List.of(new PlanTratamientoDetalleCommand(
                                "TMN001",
                                10,
                                null,
                                PlanTratamientoCaracter.PARCIAL,
                                null,
                                false,
                                null,
                                null
                        )),
                        USER_ID
                ),
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inactivo");
    }

    @Test
    void createLegajo_persistsLegajoAntecedentesAndInitialContext() {
        HistoriaClinicaLegajo legajo = sampleLegajo();
        when(legajoRepo.findByConsultorioIdAndPacienteId(CONSULTORIO_ID, PACIENTE_ID))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(legajo));
        when(antecedenteRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(sesionRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(List.of(sampleSesion(HistoriaClinicaSesionEstado.BORRADOR)));
        when(diagnosticoRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(List.of(sampleDiagnostico(DiagnosticoClinicoEstado.ACTIVO)));
        when(antecedenteRepo.findByConsultorioIdAndPacienteId(CONSULTORIO_ID, PACIENTE_ID))
                .thenReturn(List.of(sampleAntecedente(legajo.getId())));

        var result = service.createLegajo(
                new CreateHistoriaClinicaLegajoCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        PROFESIONAL_ID,
                        LocalDateTime.now(),
                        "Motivo",
                        "Resumen",
                        null,
                        null,
                        "Evaluacion",
                        null,
                        null,
                        "Lumbalgia mecanica",
                        LocalDate.now().minusDays(2),
                        "Caso activo",
                        List.of(new HistoriaClinicaAntecedenteItemCommand(
                                "medication",
                                "diclofenac",
                                "Alergia",
                                "Diclofenac",
                                true,
                                "Evitar"
                        )),
                        null
                ),
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        );

        assertThat(result.legajo().exists()).isTrue();
        assertThat(result.alertasClinicas()).contains("Alergia: Diclofenac");
        assertThat(result.casosActivos()).hasSize(1);
    }

    @Test
    void updateAntecedentes_withoutLegajo_createsOneAndReturnsSavedItems() {
        HistoriaClinicaLegajo legajo = sampleLegajo();
        when(legajoRepo.findByConsultorioIdAndPacienteId(CONSULTORIO_ID, PACIENTE_ID))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(legajo));
        when(antecedenteRepo.saveAll(anyList())).thenReturn(List.of(sampleAntecedente(legajo.getId())));
        when(antecedenteRepo.findByConsultorioIdAndPacienteId(CONSULTORIO_ID, PACIENTE_ID))
                .thenReturn(List.of(sampleAntecedente(legajo.getId())));

        var result = service.updateAntecedentes(
                new UpdateHistoriaClinicaAntecedentesCommand(
                        CONSULTORIO_ID,
                        PACIENTE_ID,
                        List.of(new HistoriaClinicaAntecedenteItemCommand(
                                "medication",
                                "diclofenac",
                                "Alergia",
                                "Diclofenac",
                                true,
                                "Evitar"
                        )),
                        null
                ),
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        );

        assertThat(result).singleElement().satisfies(item -> {
            assertThat(item.label()).isEqualTo("Alergia");
            assertThat(item.critical()).isTrue();
        });
    }

    @Test
    void getTimeline_filtersByRequestedType() {
        HistoriaClinicaLegajo legajo = sampleLegajo();
        SesionClinica sesion = sampleSesion(HistoriaClinicaSesionEstado.BORRADOR);
        when(legajoRepo.findByConsultorioIdAndPacienteId(CONSULTORIO_ID, PACIENTE_ID)).thenReturn(Optional.of(legajo));
        when(sesionRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID)).thenReturn(List.of(sesion));
        when(diagnosticoRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID))
                .thenReturn(List.of(sampleDiagnostico(DiagnosticoClinicoEstado.ACTIVO)));
        when(antecedenteRepo.findByConsultorioIdAndPacienteId(CONSULTORIO_ID, PACIENTE_ID))
                .thenReturn(List.of(sampleAntecedente(legajo.getId())));
        when(adjuntoRepo.findBySesionIds(List.of(sesion.getId()))).thenReturn(List.of(sampleAdjunto(CONSULTORIO_ID, PACIENTE_ID, sesion.getId())));

        var result = service.getTimeline(
                CONSULTORIO_ID,
                PACIENTE_ID,
                "attachments",
                "admin@test.com",
                Set.of("ROLE_ADMIN")
        );

        assertThat(result).singleElement().satisfies(event -> {
            assertThat(event.type()).isEqualTo(HistoriaClinicaTimelineEventType.ADJUNTO);
            assertThat(event.summary()).isEqualTo("nota.pdf");
        });
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
        when(legajoRepo.findByConsultorioIdAndPacienteId(CONSULTORIO_ID, PACIENTE_ID)).thenReturn(Optional.empty());
        when(antecedenteRepo.findByConsultorioIdAndPacienteId(CONSULTORIO_ID, PACIENTE_ID)).thenReturn(List.of());
        when(diagnosticoRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID)).thenReturn(List.of());
        when(sesionRepo.findByPacienteIdAndConsultorioId(PACIENTE_ID, CONSULTORIO_ID)).thenReturn(List.of());
        when(adjuntoRepo.findBySesionId(SESION_ID)).thenReturn(List.of());
        when(adjuntoRepo.findBySesionIds(anyList())).thenReturn(List.of());
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
        return sampleSesion(SESION_ID, CONSULTORIO_ID, PACIENTE_ID, PROFESIONAL_ID, estado);
    }

    private SesionClinica sampleSesion(UUID sesionId,
                                       UUID consultorioId,
                                       UUID pacienteId,
                                       UUID profesionalId,
                                       HistoriaClinicaSesionEstado estado) {
        Instant now = Instant.now();
        return new SesionClinica(
                sesionId,
                consultorioId,
                pacienteId,
                profesionalId,
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

    private Turno sampleTurno(UUID turnoId,
                              UUID consultorioId,
                              UUID pacienteId,
                              UUID profesionalId,
                              UUID boxId) {
        return new Turno(
                turnoId,
                consultorioId,
                profesionalId,
                boxId,
                pacienteId,
                LocalDateTime.now().plusDays(1),
                60,
                TurnoEstado.PROGRAMADO,
                "Motivo",
                "Notas",
                TipoConsulta.PARTICULAR,
                null,
                USER_ID,
                null,
                null,
                Instant.now()
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
                "DIAGNOSTICO_MEDICO",
                "OSTEO",
                "Osteomuscular",
                "Columna lumbar",
                "Lumbar",
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

    private HistoriaClinicaLegajo sampleLegajo() {
        Instant now = Instant.now();
        return new HistoriaClinicaLegajo(
                UUID.randomUUID(),
                CONSULTORIO_ID,
                PACIENTE_ID,
                USER_ID,
                USER_ID,
                now,
                now
        );
    }

    private HistoriaClinicaAntecedente sampleAntecedente(UUID legajoId) {
        Instant now = Instant.now();
        return new HistoriaClinicaAntecedente(
                UUID.randomUUID(),
                legajoId,
                CONSULTORIO_ID,
                PACIENTE_ID,
                "medication",
                "diclofenac",
                "Alergia",
                "Diclofenac",
                true,
                "Evitar",
                USER_ID,
                USER_ID,
                now,
                now
        );
    }

    private AdjuntoClinico sampleAdjunto(UUID consultorioId, UUID pacienteId, UUID sesionId) {
        return new AdjuntoClinico(
                ADJUNTO_ID,
                consultorioId,
                pacienteId,
                sesionId,
                null,
                "storage-key",
                "nota.pdf",
                "application/pdf",
                4L,
                USER_ID,
                Instant.now()
        );
    }
}
