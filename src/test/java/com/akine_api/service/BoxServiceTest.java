package com.akine_api.service;

import com.akine_api.application.dto.command.CreateBoxCommand;
import com.akine_api.application.dto.command.UpdateBoxCommand;
import com.akine_api.application.dto.result.BoxResult;
import com.akine_api.application.port.output.BoxRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.BoxService;
import com.akine_api.domain.exception.BoxNotFoundException;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.model.*;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoxServiceTest {

    @Mock BoxRepositoryPort boxRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;

    BoxService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID BOX_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_EMAIL = "user@test.com";
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");
    private static final Set<String> PROF_ADMIN_ROLES = Set.of("ROLE_PROFESIONAL_ADMIN");
    private static final Set<String> PROFESIONAL_ROLES = Set.of("ROLE_PROFESIONAL");

    @BeforeEach
    void setUp() {
        service = new BoxService(boxRepo, consultorioRepo, userRepo);
    }

    private Consultorio consultorio() {
        return new Consultorio(CONSULTORIO_ID, "Test", null, null, null, null, "ACTIVE", Instant.now());
    }

    private Box box() {
        return new Box(BOX_ID, CONSULTORIO_ID, "Box 1", "B01", BoxTipo.BOX,
                BoxCapacidadTipo.UNLIMITED, null, true, Instant.now());
    }

    private User user() {
        return new User(USER_ID, USER_EMAIL, "hash", "Test", "User", null, UserStatus.ACTIVE, Instant.now());
    }

    // ─── list ─────────────────────────────────────────────────────────────────

    @Test
    void list_asAdmin_returnsAll() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(boxRepo.findByConsultorioId(CONSULTORIO_ID)).thenReturn(List.of(box()));

        List<BoxResult> result = service.list(CONSULTORIO_ID, USER_EMAIL, ADMIN_ROLES);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Box 1");
    }

    @Test
    void list_consultorioNotFound_throws() {
        when(consultorioRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.list(CONSULTORIO_ID, USER_EMAIL, ADMIN_ROLES))
                .isInstanceOf(ConsultorioNotFoundException.class);
    }

    @Test
    void list_noMembership_throwsAccessDenied() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(userRepo.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> service.list(CONSULTORIO_ID, USER_EMAIL, PROFESIONAL_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void create_asAdmin_succeeds() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(boxRepo.existsByCodigoAndConsultorioId(any(), any())).thenReturn(false);
        when(boxRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateBoxCommand cmd = new CreateBoxCommand(CONSULTORIO_ID, "Box 2", "B02", BoxTipo.GIMNASIO);
        BoxResult result = service.create(cmd, USER_EMAIL, ADMIN_ROLES);

        assertThat(result.nombre()).isEqualTo("Box 2");
        assertThat(result.tipo()).isEqualTo(BoxTipo.GIMNASIO);
    }

    @Test
    void create_duplicateCodigo_throwsIllegalArgument() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(boxRepo.existsByCodigoAndConsultorioId("B01", CONSULTORIO_ID)).thenReturn(true);

        CreateBoxCommand cmd = new CreateBoxCommand(CONSULTORIO_ID, "Box X", "B01", BoxTipo.BOX);

        assertThatThrownBy(() -> service.create(cmd, USER_EMAIL, ADMIN_ROLES))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("B01");
    }

    @Test
    void create_asProfesional_throwsAccessDenied() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));

        CreateBoxCommand cmd = new CreateBoxCommand(CONSULTORIO_ID, "Box X", null, BoxTipo.BOX);

        assertThatThrownBy(() -> service.create(cmd, USER_EMAIL, PROFESIONAL_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    // ─── inactivate ───────────────────────────────────────────────────────────

    @Test
    void inactivate_asAdmin_setsInactive() {
        Box b = box();
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(boxRepo.findById(BOX_ID)).thenReturn(Optional.of(b));
        when(boxRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.inactivate(CONSULTORIO_ID, BOX_ID, USER_EMAIL, ADMIN_ROLES);

        assertThat(b.isActivo()).isFalse();
    }

    @Test
    void inactivate_boxNotFound_throws() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(boxRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.inactivate(CONSULTORIO_ID, BOX_ID, USER_EMAIL, ADMIN_ROLES))
                .isInstanceOf(BoxNotFoundException.class);
    }
}
