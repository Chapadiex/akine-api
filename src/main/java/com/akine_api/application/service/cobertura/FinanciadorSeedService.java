package com.akine_api.application.service.cobertura;

import com.akine_api.domain.model.cobertura.FinanciadorSalud;
import com.akine_api.domain.model.cobertura.PlanFinanciador;
import com.akine_api.domain.model.cobertura.TipoFinanciador;
import com.akine_api.domain.model.cobertura.TipoPlan;
import com.akine_api.domain.repository.cobertura.FinanciadorSaludRepositoryPort;
import com.akine_api.domain.repository.cobertura.PlanFinanciadorRepositoryPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanciadorSeedService {

    private final FinanciadorSaludRepositoryPort financiadorRepo;
    private final PlanFinanciadorRepositoryPort planRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public void seedForConsultorio(UUID consultorioId) {
        try {
            ClassPathResource resource = new ClassPathResource("cobertura/financiadores_seed.json");
            try (InputStream is = resource.getInputStream()) {
                JsonNode root = objectMapper.readTree(is);
                JsonNode financiadores = root.get("financiadores");
                if (financiadores == null || !financiadores.isArray()) {
                    log.warn("Seed JSON sin array 'financiadores'");
                    return;
                }
                int count = 0;
                for (JsonNode node : financiadores) {
                    FinanciadorSalud financiador = parseFinanciador(node, consultorioId);
                    FinanciadorSalud saved = financiadorRepo.save(financiador);
                    JsonNode planes = node.get("planes");
                    if (planes != null && planes.isArray()) {
                        for (JsonNode planNode : planes) {
                            PlanFinanciador plan = parsePlan(planNode, saved.getId());
                            planRepo.save(plan);
                        }
                    }
                    count++;
                }
                log.info("Seed financiadores: {} registros creados para consultorio {}", count, consultorioId);
            }
        } catch (Exception e) {
            log.error("Error al cargar seed de financiadores para consultorio {}", consultorioId, e);
            throw new RuntimeException("Error al cargar financiadores por defecto", e);
        }
    }

    private FinanciadorSalud parseFinanciador(JsonNode node, UUID consultorioId) {
        return FinanciadorSalud.builder()
                .consultorioId(consultorioId)
                .nombre(node.path("nombreCompleto").asText())
                .nombreCorto(nullableText(node, "nombreCortoAcronimo"))
                .codigoExterno(nullableText(node, "codigoExternoSSS"))
                .tipoFinanciador(parseTipoFinanciador(node.path("tipoFinanciador").asText("Otro")))
                .ambitoCobertura(nullableText(node, "ambitoCobertura"))
                .activo(node.path("financiadorActivo").asBoolean(true))
                .build();
    }

    private PlanFinanciador parsePlan(JsonNode node, UUID financiadorId) {
        return PlanFinanciador.builder()
                .financiadorId(financiadorId)
                .nombrePlan(node.path("nombrePlan").asText("PLAN GENERAL"))
                .tipoPlan(parseTipoPlan(node.path("tipoPlan").asText("Otro")))
                .requiereAutorizacionDefault(node.path("requiereAutorizacionPorDefecto").asBoolean(false))
                .vigenciaDesde(null)
                .vigenciaHasta(null)
                .activo(node.path("planActivo").asBoolean(true))
                .build();
    }

    private TipoFinanciador parseTipoFinanciador(String value) {
        return switch (value) {
            case "Obra Social" -> TipoFinanciador.OBRA_SOCIAL;
            case "PAMI"        -> TipoFinanciador.PAMI;
            case "Particular"  -> TipoFinanciador.PARTICULAR;
            case "Prepaga"     -> TipoFinanciador.PREPAGA;
            default            -> TipoFinanciador.OTRO;
        };
    }

    private TipoPlan parseTipoPlan(String value) {
        return switch (value) {
            case "PMO"       -> TipoPlan.PMO;
            case "Básico"    -> TipoPlan.BASICO;
            case "Superador" -> TipoPlan.SUPERADOR;
            case "Comercial" -> TipoPlan.COMERCIAL;
            default          -> TipoPlan.OTRO;
        };
    }

    private String nullableText(JsonNode node, String field) {
        JsonNode child = node.get(field);
        if (child == null || child.isNull()) return null;
        return child.asText();
    }
}
