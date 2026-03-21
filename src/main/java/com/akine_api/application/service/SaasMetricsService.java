package com.akine_api.application.service;

import com.akine_api.application.dto.result.SaasMetricsResult;
import com.akine_api.application.dto.result.SaasMetricsResult.MrrResult;
import com.akine_api.application.dto.result.SaasMetricsResult.VencimientoProximoResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.PlanDefinicionRepositoryPort;
import com.akine_api.application.port.output.SuscripcionRepositoryPort;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.PlanDefinicion;
import com.akine_api.domain.model.Suscripcion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class SaasMetricsService {

    private final SuscripcionRepositoryPort suscripcionRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final PlanDefinicionRepositoryPort planRepo;

    public SaasMetricsService(SuscripcionRepositoryPort suscripcionRepo,
                               ConsultorioRepositoryPort consultorioRepo,
                               PlanDefinicionRepositoryPort planRepo) {
        this.suscripcionRepo = suscripcionRepo;
        this.consultorioRepo = consultorioRepo;
        this.planRepo = planRepo;
    }

    public SaasMetricsResult getMetrics() {
        LocalDate today = LocalDate.now();
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);

        Map<String, Long> totalSuscripciones = suscripcionRepo.countGroupByStatus();
        Map<String, Long> distribucionPlanes = suscripcionRepo.countActiveGroupByPlanCode();
        MrrResult mrr = calcularMrr(distribucionPlanes);
        List<VencimientoProximoResult> vencimientos = calcularVencimientosProximos(today);
        long nuevas = suscripcionRepo.countCreatedAfter(thirtyDaysAgo);
        long churns = suscripcionRepo.countExpiredSince(thirtyDaysAgo);

        return new SaasMetricsResult(
                totalSuscripciones,
                distribucionPlanes,
                mrr,
                vencimientos,
                nuevas,
                churns
        );
    }

    private MrrResult calcularMrr(Map<String, Long> distribucionPlanes) {
        List<PlanDefinicion> planes = planRepo.findAllActivos();
        Map<String, BigDecimal> preciosPorPlan = new HashMap<>();
        for (PlanDefinicion plan : planes) {
            if (plan.getPrecioMensual() != null) {
                preciosPorPlan.put(plan.getCodigo(), plan.getPrecioMensual());
            }
        }

        Map<String, BigDecimal> mrrPorPlan = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, Long> entry : distribucionPlanes.entrySet()) {
            String planCode = entry.getKey();
            long count = entry.getValue();
            BigDecimal precio = preciosPorPlan.getOrDefault(planCode, BigDecimal.ZERO);
            BigDecimal planMrr = precio.multiply(BigDecimal.valueOf(count));
            mrrPorPlan.put(planCode, planMrr);
            total = total.add(planMrr);
        }

        return new MrrResult(total, mrrPorPlan);
    }

    private List<VencimientoProximoResult> calcularVencimientosProximos(LocalDate today) {
        List<Suscripcion> expirando = suscripcionRepo.findExpiringBetween(today, today.plusDays(30));
        return expirando.stream()
                .map(sub -> {
                    Consultorio c = consultorioRepo.findById(sub.getConsultorioBaseId()).orElse(null);
                    String nro = c != null ? c.getNroConsultorio() : null;
                    String razonSocial = c != null ? c.getLegalName() : null;
                    long diasRestantes = ChronoUnit.DAYS.between(today, sub.getEndDate());
                    return new VencimientoProximoResult(nro, razonSocial, sub.getEndDate(), diasRestantes);
                })
                .toList();
    }
}
