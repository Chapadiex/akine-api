package com.akine_api.service;

import com.akine_api.application.dto.command.CambiarEstadoTurnoCommand;
import com.akine_api.application.dto.command.CreateTurnoCommand;
import com.akine_api.application.dto.command.ReprogramarTurnoCommand;
import com.akine_api.application.dto.result.SlotDisponibleResult;
import com.akine_api.application.dto.result.TurnoResult;
import com.akine_api.application.port.output.*;
import com.akine_api.application.service.TurnoService;
import com.akine_api.domain.exception.*;
import com.akine_api.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    @Mock TurnoRepositoryPort turnoRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    @Mock BoxRepositoryPort boxRepo;
    @Mock ConsultorioDuracionTurnoRepositoryPort duracionRepo;
    @Mock ConsultorioHorarioRepositoryPort horarioRepo;
    @Mock DisponibilidadProfesionalRepositoryPort disponibilidadRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock HistorialEstadoTurnoRepositoryPort historialRepo;
    @Mock ConsultorioFeriadoRepositoryPort feriadoRepo;
    @Mock PacienteRepositoryPort pacienteRepo;

    TurnoService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PROFESIONAL_ID = UUID.randomUUID();
    private static final UUID BOX_ID = UUID.randomUUID();
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");

    // Next Monday at 10:00
    private static final LocalDateTime NEXT_MONDAY_10 =
            LocalDate.now().with(java.time.temporal.TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(10, 0);

    @BeforeEach
    void setUp() {
        service = new TurnoService(
                turnoRepo, consultorioRepo, profesionalRepo, profesionalConsultorioRepo,
                boxRepo, duracionRepo, horarioRepo, disponibilidadRepo, userRepo,
                historialRepo, feriadoRepo, pacienteRepo);
    }

    private void stubConsultorioExists() {
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
    }

    private void stubProfesionalExists() {
        when(profesionalRepo.findById(PROFESIONAL_ID))
                .thenReturn(Optional.of(new Profesional(PROFESIONAL_ID, CONSULTORIO_ID, "Juan", "Perez", "M123", null, "jp@test.com", null, true, Instant.now())));
    }

    private void stubProfesionalAssigned() {
        when(profesionalConsultorioRepo.existsByProfesionalIdAndConsultorioId(PROFESIONAL_ID, CONSULTORIO_ID))
                .thenReturn(true);
    }

    private void stubDuracionAllowed(int minutos) {
        when(duracionRepo.existsByConsultorioIdAndMinutos(CONSULTORIO_ID, minutos)).thenReturn(true);
    }

    private void stubHorarioMonday() {
        when(horarioRepo.findByConsultorioIdAndDiaSemana(CONSULTORIO_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(new ConsultorioHorario(UUID.randomUUID(), CONSULTORIO_ID, DayOfWeek.MONDAY,
                        LocalTime.of(8, 0), LocalTime.of(20, 0), true)));
    }

    private void stubDisponibilidadMonday() {
        when(disponibilidadRepo.findByProfesionalIdAndConsultorioIdAndDiaSemana(PROFESIONAL_ID, CONSULTORIO_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(new DisponibilidadProfesional(UUID.randomUUID(), PROFESIONAL_ID, CONSULTORIO_ID,
                        DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0), true)));
    }

    private void stubNoConflicts() {
        when(turnoRepo.findByProfesionalIdAndRange(any(), any(), any())).thenReturn(List.of());
    }

    private void stubNoFeriado() {
        when(feriadoRepo.existsByConsultorioIdAndFecha(any(), any())).thenReturn(false);
    }

    private void stubHistorialSave() {
        when(historialRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private void stubAdminUser() {
        UUID adminUserId = UUID.randomUUID();
        when(userRepo.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(new User(adminUserId, "admin@test.com", "hashed", "Admin", "User", null, UserStatus.ACTIVE, Instant.now())));
    }

    private CreateTurnoCommand validCreateCommand() {
        return new CreateTurnoCommand(
                CONSULTORIO_ID, PROFESIONAL_ID, null, null,
                NEXT_MONDAY_10, 30, "Consulta", null,
                null, null, null);
    }

    // ── create ──────────────────────────────────────────────────────

    @Test
    void create_validSlot_returnsTurno() {
        stubConsultorioExists();
        stubProfesionalExists();
        stubProfesionalAssigned();
        stubDuracionAllowed(30);
        stubHorarioMonday();
        stubDisponibilidadMonday();
        stubNoConflicts();
        stubNoFeriado();
        stubHistorialSave();
        stubAdminUser();
        when(turnoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TurnoResult result = service.create(validCreateCommand(), "admin@test.com", ADMIN_ROLES);

        assertThat(result).isNotNull();
        assertThat(result.estado()).isEqualTo(TurnoEstado.PROGRAMADO);
        assertThat(result.duracionMinutos()).isEqualTo(30);
        assertThat(result.fechaHoraInicio()).isEqualTo(NEXT_MONDAY_10);
    }

    @Test
    void create_profesionalNotAssigned_throws() {
        stubConsultorioExists();
        stubProfesionalExists();
        when(profesionalConsultorioRepo.existsByProfesionalIdAndConsultorioId(PROFESIONAL_ID, CONSULTORIO_ID))
                .thenReturn(false);

        assertThatThrownBy(() -> service.create(validCreateCommand(), "admin@test.com", ADMIN_ROLES))
                .isInstanceOf(ProfesionalConsultorioNotFoundException.class);
    }

    @Test
    void create_duracionNotAllowed_throws() {
        stubConsultorioExists();
        stubProfesionalExists();
        stubProfesionalAssigned();
        when(duracionRepo.existsByConsultorioIdAndMinutos(CONSULTORIO_ID, 30)).thenReturn(false);

        assertThatThrownBy(() -> service.create(validCreateCommand(), "admin@test.com", ADMIN_ROLES))
                .isInstanceOf(SlotNoDisponibleException.class)
                .hasMessageContaining("duracion");
    }

    @Test
    void create_outsideConsultorioHorario_throws() {
        stubConsultorioExists();
        stubProfesionalExists();
        stubProfesionalAssigned();
        stubDuracionAllowed(30);
        stubNoFeriado();
        // Consultorio opens 8-12 but turno is at 10:00-10:30 on Monday... let's make it closed that day
        when(horarioRepo.findByConsultorioIdAndDiaSemana(CONSULTORIO_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.create(validCreateCommand(), "admin@test.com", ADMIN_ROLES))
                .isInstanceOf(SlotNoDisponibleException.class)
                .hasMessageContaining("horario");
    }

    @Test
    void create_outsideProfesionalDisponibilidad_throws() {
        stubConsultorioExists();
        stubProfesionalExists();
        stubProfesionalAssigned();
        stubDuracionAllowed(30);
        stubNoFeriado();
        stubHorarioMonday();
        // Profesional only available 14-18, turno at 10
        when(disponibilidadRepo.findByProfesionalIdAndConsultorioIdAndDiaSemana(PROFESIONAL_ID, CONSULTORIO_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(new DisponibilidadProfesional(UUID.randomUUID(), PROFESIONAL_ID, CONSULTORIO_ID,
                        DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(18, 0), true)));

        assertThatThrownBy(() -> service.create(validCreateCommand(), "admin@test.com", ADMIN_ROLES))
                .isInstanceOf(SlotNoDisponibleException.class)
                .hasMessageContaining("disponibilidad");
    }

    @Test
    void create_conflictWithExistingTurno_throws() {
        stubConsultorioExists();
        stubProfesionalExists();
        stubProfesionalAssigned();
        stubDuracionAllowed(30);
        stubNoFeriado();
        stubHorarioMonday();
        stubDisponibilidadMonday();

        // Existing turno at 09:45-10:15 overlaps with 10:00-10:30
        Turno existing = new Turno(UUID.randomUUID(), CONSULTORIO_ID, PROFESIONAL_ID, null, null,
                NEXT_MONDAY_10.minusMinutes(15), 30, TurnoEstado.PROGRAMADO, null, null, Instant.now());
        when(turnoRepo.findByProfesionalIdAndRange(any(), any(), any())).thenReturn(List.of(existing));

        assertThatThrownBy(() -> service.create(validCreateCommand(), "admin@test.com", ADMIN_ROLES))
                .isInstanceOf(TurnoConflictException.class)
                .hasMessageContaining("profesional");
    }

    @Test
    void create_accessDenied_throws() {
        stubConsultorioExists();

        // PROFESIONAL from different consultorio
        UUID userId = UUID.randomUUID();
        when(userRepo.findByEmail("prof@test.com"))
                .thenReturn(Optional.of(createUser(userId)));
        when(consultorioRepo.findConsultorioIdsByUserId(userId))
                .thenReturn(List.of(UUID.randomUUID())); // different consultorio

        assertThatThrownBy(() -> service.create(validCreateCommand(), "prof@test.com", Set.of("ROLE_PROFESIONAL")))
                .isInstanceOf(AccessDeniedException.class);
    }

    // ── cambiarEstado ───────────────────────────────────────────────

    @Test
    void cambiarEstado_validTransition_succeeds() {
        Turno turno = new Turno(UUID.randomUUID(), CONSULTORIO_ID, PROFESIONAL_ID, null, null,
                NEXT_MONDAY_10, 30, TurnoEstado.PROGRAMADO, null, null, Instant.now());
        when(turnoRepo.findById(turno.getId())).thenReturn(Optional.of(turno));
        when(turnoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        stubHistorialSave();
        stubAdminUser();

        TurnoResult result = service.cambiarEstado(turno.getId(),
                new CambiarEstadoTurnoCommand(turno.getId(), TurnoEstado.CONFIRMADO, null, null),
                "admin@test.com", ADMIN_ROLES);

        assertThat(result.estado()).isEqualTo(TurnoEstado.CONFIRMADO);
    }

    @Test
    void cambiarEstado_invalidTransition_throws() {
        Turno turno = new Turno(UUID.randomUUID(), CONSULTORIO_ID, PROFESIONAL_ID, null, null,
                NEXT_MONDAY_10, 30, TurnoEstado.COMPLETADO, null, null, Instant.now());
        when(turnoRepo.findById(turno.getId())).thenReturn(Optional.of(turno));

        assertThatThrownBy(() -> service.cambiarEstado(turno.getId(),
                new CambiarEstadoTurnoCommand(turno.getId(), TurnoEstado.PROGRAMADO, null, null),
                "admin@test.com", ADMIN_ROLES))
                .isInstanceOf(TransicionEstadoInvalidaException.class);
    }

    // ── reprogramar ─────────────────────────────────────────────────

    @Test
    void reprogramar_validSlot_updatesTime() {
        LocalDateTime newTime = NEXT_MONDAY_10.plusHours(2);
        Turno turno = new Turno(UUID.randomUUID(), CONSULTORIO_ID, PROFESIONAL_ID, null, null,
                NEXT_MONDAY_10, 30, TurnoEstado.PROGRAMADO, null, null, Instant.now());
        when(turnoRepo.findById(turno.getId())).thenReturn(Optional.of(turno));
        stubNoFeriado();
        stubHorarioMonday();
        stubDisponibilidadMonday();
        stubNoConflicts();
        stubHistorialSave();
        stubAdminUser();
        when(turnoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TurnoResult result = service.reprogramar(turno.getId(),
                new ReprogramarTurnoCommand(turno.getId(), newTime),
                "admin@test.com", ADMIN_ROLES);

        assertThat(result.fechaHoraInicio()).isEqualTo(newTime);
    }

    // ── getDisponibilidad ───────────────────────────────────────────

    @Test
    void getDisponibilidad_returnsAvailableSlots() {
        stubConsultorioExists();
        stubNoFeriado();
        LocalDate monday = NEXT_MONDAY_10.toLocalDate();

        when(horarioRepo.findByConsultorioIdAndDiaSemana(CONSULTORIO_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(new ConsultorioHorario(UUID.randomUUID(), CONSULTORIO_ID, DayOfWeek.MONDAY,
                        LocalTime.of(9, 0), LocalTime.of(12, 0), true)));
        when(disponibilidadRepo.findByProfesionalIdAndConsultorioIdAndDiaSemana(PROFESIONAL_ID, CONSULTORIO_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of(new DisponibilidadProfesional(UUID.randomUUID(), PROFESIONAL_ID, CONSULTORIO_ID,
                        DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0), true)));
        when(turnoRepo.findByProfesionalIdAndRange(any(), any(), any())).thenReturn(List.of());

        List<SlotDisponibleResult> slots = service.getDisponibilidad(
                CONSULTORIO_ID, PROFESIONAL_ID, monday, 30,
                "admin@test.com", ADMIN_ROLES);

        // 9:00-12:00 with 30 min slots = 6 slots
        assertThat(slots).hasSize(6);
        assertThat(slots.get(0).inicio()).isEqualTo(monday.atTime(9, 0));
        assertThat(slots.get(5).fin()).isEqualTo(monday.atTime(12, 0));
    }

    @Test
    void getDisponibilidad_noHorarioForDay_returnsEmpty() {
        stubConsultorioExists();
        stubNoFeriado();
        LocalDate monday = NEXT_MONDAY_10.toLocalDate();

        when(horarioRepo.findByConsultorioIdAndDiaSemana(CONSULTORIO_ID, DayOfWeek.MONDAY))
                .thenReturn(List.of());

        List<SlotDisponibleResult> slots = service.getDisponibilidad(
                CONSULTORIO_ID, PROFESIONAL_ID, monday, 30,
                "admin@test.com", ADMIN_ROLES);

        assertThat(slots).isEmpty();
    }

    // ── listByRange ─────────────────────────────────────────────────

    @Test
    void listByRange_filtersResults() {
        stubConsultorioExists();
        UUID otherProfId = UUID.randomUUID();
        Turno t1 = new Turno(UUID.randomUUID(), CONSULTORIO_ID, PROFESIONAL_ID, null, null,
                NEXT_MONDAY_10, 30, TurnoEstado.PROGRAMADO, null, null, Instant.now());
        Turno t2 = new Turno(UUID.randomUUID(), CONSULTORIO_ID, otherProfId, null, null,
                NEXT_MONDAY_10.plusHours(1), 30, TurnoEstado.PROGRAMADO, null, null, Instant.now());

        when(turnoRepo.findByConsultorioIdAndRange(any(), any(), any())).thenReturn(List.of(t1, t2));
        when(profesionalRepo.findByConsultorioId(CONSULTORIO_ID)).thenReturn(List.of(
                new Profesional(PROFESIONAL_ID, CONSULTORIO_ID, "Juan", "Perez", "M1", null, null, null, true, Instant.now()),
                new Profesional(otherProfId, CONSULTORIO_ID, "Ana", "Lopez", "M2", null, null, null, true, Instant.now())));
        when(boxRepo.findByConsultorioId(CONSULTORIO_ID)).thenReturn(List.of());

        LocalDateTime from = NEXT_MONDAY_10.toLocalDate().atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        // Filter by profesionalId
        List<TurnoResult> results = service.listByRange(
                CONSULTORIO_ID, from, to, PROFESIONAL_ID, null, null,
                "admin@test.com", ADMIN_ROLES);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).profesionalId()).isEqualTo(PROFESIONAL_ID);
    }

    // ── create: feriado ─────────────────────────────────────────────

    @Test
    void create_onFeriado_throws() {
        stubConsultorioExists();
        stubProfesionalExists();
        stubProfesionalAssigned();
        stubDuracionAllowed(30);
        when(feriadoRepo.existsByConsultorioIdAndFecha(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.create(validCreateCommand(), "admin@test.com", ADMIN_ROLES))
                .isInstanceOf(FeriadoException.class)
                .hasMessageContaining("feriado");
    }

    // ── create: paciente overlap ─────────────────────────────────────

    @Test
    void create_pacienteOverlap_throws() {
        UUID pacienteId = UUID.randomUUID();
        CreateTurnoCommand cmd = new CreateTurnoCommand(
                CONSULTORIO_ID, PROFESIONAL_ID, null, pacienteId,
                NEXT_MONDAY_10, 30, "Consulta", null, null, null, null);

        stubConsultorioExists();
        stubProfesionalExists();
        stubProfesionalAssigned();
        stubDuracionAllowed(30);
        stubNoFeriado();
        stubHorarioMonday();
        stubDisponibilidadMonday();
        stubNoConflicts();

        // Paciente ya tiene turno solapado
        Turno existing = new Turno(UUID.randomUUID(), CONSULTORIO_ID, UUID.randomUUID(), null, pacienteId,
                NEXT_MONDAY_10.minusMinutes(15), 30, TurnoEstado.PROGRAMADO, null, null, Instant.now());
        when(turnoRepo.findByPacienteIdAndRange(any(), any(), any())).thenReturn(List.of(existing));

        assertThatThrownBy(() -> service.create(cmd, "admin@test.com", ADMIN_ROLES))
                .isInstanceOf(TurnoPacienteSolapadoException.class);
    }

    // ── create: tipoConsulta y metadata ──────────────────────────────

    @Test
    void create_setsMetadata() {
        stubConsultorioExists();
        stubProfesionalExists();
        stubProfesionalAssigned();
        stubDuracionAllowed(30);
        stubHorarioMonday();
        stubDisponibilidadMonday();
        stubNoConflicts();
        stubNoFeriado();
        stubHistorialSave();
        stubAdminUser();
        when(turnoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateTurnoCommand cmd = new CreateTurnoCommand(
                CONSULTORIO_ID, PROFESIONAL_ID, null, null,
                NEXT_MONDAY_10, 30, "Consulta", null,
                TipoConsulta.OBRA_SOCIAL, "1155667788", null);

        TurnoResult result = service.create(cmd, "admin@test.com", ADMIN_ROLES);

        assertThat(result.tipoConsulta()).isEqualTo(TipoConsulta.OBRA_SOCIAL);
        assertThat(result.telefonoContacto()).isEqualTo("1155667788");
        assertThat(result.creadoPorUserId()).isNotNull();
    }

    // ── cambiarEstado: EN_ESPERA ─────────────────────────────────────

    @Test
    void cambiarEstado_CONFIRMADO_to_EN_ESPERA_works() {
        Turno turno = new Turno(UUID.randomUUID(), CONSULTORIO_ID, PROFESIONAL_ID, null, null,
                NEXT_MONDAY_10, 30, TurnoEstado.CONFIRMADO, null, null, Instant.now());
        when(turnoRepo.findById(turno.getId())).thenReturn(Optional.of(turno));
        when(turnoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        stubHistorialSave();
        stubAdminUser();

        TurnoResult result = service.cambiarEstado(turno.getId(),
                new CambiarEstadoTurnoCommand(turno.getId(), TurnoEstado.EN_ESPERA, null, null),
                "admin@test.com", ADMIN_ROLES);

        assertThat(result.estado()).isEqualTo(TurnoEstado.EN_ESPERA);
    }

    @Test
    void cambiarEstado_EN_ESPERA_to_EN_CURSO_works() {
        Turno turno = new Turno(UUID.randomUUID(), CONSULTORIO_ID, PROFESIONAL_ID, null, null,
                NEXT_MONDAY_10, 30, TurnoEstado.CONFIRMADO, null, null, Instant.now());
        turno.cambiarEstado(TurnoEstado.EN_ESPERA); // transition to EN_ESPERA first
        when(turnoRepo.findById(turno.getId())).thenReturn(Optional.of(turno));
        when(turnoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        stubHistorialSave();
        stubAdminUser();

        TurnoResult result = service.cambiarEstado(turno.getId(),
                new CambiarEstadoTurnoCommand(turno.getId(), TurnoEstado.EN_CURSO, null, null),
                "admin@test.com", ADMIN_ROLES);

        assertThat(result.estado()).isEqualTo(TurnoEstado.EN_CURSO);
    }

    // ── cambiarEstado: cancelar con motivo ───────────────────────────

    @Test
    void cambiarEstado_toCancelado_setsMotivoCancelacion() {
        Turno turno = new Turno(UUID.randomUUID(), CONSULTORIO_ID, PROFESIONAL_ID, null, null,
                NEXT_MONDAY_10, 30, TurnoEstado.PROGRAMADO, null, null, Instant.now());
        when(turnoRepo.findById(turno.getId())).thenReturn(Optional.of(turno));
        when(turnoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        stubHistorialSave();
        stubAdminUser();

        TurnoResult result = service.cambiarEstado(turno.getId(),
                new CambiarEstadoTurnoCommand(turno.getId(), TurnoEstado.CANCELADO, "Paciente no puede asistir", null),
                "admin@test.com", ADMIN_ROLES);

        assertThat(result.estado()).isEqualTo(TurnoEstado.CANCELADO);
        assertThat(result.motivoCancelacion()).isEqualTo("Paciente no puede asistir");
    }

    // ── getDisponibilidad: feriado ───────────────────────────────────

    @Test
    void getDisponibilidad_onFeriado_returnsEmpty() {
        stubConsultorioExists();
        when(feriadoRepo.existsByConsultorioIdAndFecha(any(), any())).thenReturn(true);

        LocalDate monday = NEXT_MONDAY_10.toLocalDate();
        List<SlotDisponibleResult> slots = service.getDisponibilidad(
                CONSULTORIO_ID, PROFESIONAL_ID, monday, 30,
                "admin@test.com", ADMIN_ROLES);

        assertThat(slots).isEmpty();
    }

    // ── Helpers ─────────────────────────────────────────────────────

    private User createUser(UUID id) {
        return new User(id, "prof@test.com", "hashed", "Test", "User", null, UserStatus.ACTIVE, Instant.now());
    }
}
