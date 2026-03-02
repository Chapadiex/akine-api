package com.akine_api.service;

import com.akine_api.application.dto.command.CreateProfesionalCommand;
import com.akine_api.application.dto.result.ProfesionalResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.ProfesionalService;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.exception.ProfesionalNotFoundException;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.Profesional;
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
class ProfesionalServiceTest {

    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;

    ProfesionalService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PROF_ID = UUID.randomUUID();
    private static final String USER_EMAIL = "user@test.com";
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");
    private static final Set<String> PROFESIONAL_ROLES = Set.of("ROLE_PROFESIONAL");

    @BeforeEach
    void setUp() {
        service = new ProfesionalService(profesionalRepo, consultorioRepo, userRepo);
    }

    private Consultorio consultorio() {
        return new Consultorio(CONSULTORIO_ID, "Test", null, null, null, null, "ACTIVE", Instant.now());
    }

    private Profesional profesional() {
        return new Profesional(PROF_ID, CONSULTORIO_ID, "Juan", "Pérez",
                "MP-1234", "Kinesiología", "juan@mail.com", "1155550000", true, Instant.now());
    }

    // ─── list ─────────────────────────────────────────────────────────────────

    @Test
    void list_asAdmin_returnsAll() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(profesionalRepo.findByConsultorioId(CONSULTORIO_ID)).thenReturn(List.of(profesional()));

        List<ProfesionalResult> result = service.list(CONSULTORIO_ID, USER_EMAIL, ADMIN_ROLES);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).matricula()).isEqualTo("MP-1234");
    }

    @Test
    void list_consultorioNotFound_throws() {
        when(consultorioRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.list(CONSULTORIO_ID, USER_EMAIL, ADMIN_ROLES))
                .isInstanceOf(ConsultorioNotFoundException.class);
    }

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void create_asAdmin_succeeds() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(profesionalRepo.existsByMatriculaAndConsultorioId("MP-9999", CONSULTORIO_ID)).thenReturn(false);
        when(profesionalRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateProfesionalCommand cmd = new CreateProfesionalCommand(
                CONSULTORIO_ID, "Ana", "García", "MP-9999",
                "Nutrición", "ana@mail.com", "1155559999");
        ProfesionalResult result = service.create(cmd, USER_EMAIL, ADMIN_ROLES);

        assertThat(result.nombre()).isEqualTo("Ana");
        assertThat(result.matricula()).isEqualTo("MP-9999");
        assertThat(result.activo()).isTrue();
    }

    @Test
    void create_duplicateMatricula_throwsIllegalArgument() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(profesionalRepo.existsByMatriculaAndConsultorioId("MP-1234", CONSULTORIO_ID)).thenReturn(true);

        CreateProfesionalCommand cmd = new CreateProfesionalCommand(
                CONSULTORIO_ID, "Otro", "Prof", "MP-1234", null, null, null);

        assertThatThrownBy(() -> service.create(cmd, USER_EMAIL, ADMIN_ROLES))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MP-1234");
    }

    @Test
    void create_asProfesional_throwsAccessDenied() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));

        CreateProfesionalCommand cmd = new CreateProfesionalCommand(
                CONSULTORIO_ID, "Test", "Prof", "MP-0001", null, null, null);

        assertThatThrownBy(() -> service.create(cmd, USER_EMAIL, PROFESIONAL_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    // ─── inactivate ───────────────────────────────────────────────────────────

    @Test
    void inactivate_asAdmin_setsInactive() {
        Profesional p = profesional();
        when(profesionalRepo.findById(PROF_ID)).thenReturn(Optional.of(p));
        when(profesionalRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.inactivate(CONSULTORIO_ID, PROF_ID, USER_EMAIL, ADMIN_ROLES);

        assertThat(p.isActivo()).isFalse();
    }

    @Test
    void inactivate_notFound_throws() {
        when(profesionalRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.inactivate(CONSULTORIO_ID, PROF_ID, USER_EMAIL, ADMIN_ROLES))
                .isInstanceOf(ProfesionalNotFoundException.class);
    }
}
