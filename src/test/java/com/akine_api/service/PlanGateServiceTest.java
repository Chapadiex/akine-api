package com.akine_api.service;

import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.PacienteConsultorioRepositoryPort;
import com.akine_api.application.port.output.PlanDefinicionRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.SuscripcionRepositoryPort;
import com.akine_api.application.service.PlanGateService;
import com.akine_api.domain.exception.PlanLimitExceededException;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.PlanDefinicion;
import com.akine_api.domain.model.Suscripcion;
import com.akine_api.domain.model.SuscripcionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanGateServiceTest {

    @Mock SuscripcionRepositoryPort suscripcionRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock PlanDefinicionRepositoryPort planRepo;
    @Mock ProfesionalRepositoryPort profesionalRepo;
    @Mock PacienteConsultorioRepositoryPort pacienteConsultorioRepo;

    PlanGateService service;

    private static final UUID EMPRESA_ID     = UUID.randomUUID();
    private static final UUID CONSULTORIO_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new PlanGateService(suscripcionRepo, consultorioRepo, planRepo, profesionalRepo, pacienteConsultorioRepo);
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private Consultorio consultorioWithEmpresa() {
        return new Consultorio(CONSULTORIO_ID, "Test", null, "Dir", "1155550000", "t@t.com", "ACTIVE", EMPRESA_ID, Instant.now());
    }

    private Suscripcion activeSuscripcion(String planCode) {
        return new Suscripcion(
                UUID.randomUUID(), UUID.randomUUID(), EMPRESA_ID, CONSULTORIO_ID,
                planCode, "MONTHLY", "REVIEW", "SIM-1", "token",
                SuscripcionStatus.ACTIVE,
                Instant.now(), LocalDate.now(), LocalDate.now().plusMonths(1),
                null, null,
                null, null, null,
                Instant.now(), Instant.now(), Instant.now()
        );
    }

    private PlanDefinicion planBasico() {
        return new PlanDefinicion("BASICO", "Plan Basico", null,
                new BigDecimal("15000"), new BigDecimal("150000"),
                1, 3, 500,
                false, false, false, false, true, 1);
    }

    private PlanDefinicion planEnterprise() {
        return new PlanDefinicion("ENTERPRISE", "Plan Enterprise", null,
                new BigDecimal("80000"), new BigDecimal("800000"),
                null, null, null,
                true, true, true, true, true, 3);
    }

    // ── checkConsultorioLimit ─────────────────────────────────────────────────

    @Test
    void checkConsultorioLimit_belowLimit_doesNotThrow() {
        when(suscripcionRepo.findByEmpresaId(EMPRESA_ID)).thenReturn(Optional.of(activeSuscripcion("BASICO")));
        when(planRepo.findByCodigo("BASICO")).thenReturn(Optional.of(planBasico()));
        when(consultorioRepo.countByEmpresaId(EMPRESA_ID)).thenReturn(0L);

        assertThatCode(() -> service.checkConsultorioLimit(EMPRESA_ID)).doesNotThrowAnyException();
    }

    @Test
    void checkConsultorioLimit_atLimit_throwsPlanLimitExceeded() {
        when(suscripcionRepo.findByEmpresaId(EMPRESA_ID)).thenReturn(Optional.of(activeSuscripcion("BASICO")));
        when(planRepo.findByCodigo("BASICO")).thenReturn(Optional.of(planBasico()));
        when(consultorioRepo.countByEmpresaId(EMPRESA_ID)).thenReturn(1L); // maxConsultorios = 1

        assertThatThrownBy(() -> service.checkConsultorioLimit(EMPRESA_ID))
                .isInstanceOf(PlanLimitExceededException.class)
                .hasMessageContaining("consultorios");
    }

    @Test
    void checkConsultorioLimit_enterprisePlan_unlimitedAlwaysPasses() {
        when(suscripcionRepo.findByEmpresaId(EMPRESA_ID)).thenReturn(Optional.of(activeSuscripcion("ENTERPRISE")));
        when(planRepo.findByCodigo("ENTERPRISE")).thenReturn(Optional.of(planEnterprise()));

        assertThatCode(() -> service.checkConsultorioLimit(EMPRESA_ID)).doesNotThrowAnyException();
    }

    @Test
    void checkConsultorioLimit_noActiveSuscripcion_isPermissive() {
        when(suscripcionRepo.findByEmpresaId(EMPRESA_ID)).thenReturn(Optional.empty());

        assertThatCode(() -> service.checkConsultorioLimit(EMPRESA_ID)).doesNotThrowAnyException();
    }

    @Test
    void checkConsultorioLimit_nullEmpresaId_isPermissive() {
        assertThatCode(() -> service.checkConsultorioLimit(null)).doesNotThrowAnyException();
    }

    // ── checkProfesionalLimit ─────────────────────────────────────────────────

    @Test
    void checkProfesionalLimit_belowLimit_doesNotThrow() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorioWithEmpresa()));
        when(suscripcionRepo.findByEmpresaId(EMPRESA_ID)).thenReturn(Optional.of(activeSuscripcion("BASICO")));
        when(planRepo.findByCodigo("BASICO")).thenReturn(Optional.of(planBasico()));
        when(profesionalRepo.countByConsultorioId(CONSULTORIO_ID)).thenReturn(2L); // max = 3

        assertThatCode(() -> service.checkProfesionalLimit(CONSULTORIO_ID)).doesNotThrowAnyException();
    }

    @Test
    void checkProfesionalLimit_atLimit_throwsPlanLimitExceeded() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorioWithEmpresa()));
        when(suscripcionRepo.findByEmpresaId(EMPRESA_ID)).thenReturn(Optional.of(activeSuscripcion("BASICO")));
        when(planRepo.findByCodigo("BASICO")).thenReturn(Optional.of(planBasico()));
        when(profesionalRepo.countByConsultorioId(CONSULTORIO_ID)).thenReturn(3L); // max = 3

        assertThatThrownBy(() -> service.checkProfesionalLimit(CONSULTORIO_ID))
                .isInstanceOf(PlanLimitExceededException.class)
                .hasMessageContaining("profesionales");
    }

    @Test
    void checkProfesionalLimit_noEmpresaOnConsultorio_isPermissive() {
        Consultorio sinEmpresa = new Consultorio(CONSULTORIO_ID, "Test", null, "Dir", "1155550000", "t@t.com", "ACTIVE", Instant.now());
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(sinEmpresa));

        assertThatCode(() -> service.checkProfesionalLimit(CONSULTORIO_ID)).doesNotThrowAnyException();
    }

    // ── checkPacienteLimit ────────────────────────────────────────────────────

    @Test
    void checkPacienteLimit_atLimit_throwsPlanLimitExceeded() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorioWithEmpresa()));
        when(suscripcionRepo.findByEmpresaId(EMPRESA_ID)).thenReturn(Optional.of(activeSuscripcion("BASICO")));
        when(planRepo.findByCodigo("BASICO")).thenReturn(Optional.of(planBasico()));
        when(pacienteConsultorioRepo.countByConsultorioId(CONSULTORIO_ID)).thenReturn(500L); // max = 500

        assertThatThrownBy(() -> service.checkPacienteLimit(CONSULTORIO_ID))
                .isInstanceOf(PlanLimitExceededException.class)
                .hasMessageContaining("pacientes");
    }

    @Test
    void checkPacienteLimit_enterprisePlan_unlimitedAlwaysPasses() {
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorioWithEmpresa()));
        when(suscripcionRepo.findByEmpresaId(EMPRESA_ID)).thenReturn(Optional.of(activeSuscripcion("ENTERPRISE")));
        when(planRepo.findByCodigo("ENTERPRISE")).thenReturn(Optional.of(planEnterprise()));

        assertThatCode(() -> service.checkPacienteLimit(CONSULTORIO_ID)).doesNotThrowAnyException();
    }
}
