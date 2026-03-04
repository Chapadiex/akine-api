package com.akine_api.service;

import com.akine_api.application.dto.command.ChangeObraSocialEstadoCommand;
import com.akine_api.application.dto.command.PlanCommand;
import com.akine_api.application.dto.command.UpsertObraSocialCommand;
import com.akine_api.application.dto.result.ObraSocialDetailResult;
import com.akine_api.application.dto.result.PagedResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.ObraSocialRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.ObraSocialService;
import com.akine_api.domain.exception.ObraSocialConflictException;
import com.akine_api.domain.exception.ObraSocialValidationException;
import com.akine_api.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObraSocialServiceTest {

    @Mock ObraSocialRepositoryPort obraSocialRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;

    private ObraSocialService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_EMAIL = "admin@akine.com";

    @BeforeEach
    void setUp() {
        service = new ObraSocialService(obraSocialRepo, consultorioRepo, userRepo);
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(new Consultorio(
                CONSULTORIO_ID, "Cons", null, null, null, null, "ACTIVE", Instant.now()
        )));
        when(userRepo.findByEmail(USER_EMAIL)).thenReturn(Optional.of(
                new User(USER_ID, USER_EMAIL, "hash", "A", "B", null, UserStatus.ACTIVE, Instant.now())
        ));
    }

    @Test
    void create_withValidData_succeeds() {
        when(obraSocialRepo.existsByConsultorioIdAndCuit(any(), any())).thenReturn(false);
        when(obraSocialRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ObraSocialDetailResult result = service.create(buildCommand(null), USER_EMAIL, Set.of("ROLE_ADMIN"));

        assertThat(result.acronimo()).isEqualTo("OSDE");
        assertThat(result.planes()).hasSize(1);
    }

    @Test
    void create_withoutPlans_fails() {
        UpsertObraSocialCommand cmd = new UpsertObraSocialCommand(
                null, CONSULTORIO_ID, "OSDE", "OSDE 210", "30712345689",
                "mail@test.com", null, null, null, null, null,
                ObraSocialEstado.ACTIVE, List.of()
        );

        assertThatThrownBy(() -> service.create(cmd, USER_EMAIL, Set.of("ROLE_ADMIN")))
                .isInstanceOf(ObraSocialValidationException.class);
    }

    @Test
    void create_withDuplicateCuit_fails() {
        when(obraSocialRepo.existsByConsultorioIdAndCuit(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.create(buildCommand(null), USER_EMAIL, Set.of("ROLE_ADMIN")))
                .isInstanceOf(ObraSocialConflictException.class);
    }

    @Test
    void create_withoutContact_fails() {
        UpsertObraSocialCommand cmd = new UpsertObraSocialCommand(
                null, CONSULTORIO_ID, "OSDE", "OSDE 210", "30712345689",
                null, null, null, null, null, null,
                ObraSocialEstado.ACTIVE, List.of(buildPlan("210", TipoCoseguro.MONTO, BigDecimal.TEN))
        );

        assertThatThrownBy(() -> service.create(cmd, USER_EMAIL, Set.of("ROLE_ADMIN")))
                .isInstanceOf(ObraSocialValidationException.class);
    }

    @Test
    void create_withDuplicatedPlanName_fails() {
        UpsertObraSocialCommand cmd = new UpsertObraSocialCommand(
                null, CONSULTORIO_ID, "OSDE", "OSDE 210", "30712345689",
                "mail@test.com", null, null, null, null, null,
                ObraSocialEstado.ACTIVE,
                List.of(
                        buildPlan("210", TipoCoseguro.MONTO, BigDecimal.ONE),
                        buildPlan("210", TipoCoseguro.MONTO, BigDecimal.ONE)
                )
        );

        assertThatThrownBy(() -> service.create(cmd, USER_EMAIL, Set.of("ROLE_ADMIN")))
                .isInstanceOf(ObraSocialConflictException.class);
    }

    @Test
    void create_withInvalidCoveragePercentage_fails() {
        UpsertObraSocialCommand cmd = new UpsertObraSocialCommand(
                null, CONSULTORIO_ID, "OSDE", "OSDE 210", "30712345689",
                "mail@test.com", null, null, null, null, null,
                ObraSocialEstado.ACTIVE,
                List.of(new PlanCommand(null, "210", "OSDE 210", TipoCobertura.PORCENTAJE,
                        BigDecimal.valueOf(120), TipoCoseguro.MONTO, BigDecimal.ONE, 0, null, true))
        );

        assertThatThrownBy(() -> service.create(cmd, USER_EMAIL, Set.of("ROLE_ADMIN")))
                .isInstanceOf(ObraSocialValidationException.class);
    }

    @Test
    void create_withSinCoseguro_setsZero() {
        when(obraSocialRepo.existsByConsultorioIdAndCuit(any(), any())).thenReturn(false);
        when(obraSocialRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ObraSocialDetailResult result = service.create(new UpsertObraSocialCommand(
                null, CONSULTORIO_ID, "OSDE", "OSDE 210", "30712345689",
                "mail@test.com", null, null, null, null, null,
                ObraSocialEstado.ACTIVE,
                List.of(new PlanCommand(null, "210", "OSDE 210", TipoCobertura.MONTO,
                        BigDecimal.valueOf(1000), TipoCoseguro.SIN_COSEGURO, BigDecimal.valueOf(99), 0, null, true))
        ), USER_EMAIL, Set.of("ROLE_ADMIN"));

        assertThat(result.planes().get(0).valorCoseguro()).isEqualByComparingTo("0.00");
    }

    @Test
    void list_asAdministrativoMember_succeeds() {
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));
        when(obraSocialRepo.search(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        PagedResult<?> result = service.list(CONSULTORIO_ID, null, null, null, 0, 20, USER_EMAIL, Set.of("ROLE_ADMINISTRATIVO"));

        assertThat(result.total()).isZero();
    }

    @Test
    void write_asAdministrativo_fails() {
        assertThatThrownBy(() -> service.create(buildCommand(null), USER_EMAIL, Set.of("ROLE_ADMINISTRATIVO")))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void changeEstado_updatesState() {
        ObraSocial current = new ObraSocial(UUID.randomUUID(), CONSULTORIO_ID, "OSDE", "OSDE 210", "30712345689",
                "mail@test.com", "123", null, null, null, null, ObraSocialEstado.ACTIVE, Instant.now(), List.of());
        when(obraSocialRepo.findByIdAndConsultorioId(current.getId(), CONSULTORIO_ID)).thenReturn(Optional.of(current));
        when(obraSocialRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ObraSocialDetailResult result = service.changeEstado(
                new ChangeObraSocialEstadoCommand(CONSULTORIO_ID, current.getId(), ObraSocialEstado.INACTIVE),
                USER_EMAIL,
                Set.of("ROLE_ADMIN")
        );

        assertThat(result.estado()).isEqualTo(ObraSocialEstado.INACTIVE);
    }

    private UpsertObraSocialCommand buildCommand(UUID id) {
        return new UpsertObraSocialCommand(
                id,
                CONSULTORIO_ID,
                "OSDE",
                "OSDE 210",
                "30712345689",
                "mail@test.com",
                "1155550000",
                null,
                "Rep",
                null,
                null,
                ObraSocialEstado.ACTIVE,
                List.of(buildPlan("210", TipoCoseguro.MONTO, BigDecimal.TEN))
        );
    }

    private PlanCommand buildPlan(String shortName, TipoCoseguro tipoCoseguro, BigDecimal valorCoseguro) {
        return new PlanCommand(
                null,
                shortName,
                "Plan " + shortName,
                TipoCobertura.PORCENTAJE,
                BigDecimal.valueOf(80),
                tipoCoseguro,
                valorCoseguro,
                0,
                null,
                true
        );
    }
}


