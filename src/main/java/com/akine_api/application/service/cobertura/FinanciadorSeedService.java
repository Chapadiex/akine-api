package com.akine_api.application.service.cobertura;

import com.akine_api.domain.model.cobertura.FinanciadorSalud;
import com.akine_api.domain.model.cobertura.PlanFinanciador;
import com.akine_api.domain.model.cobertura.TipoFinanciador;
import com.akine_api.domain.model.cobertura.TipoPlan;
import com.akine_api.domain.model.convenio.Arancel;
import com.akine_api.domain.model.convenio.Convenio;
import com.akine_api.domain.model.convenio.ConvenioVersion;
import com.akine_api.domain.model.convenio.ConvenioVersionEstado;
import com.akine_api.domain.model.convenio.CoseguroTipo;
import com.akine_api.domain.model.convenio.ModalidadConvenio;
import com.akine_api.domain.repository.cobertura.FinanciadorSaludRepositoryPort;
import com.akine_api.domain.repository.cobertura.PlanFinanciadorRepositoryPort;
import com.akine_api.domain.repository.convenio.ArancelRepositoryPort;
import com.akine_api.domain.repository.convenio.ConvenioRepositoryPort;
import com.akine_api.domain.repository.convenio.ConvenioVersionRepositoryPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanciadorSeedService {

    private final FinanciadorSaludRepositoryPort financiadorRepo;
    private final PlanFinanciadorRepositoryPort planRepo;
    private final ConvenioRepositoryPort convenioRepo;
    private final ConvenioVersionRepositoryPort convenioVersionRepo;
    private final ArancelRepositoryPort arancelRepo;
    private final ObjectMapper objectMapper;

    /** Porcentajes de cobertura típicos por sigla de financiador. Resto: 60 por defecto. */
    private static final Map<String, Integer> COBERTURA_PCT;
    static {
        Map<String, Integer> m = new HashMap<>();
        m.put("PARTICULAR", 0);
        m.put("PAMI", 100);
        m.put("OMINT", 75);
        m.put("SMG", 70);
        m.put("OSDE", 70);
        m.put("JERARQUICOS", 65);
        m.put("GALENO", 60);
        m.put("MEDIFE", 60);
        m.put("MEDICUS", 60);
        m.put("DOCTHOS", 60);
        m.put("UOMRA", 60);
        m.put("OSECAC", 60);
        m.put("SMATA", 60);
        m.put("UPCN", 60);
        m.put("OSPGRA", 60);
        m.put("LUIS PASTEUR", 60);
        m.put("PASTEUR", 60);
        m.put("SANCOR", 55);
        m.put("SANCOR EMP", 55);
        m.put("OSPEDYC", 55);
        m.put("OSUTHGRA", 55);
        m.put("SUTERH", 55);
        m.put("ACCORD", 50);
        m.put("IOMA", 50);
        m.put("OSDEPYM", 50);
        COBERTURA_PCT = Collections.unmodifiableMap(m);
    }

    /** Compatibilidad retroactiva — sin aranceles. */
    @Transactional
    public void seedForConsultorio(UUID consultorioId) {
        seedForConsultorio(consultorioId, null);
    }

    @Transactional
    public void seedForConsultorio(UUID consultorioId, BigDecimal precioLista) {
        try {
            ClassPathResource resource = new ClassPathResource("cobertura/financiadores_seed.json");
            try (InputStream is = resource.getInputStream()) {
                JsonNode root = objectMapper.readTree(is);
                JsonNode financiadores = root.get("financiadores");
                if (financiadores == null || !financiadores.isArray()) {
                    log.warn("Seed JSON sin array 'financiadores'");
                    return;
                }

                // Pre-cargar estado existente para idempotencia eficiente
                Map<String, FinanciadorSalud> existingByNombre = financiadorRepo
                        .findAllByConsultorioId(consultorioId)
                        .stream()
                        .collect(Collectors.toMap(FinanciadorSalud::getNombre, f -> f));

                Map<UUID, Convenio> convenioByFinanciadorId = convenioRepo
                        .findByConsultorioId(consultorioId)
                        .stream()
                        .collect(Collectors.toMap(Convenio::getFinanciadorId, c -> c));

                int fsCreados = 0, fsOmitidos = 0, convCreados = 0, convOmitidos = 0;
                int arancelCreados = 0, arancelOmitidos = 0;
                LocalDate hoy = LocalDate.now();

                for (JsonNode node : financiadores) {
                    String nombreCompleto = node.path("nombreCompleto").asText();
                    String acronimo = nullableText(node, "nombreCortoAcronimo");

                    // 1. Financiador
                    FinanciadorSalud fs;
                    if (existingByNombre.containsKey(nombreCompleto)) {
                        fs = existingByNombre.get(nombreCompleto);
                        fsOmitidos++;
                    } else {
                        fs = financiadorRepo.save(parseFinanciador(node, consultorioId));
                        JsonNode planes = node.get("planes");
                        if (planes != null && planes.isArray()) {
                            for (JsonNode planNode : planes) {
                                planRepo.save(parsePlan(planNode, fs.getId()));
                            }
                        }
                        fsCreados++;
                    }

                    // 2. Convenio + Version
                    ConvenioVersion version;
                    if (!convenioByFinanciadorId.containsKey(fs.getId())) {
                        String sigla = acronimo != null ? acronimo : fs.getNombre();
                        Convenio convenio = Convenio.builder()
                                .consultorioId(consultorioId)
                                .financiadorId(fs.getId())
                                .siglaDisplay(sigla)
                                .modalidad(ModalidadConvenio.POR_SESION)
                                .requiereAut(false)
                                .requiereOrden(false)
                                .build();
                        Convenio savedConvenio = convenioRepo.save(convenio);
                        version = convenioVersionRepo.save(ConvenioVersion.builder()
                                .convenioId(savedConvenio.getId())
                                .versionNum(1)
                                .vigenciaDesde(hoy)
                                .vigenciaHasta(null)
                                .estado(ConvenioVersionEstado.VIGENTE)
                                .build());
                        convCreados++;
                    } else {
                        Convenio existingConvenio = convenioByFinanciadorId.get(fs.getId());
                        version = convenioVersionRepo.findVigenteByConvenioId(existingConvenio.getId()).orElse(null);
                        convOmitidos++;
                    }

                    // 3. Arancel (solo si se pasó precioLista y hay version vigente)
                    if (precioLista != null && precioLista.compareTo(BigDecimal.ZERO) > 0 && version != null) {
                        int pct = getCoberturaPct(acronimo);
                        if (pct == 0) {
                            // Particular: no genera arancel OS
                            log.debug("Financiador '{}' — sin arancel OS (Particular)", nombreCompleto);
                        } else if (arancelRepo.findByConvenioVersionId(version.getId()).isEmpty()) {
                            crearArancel(version.getId(), precioLista, pct, hoy);
                            log.debug("Financiador '{}' — arancel creado {}%", nombreCompleto, pct);
                            arancelCreados++;
                        } else {
                            arancelOmitidos++;
                        }
                    }
                }

                log.info("Seed consultorio {}: FS [{} creados / {} omitidos] · convenios [{} creados / {} omitidos] · aranceles [{} creados / {} omitidos]",
                        consultorioId, fsCreados, fsOmitidos, convCreados, convOmitidos, arancelCreados, arancelOmitidos);
            }
        } catch (Exception e) {
            log.error("Error al cargar seed para consultorio {}", consultorioId, e);
            throw new RuntimeException("Error al cargar financiadores por defecto", e);
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void crearArancel(UUID versionId, BigDecimal precioLista, int pct, LocalDate hoy) {
        BigDecimal osPaga = precioLista
                .multiply(BigDecimal.valueOf(pct))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal coseguro = precioLista.subtract(osPaga);

        CoseguroTipo coseguroTipo = pct >= 100 ? CoseguroTipo.NINGUNO : CoseguroTipo.FIJO;
        BigDecimal coseguroValor = pct >= 100 ? null : coseguro;

        arancelRepo.save(Arancel.builder()
                .convenioVersionId(versionId)
                .prestacionId(null)
                .prestacionCodigo(null)
                .prestacionNombre("Sesión de kinesiología")
                .importeOs(osPaga)
                .coseguroTipo(coseguroTipo)
                .coseguroValor(coseguroValor)
                .importeTotal(precioLista)
                .vigenciaDesde(hoy)
                .vigenciaHasta(null)
                .activo(true)
                .build());
    }

    private int getCoberturaPct(String acronimo) {
        if (acronimo == null) return 60;
        return COBERTURA_PCT.getOrDefault(acronimo.toUpperCase(), 60);
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
