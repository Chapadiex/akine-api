package com.akine_api.service;

import com.akine_api.application.dto.result.SaasMetricsResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.PlanDefinicionRepositoryPort;
import com.akine_api.application.port.output.SuscripcionRepositoryPort;
import com.akine_api.application.service.SaasMetricsService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaasMetricsServiceTest {

    @Mock SuscripcionRepositoryPort suscripcionRepo;
    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock PlanDefinicionRepositoryPort planRepo;

    SaasMetricsService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID EMPRESA_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new SaasMetricsService(suscripcionRepo, consultorioRepo, planRepo);
    }

    @Test
    void getMetrics_returnsCountsByStatus() {
        when(suscripcionRepo.countGroupByStatus()).thenReturn(Map.of("ACTIVE", 10L, "EXPIRED", 2L));
        when(suscripcionRepo.countActiveGroupByPlanCode()).thenReturn(Map.of());
        when(planRepo.findAllActivos()).thenReturn(List.of());
        when(suscripcionRepo.findExpiringBetween(any(), any())).thenReturn(List.of());
        when(suscripcionRepo.countCreatedAfter(any())).thenReturn(0L);
        when(suscripcionRepo.countExpiredSince(any())).thenReturn(0L);

        SaasMetricsResult result = service.getMetrics();

        assertThat(result.totalSuscripciones()).containsEntry("ACTIVE", 10L);
        assertThat(result.totalSuscripciones()).containsEntry("EXPIRED", 2L);
    }

    @Test
    void getMetrics_calculatesDistribucionPlanes() {
        when(suscripcionRepo.countGroupByStatus()).thenReturn(Map.of());
        when(suscripcionRepo.countActiveGroupByPlanCode()).thenReturn(Map.of("BASICO", 5L, "PROFESIONAL", 3L));
        when(planRepo.findAllActivos()).thenReturn(List.of());
        when(suscripcionRepo.findExpiringBetween(any(), any())).thenReturn(List.of());
        when(suscripcionRepo.countCreatedAfter(any())).thenReturn(0L);
        when(suscripcionRepo.countExpiredSince(any())).thenReturn(0L);

        SaasMetricsResult result = service.getMetrics();

        assertThat(result.distribucionPlanes()).containsEntry("BASICO", 5L);
        assertThat(result.distribucionPlanes()).containsEntry("PROFESIONAL", 3L);
    }

    @Test
    void getMetrics_calculatesMrr() {
        PlanDefinicion basico = new PlanDefinicion("BASICO", "Plan Basico", null,
                new BigDecimal("15000"), null, 1, 3, 500,
                false, false, false, false, true, 1);
        PlanDefinicion profesional = new PlanDefinicion("PROFESIONAL", "Plan Profesional", null,
                new BigDecimal("35000"), null, 3, 10, null,
                true, true, true, true, true, 2);

        when(suscripcionRepo.countGroupByStatus()).thenReturn(Map.of());
        when(suscripcionRepo.countActiveGroupByPlanCode()).thenReturn(Map.of("BASICO", 4L, "PROFESIONAL", 2L));
        when(planRepo.findAllActivos()).thenReturn(List.of(basico, profesional));
        when(suscripcionRepo.findExpiringBetween(any(), any())).thenReturn(List.of());
        when(suscripcionRepo.countCreatedAfter(any())).thenReturn(0L);
        when(suscripcionRepo.countExpiredSince(any())).thenReturn(0L);

        SaasMetricsResult result = service.getMetrics();

        // BASICO: 4 × 15000 = 60000; PROFESIONAL: 2 × 35000 = 70000; total = 130000
        assertThat(result.mrr().porPlan()).containsEntry("BASICO", new BigDecimal("60000"));
        assertThat(result.mrr().porPlan()).containsEntry("PROFESIONAL", new BigDecimal("70000"));
        assertThat(result.mrr().total()).isEqualByComparingTo(new BigDecimal("130000"));
    }

    @Test
    void getMetrics_vencimientosProximos_includesNroAndRazonSocial() {
        LocalDate endDate = LocalDate.now().plusDays(7);
        Suscripcion sub = new Suscripcion(UUID.randomUUID(), UUID.randomUUID(), EMPRESA_ID, CONSULTORIO_ID,
                "BASICO", "MONTHLY", "ACTIVE", null, "token",
                SuscripcionStatus.ACTIVE, Instant.now(),
                LocalDate.now().minusMonths(1), endDate, null, null,
                null, null, null, null, Instant.now(), Instant.now());

        Consultorio consultorio = buildConsultorio("AKN-000042", "Clinica Norte");

        when(suscripcionRepo.countGroupByStatus()).thenReturn(Map.of());
        when(suscripcionRepo.countActiveGroupByPlanCode()).thenReturn(Map.of());
        when(planRepo.findAllActivos()).thenReturn(List.of());
        when(suscripcionRepo.findExpiringBetween(any(), any())).thenReturn(List.of(sub));
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(consultorio));
        when(suscripcionRepo.countCreatedAfter(any())).thenReturn(0L);
        when(suscripcionRepo.countExpiredSince(any())).thenReturn(0L);

        SaasMetricsResult result = service.getMetrics();

        assertThat(result.vencimientosProximos()).hasSize(1);
        SaasMetricsResult.VencimientoProximoResult v = result.vencimientosProximos().get(0);
        assertThat(v.nroConsultorio()).isEqualTo("AKN-000042");
        assertThat(v.razonSocial()).isEqualTo("Clinica Norte");
        assertThat(v.endDate()).isEqualTo(endDate);
        assertThat(v.diasRestantes()).isEqualTo(7L);
    }

    @Test
    void getMetrics_returnsNuevasYChurns() {
        when(suscripcionRepo.countGroupByStatus()).thenReturn(Map.of());
        when(suscripcionRepo.countActiveGroupByPlanCode()).thenReturn(Map.of());
        when(planRepo.findAllActivos()).thenReturn(List.of());
        when(suscripcionRepo.findExpiringBetween(any(), any())).thenReturn(List.of());
        when(suscripcionRepo.countCreatedAfter(any())).thenReturn(7L);
        when(suscripcionRepo.countExpiredSince(any())).thenReturn(1L);

        SaasMetricsResult result = service.getMetrics();

        assertThat(result.nuevasSuscripcionesUltimos30Dias()).isEqualTo(7L);
        assertThat(result.churnsUltimos30Dias()).isEqualTo(1L);
    }

    @Test
    void getMetrics_vencimientosSinConsultorio_usesNulls() {
        LocalDate endDate = LocalDate.now().plusDays(3);
        Suscripcion sub = new Suscripcion(UUID.randomUUID(), UUID.randomUUID(), EMPRESA_ID, CONSULTORIO_ID,
                "BASICO", "MONTHLY", "ACTIVE", null, "token",
                SuscripcionStatus.ACTIVE, Instant.now(),
                LocalDate.now().minusMonths(1), endDate, null, null,
                null, null, null, null, Instant.now(), Instant.now());

        when(suscripcionRepo.countGroupByStatus()).thenReturn(Map.of());
        when(suscripcionRepo.countActiveGroupByPlanCode()).thenReturn(Map.of());
        when(planRepo.findAllActivos()).thenReturn(List.of());
        when(suscripcionRepo.findExpiringBetween(any(), any())).thenReturn(List.of(sub));
        when(consultorioRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.empty());
        when(suscripcionRepo.countCreatedAfter(any())).thenReturn(0L);
        when(suscripcionRepo.countExpiredSince(any())).thenReturn(0L);

        SaasMetricsResult result = service.getMetrics();

        SaasMetricsResult.VencimientoProximoResult v = result.vencimientosProximos().get(0);
        assertThat(v.nroConsultorio()).isNull();
        assertThat(v.razonSocial()).isNull();
    }

    @Test
    void getMetrics_mrrZero_whenNoActiveSubs() {
        when(suscripcionRepo.countGroupByStatus()).thenReturn(Map.of());
        when(suscripcionRepo.countActiveGroupByPlanCode()).thenReturn(Map.of());
        when(planRepo.findAllActivos()).thenReturn(List.of());
        when(suscripcionRepo.findExpiringBetween(any(), any())).thenReturn(List.of());
        when(suscripcionRepo.countCreatedAfter(any())).thenReturn(0L);
        when(suscripcionRepo.countExpiredSince(any())).thenReturn(0L);

        SaasMetricsResult result = service.getMetrics();

        assertThat(result.mrr().total()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.mrr().porPlan()).isEmpty();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Consultorio buildConsultorio(String nroConsultorio, String legalName) {
        return new Consultorio(
                CONSULTORIO_ID, "Consultorio Test", null, null, null, legalName,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                "ACTIVE", EMPRESA_ID, nroConsultorio, null, Instant.now()
        );
    }
}
