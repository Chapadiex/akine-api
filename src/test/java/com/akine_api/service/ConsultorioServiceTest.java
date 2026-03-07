package com.akine_api.service;

import com.akine_api.application.dto.command.CreateConsultorioCommand;
import com.akine_api.application.dto.command.UpdateConsultorioCommand;
import com.akine_api.application.dto.result.ConsultorioResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.CargoEmpleadoCatalogoBootstrapService;
import com.akine_api.application.service.ConsultorioAntecedenteBootstrapService;
import com.akine_api.application.service.ConsultorioEspecialidadBootstrapService;
import com.akine_api.application.service.ConsultorioService;
import com.akine_api.domain.exception.ConsultorioInactiveException;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultorioServiceTest {

    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock ConsultorioEspecialidadBootstrapService especialidadBootstrapService;
    @Mock ConsultorioAntecedenteBootstrapService antecedenteBootstrapService;
    @Mock CargoEmpleadoCatalogoBootstrapService cargoBootstrapService;

    ConsultorioService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");
    private static final Set<String> PROF_ADMIN_ROLES = Set.of("ROLE_PROFESIONAL_ADMIN");
    private static final Set<String> PROF_ROLES = Set.of("ROLE_PROFESIONAL");

    @BeforeEach
    void setUp() {
        service = new ConsultorioService(
                consultorioRepo,
                userRepo,
                especialidadBootstrapService,
                antecedenteBootstrapService,
                cargoBootstrapService
        );
    }

    private Consultorio activeConsultorio() {
        return new Consultorio(CONSULTORIO_ID, "Consultorio Test", "20123456789",
                "Av. Siempreviva 123", "1155551234", "test@mail.com", "ACTIVE", Instant.now());
    }

    private Consultorio inactiveConsultorio() {
        return new Consultorio(CONSULTORIO_ID, "Consultorio Test", "20123456789",
                "Av. Siempreviva 123", "1155551234", "test@mail.com", "INACTIVE", Instant.now());
    }

    private User activeUser() {
        return new User(USER_ID, ADMIN_EMAIL, "hash", "Admin", "User",
                null, UserStatus.ACTIVE, Instant.now());
    }

    // ─── list ─────────────────────────────────────────────────────────────────

    @Test
    void list_asAdmin_returnsAll() {
        when(consultorioRepo.findAll()).thenReturn(List.of(activeConsultorio()));

        List<ConsultorioResult> result = service.list(ADMIN_EMAIL, ADMIN_ROLES);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Consultorio Test");
        verify(consultorioRepo).findAll();
        verify(consultorioRepo, never()).findConsultorioIdsByUserId(any());
    }

    @Test
    void list_asProfAdmin_returnsOwn() {
        when(userRepo.findByEmail(ADMIN_EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));
        when(consultorioRepo.findByIds(List.of(CONSULTORIO_ID))).thenReturn(List.of(activeConsultorio()));

        List<ConsultorioResult> result = service.list(ADMIN_EMAIL, PROF_ADMIN_ROLES);

        assertThat(result).hasSize(1);
    }

    @Test
    void list_asProfAdmin_excludesInactive() {
        when(userRepo.findByEmail(ADMIN_EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));
        when(consultorioRepo.findByIds(List.of(CONSULTORIO_ID))).thenReturn(List.of(inactiveConsultorio()));

        List<ConsultorioResult> result = service.list(ADMIN_EMAIL, PROF_ADMIN_ROLES);

        assertThat(result).isEmpty();
    }

    // ─── getById ──────────────────────────────────────────────────────────────

    @Test
    void getById_asAdmin_returnsConsultorio() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));

        ConsultorioResult result = service.getById(CONSULTORIO_ID, ADMIN_EMAIL, ADMIN_ROLES);

        assertThat(result.id()).isEqualTo(CONSULTORIO_ID);
    }

    @Test
    void getById_notFound_throwsConsultorioNotFoundException() {
        when(consultorioRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(UUID.randomUUID(), ADMIN_EMAIL, ADMIN_ROLES))
                .isInstanceOf(ConsultorioNotFoundException.class);
    }

    @Test
    void getById_noMembership_throwsAccessDenied() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));
        when(userRepo.findByEmail(ADMIN_EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> service.getById(CONSULTORIO_ID, ADMIN_EMAIL, PROF_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void create_asAdmin_succeeds() {
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateConsultorioCommand cmd = new CreateConsultorioCommand(
                "Nuevo", null, "Av. 123", "1155550000", "nuevo@mail.com", null, null, null);
        ConsultorioResult result = service.create(cmd, ADMIN_ROLES);

        assertThat(result.name()).isEqualTo("Nuevo");
        assertThat(result.status()).isEqualTo("ACTIVE");
        verify(cargoBootstrapService).ensureDefaults();
        verify(especialidadBootstrapService).enableDefaultsForConsultorio(result.id());
        verify(antecedenteBootstrapService).ensureDefaults(result.id(), "system");
    }

    @Test
    void create_withMapData_persistsCoordinatesAndUrl() {
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateConsultorioCommand cmd = new CreateConsultorioCommand(
                "Nuevo", null, "Av. 123", "1155550000", "nuevo@mail.com",
                new BigDecimal("-34.603722"), new BigDecimal("-58.381592"),
                "https://maps.google.com/?q=-34.603722,-58.381592");
        ConsultorioResult result = service.create(cmd, ADMIN_ROLES);

        assertThat(result.mapLatitude()).isEqualByComparingTo("-34.603722");
        assertThat(result.mapLongitude()).isEqualByComparingTo("-58.381592");
        assertThat(result.googleMapsUrl()).isEqualTo("https://maps.google.com/?q=-34.603722,-58.381592");
    }

    @Test
    void create_whenAntecedenteBootstrapFails_throwsAndStopsFlow() {
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new IllegalStateException("bootstrap fail"))
                .when(antecedenteBootstrapService)
                .ensureDefaults(any(), any());

        CreateConsultorioCommand cmd = new CreateConsultorioCommand(
                "Nuevo", null, "Av. 123", "1155550000", "nuevo@mail.com", null, null, null);

        assertThatThrownBy(() -> service.create(cmd, ADMIN_ROLES))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("bootstrap fail");
    }

    @Test
    void create_asProfAdmin_throwsAccessDenied() {
        CreateConsultorioCommand cmd = new CreateConsultorioCommand(
                "Nuevo", null, null, null, null, null, null, null);

        assertThatThrownBy(() -> service.create(cmd, PROF_ADMIN_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    // ─── update ───────────────────────────────────────────────────────────────

    @Test
    void update_asAdmin_succeeds() {
        Consultorio c = activeConsultorio();
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(c));
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateConsultorioCommand cmd = new UpdateConsultorioCommand(
                CONSULTORIO_ID, "Actualizado", null, "Nueva Dir", "1155559999", null, null, null, null);
        ConsultorioResult result = service.update(cmd, ADMIN_EMAIL, ADMIN_ROLES);

        assertThat(result.name()).isEqualTo("Actualizado");
    }

    @Test
    void update_withMapData_persistsCoordinatesAndUrl() {
        Consultorio c = activeConsultorio();
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(c));
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateConsultorioCommand cmd = new UpdateConsultorioCommand(
                CONSULTORIO_ID, "Actualizado", null, "Nueva Dir", "1155559999", null,
                new BigDecimal("-34.603722"), new BigDecimal("-58.381592"),
                "https://maps.google.com/?q=-34.603722,-58.381592");
        ConsultorioResult result = service.update(cmd, ADMIN_EMAIL, ADMIN_ROLES);

        assertThat(result.mapLatitude()).isEqualByComparingTo("-34.603722");
        assertThat(result.mapLongitude()).isEqualByComparingTo("-58.381592");
        assertThat(result.googleMapsUrl()).isEqualTo("https://maps.google.com/?q=-34.603722,-58.381592");
    }

    @Test
    void update_inactive_throwsConsultorioInactiveException() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(inactiveConsultorio()));

        UpdateConsultorioCommand cmd = new UpdateConsultorioCommand(
                CONSULTORIO_ID, "Actualizado", null, "Nueva Dir", "1155559999", null, null, null, null);

        assertThatThrownBy(() -> service.update(cmd, ADMIN_EMAIL, ADMIN_ROLES))
                .isInstanceOf(ConsultorioInactiveException.class);
    }

    // ─── inactivate ───────────────────────────────────────────────────────────

    @Test
    void inactivate_asAdmin_setsInactive() {
        Consultorio c = activeConsultorio();
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(c));
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.inactivate(CONSULTORIO_ID, ADMIN_EMAIL, ADMIN_ROLES);

        assertThat(c.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    void inactivate_asProfAdmin_throwsAccessDenied() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(activeConsultorio()));

        assertThatThrownBy(() -> service.inactivate(CONSULTORIO_ID, ADMIN_EMAIL, PROF_ADMIN_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void activate_asAdmin_setsActive() {
        Consultorio c = inactiveConsultorio();
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(c));
        when(consultorioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ConsultorioResult result = service.activate(CONSULTORIO_ID, ADMIN_ROLES);

        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    void activate_asProfAdmin_throwsAccessDenied() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(inactiveConsultorio()));

        assertThatThrownBy(() -> service.activate(CONSULTORIO_ID, PROF_ADMIN_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }
}
