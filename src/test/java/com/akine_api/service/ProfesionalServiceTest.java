package com.akine_api.service;

import com.akine_api.application.dto.command.CreateProfesionalCommand;
import com.akine_api.application.dto.result.ProfesionalResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.PlanGateService;
import com.akine_api.application.service.ProfesionalService;
import com.akine_api.domain.exception.ConsultorioNotFoundException;
import com.akine_api.domain.exception.ProfesionalNotFoundException;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.Profesional;
import com.akine_api.domain.model.ProfesionalConsultorio;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfesionalServiceTest {

    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock PlanGateService planGateService;

    ProfesionalService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PROF_ID = UUID.randomUUID();
    private static final String USER_EMAIL = "user@test.com";
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");
    private static final Set<String> PROFESIONAL_ROLES = Set.of("ROLE_PROFESIONAL");

    @BeforeEach
    void setUp() {
        service = new ProfesionalService(profesionalRepo, profesionalConsultorioRepo, consultorioRepo, userRepo, planGateService);
    }

    private Consultorio consultorio() {
        return new Consultorio(CONSULTORIO_ID, "Test", null, null, null, null, "ACTIVE", Instant.now());
    }

    private Profesional profesional() {
        return new Profesional(
                PROF_ID,
                CONSULTORIO_ID,
                "Juan",
                "Perez",
                "12345678",
                "MP-1234",
                "Kinesiologia",
                "Kinesiologia|Traumatologia",
                "juan@mail.com",
                "1155550000",
                null,
                null,
                LocalDate.now(),
                null,
                null,
                true,
                Instant.now()
        );
    }

    @Test
    void list_asAdmin_returnsAll() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(profesionalRepo.findByConsultorioId(CONSULTORIO_ID)).thenReturn(List.of(profesional()));
        when(profesionalConsultorioRepo.findByProfesionalId(any())).thenReturn(List.of());

        List<ProfesionalResult> result = service.list(
                CONSULTORIO_ID, USER_EMAIL, ADMIN_ROLES, null, null, null, null, null
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).matricula()).isEqualTo("MP-1234");
    }

    @Test
    void list_consultorioNotFound_throws() {
        when(consultorioRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.list(
                CONSULTORIO_ID, USER_EMAIL, ADMIN_ROLES, null, null, null, null, null
        )).isInstanceOf(ConsultorioNotFoundException.class);
    }

    @Test
    void create_asAdmin_succeeds() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(profesionalRepo.existsByMatriculaAndConsultorioId("MP-9999", CONSULTORIO_ID)).thenReturn(false);
        when(profesionalRepo.existsByNroDocumento("32123123")).thenReturn(false);
        when(profesionalRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(profesionalConsultorioRepo.findByProfesionalIdAndConsultorioId(any(), eq(CONSULTORIO_ID)))
                .thenReturn(Optional.empty());
        when(profesionalConsultorioRepo.findByProfesionalId(any())).thenAnswer(inv -> List.of(
                new ProfesionalConsultorio(
                        UUID.randomUUID(),
                        inv.getArgument(0),
                        CONSULTORIO_ID,
                        true,
                        Instant.now()
                )
        ));

        CreateProfesionalCommand cmd = new CreateProfesionalCommand(
                CONSULTORIO_ID, "Ana", "Garcia", "32123123", "MP-9999",
                "Nutricion", "Nutricion|Kinesiologia", "ana@mail.com", "1155559999",
                null, null
        );
        ProfesionalResult result = service.create(cmd, USER_EMAIL, ADMIN_ROLES);

        assertThat(result.nombre()).isEqualTo("Ana");
        assertThat(result.matricula()).isEqualTo("MP-9999");
        assertThat(result.activo()).isTrue();
        assertThat(result.consultoriosAsociados()).isEqualTo(1);
        verify(profesionalConsultorioRepo).save(argThat(pc ->
                pc.getProfesionalId().equals(result.id())
                        && pc.getConsultorioId().equals(CONSULTORIO_ID)
                        && pc.isActivo()));
    }

    @Test
    void create_duplicateMatricula_throwsIllegalArgument() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(profesionalRepo.existsByMatriculaAndConsultorioId("MP-1234", CONSULTORIO_ID)).thenReturn(true);

        CreateProfesionalCommand cmd = new CreateProfesionalCommand(
                CONSULTORIO_ID, "Otro", "Prof", "30111222", "MP-1234",
                null, "Kinesiologia", null, null, null, null
        );

        assertThatThrownBy(() -> service.create(cmd, USER_EMAIL, ADMIN_ROLES))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("matricula");
    }

    @Test
    void create_asProfesional_throwsAccessDenied() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));

        CreateProfesionalCommand cmd = new CreateProfesionalCommand(
                CONSULTORIO_ID, "Test", "Prof", "30112233", "MP-0001",
                null, "Kinesiologia", null, null, null, null
        );

        assertThatThrownBy(() -> service.create(cmd, USER_EMAIL, PROFESIONAL_ROLES))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void inactivate_asAdmin_setsInactive() {
        Profesional p = profesional();
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(profesionalRepo.findById(PROF_ID)).thenReturn(Optional.of(p));
        when(profesionalRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(profesionalConsultorioRepo.findByProfesionalId(any())).thenReturn(List.of());

        service.inactivate(CONSULTORIO_ID, PROF_ID, USER_EMAIL, ADMIN_ROLES);

        assertThat(p.isActivo()).isFalse();
        assertThat(p.getFechaBaja()).isNotNull();
    }

    @Test
    void inactivate_notFound_throws() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio()));
        when(profesionalRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.inactivate(CONSULTORIO_ID, PROF_ID, USER_EMAIL, ADMIN_ROLES))
                .isInstanceOf(ProfesionalNotFoundException.class);
    }
}
