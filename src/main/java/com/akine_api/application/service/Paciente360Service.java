package com.akine_api.application.service;

import com.akine_api.application.dto.result.Paciente360AtencionesResult;
import com.akine_api.application.dto.result.Paciente360DiagnosticosResult;
import com.akine_api.application.dto.result.Paciente360HeaderResult;
import com.akine_api.application.dto.result.Paciente360HistoriaClinicaResult;
import com.akine_api.application.dto.result.Paciente360ObraSocialResult;
import com.akine_api.application.dto.result.Paciente360PagosResult;
import com.akine_api.application.dto.result.Paciente360SummaryResult;
import com.akine_api.application.dto.result.Paciente360TurnosResult;
import com.akine_api.application.port.output.DiagnosticoClinicoRepositoryPort;
import com.akine_api.application.port.output.BoxRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.ObraSocialRepositoryPort;
import com.akine_api.application.port.output.PacienteConsultorioRepositoryPort;
import com.akine_api.application.port.output.PacienteRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.SesionClinicaRepositoryPort;
import com.akine_api.application.port.output.TurnoRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.PacienteNotFoundException;
import com.akine_api.domain.model.Box;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.domain.model.DiagnosticoClinico;
import com.akine_api.domain.model.DiagnosticoClinicoEstado;
import com.akine_api.domain.model.ObraSocial;
import com.akine_api.domain.model.ObraSocialEstado;
import com.akine_api.domain.model.ObraSocialPlan;
import com.akine_api.domain.model.Paciente;
import com.akine_api.domain.model.Profesional;
import com.akine_api.domain.model.SesionClinica;
import com.akine_api.domain.model.TipoConsulta;
import com.akine_api.domain.model.Turno;
import com.akine_api.domain.model.TurnoEstado;
import com.akine_api.domain.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class Paciente360Service {

    private static final LocalDateTime HISTORY_MIN = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final LocalDateTime HISTORY_MAX = LocalDateTime.of(2100, 1, 1, 0, 0);

    private final PacienteRepositoryPort pacienteRepo;
    private final PacienteConsultorioRepositoryPort pacienteConsultorioRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final UserRepositoryPort userRepo;
    private final TurnoRepositoryPort turnoRepo;
    private final ProfesionalRepositoryPort profesionalRepo;
    private final BoxRepositoryPort boxRepo;
    private final ObraSocialRepositoryPort obraSocialRepo;
    private final SesionClinicaRepositoryPort sesionClinicaRepo;
    private final DiagnosticoClinicoRepositoryPort diagnosticoClinicoRepo;

    public Paciente360Service(PacienteRepositoryPort pacienteRepo,
                              PacienteConsultorioRepositoryPort pacienteConsultorioRepo,
                              ConsultorioRepositoryPort consultorioRepo,
                              UserRepositoryPort userRepo,
                              TurnoRepositoryPort turnoRepo,
                              ProfesionalRepositoryPort profesionalRepo,
                              BoxRepositoryPort boxRepo,
                              ObraSocialRepositoryPort obraSocialRepo,
                              SesionClinicaRepositoryPort sesionClinicaRepo,
                              DiagnosticoClinicoRepositoryPort diagnosticoClinicoRepo) {
        this.pacienteRepo = pacienteRepo;
        this.pacienteConsultorioRepo = pacienteConsultorioRepo;
        this.consultorioRepo = consultorioRepo;
        this.userRepo = userRepo;
        this.turnoRepo = turnoRepo;
        this.profesionalRepo = profesionalRepo;
        this.boxRepo = boxRepo;
        this.obraSocialRepo = obraSocialRepo;
        this.sesionClinicaRepo = sesionClinicaRepo;
        this.diagnosticoClinicoRepo = diagnosticoClinicoRepo;
    }

    public Paciente360HeaderResult getHeader(UUID consultorioId,
                                             UUID pacienteId,
                                             String userEmail,
                                             Set<String> roles) {
        Paciente paciente = loadPacienteWithAccess(consultorioId, pacienteId, userEmail, roles, TabScope.GENERAL);
        CoverageInsight coverage = resolveCoverageInsight(
                consultorioId,
                paciente,
                findAllTurnosPaciente(consultorioId, pacienteId)
        );

        return new Paciente360HeaderResult(
                paciente.getId(),
                consultorioId,
                paciente.getDni(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getTelefono(),
                paciente.getEmail(),
                paciente.getFechaNacimiento(),
                paciente.getSexo(),
                paciente.getDomicilio(),
                paciente.getNacionalidad(),
                paciente.getEstadoCivil(),
                paciente.getProfesiones(),
                paciente.getObraSocialNombre(),
                paciente.getObraSocialPlan(),
                paciente.getObraSocialNroAfiliado(),
                paciente.isActivo(),
                coverage.vigente(),
                coverage.resumen(),
                paciente.getCreatedAt(),
                paciente.getUpdatedAt()
        );
    }

    public Paciente360SummaryResult getSummary(UUID consultorioId,
                                               UUID pacienteId,
                                               String userEmail,
                                               Set<String> roles) {
        Paciente paciente = loadPacienteWithAccess(consultorioId, pacienteId, userEmail, roles, TabScope.GENERAL);
        List<Turno> turnos = findAllTurnosPaciente(consultorioId, pacienteId);
        List<SesionClinica> sesionesClinicas = findAllSesionesPaciente(consultorioId, pacienteId);
        List<DiagnosticoClinico> diagnosticos = findAllDiagnosticosPaciente(consultorioId, pacienteId);
        Map<UUID, Profesional> profesionales = buildProfesionalMap(consultorioId);
        CoverageInsight coverage = resolveCoverageInsight(consultorioId, paciente, turnos);

        Turno nextTurno = turnos.stream()
                .filter(this::isFutureScheduled)
                .min(Comparator.comparing(Turno::getFechaHoraInicio))
                .orElse(null);
        Turno lastSession = turnos.stream()
                .filter(this::isSessionTurno)
                .max(Comparator.comparing(Turno::getFechaHoraInicio))
                .orElse(null);
        SesionClinica lastClinicalSession = sesionesClinicas.stream()
                .max(Comparator.comparing(SesionClinica::getFechaAtencion))
                .orElse(null);
        LocalDateTime lastAttentionAt = lastClinicalSession != null
                ? lastClinicalSession.getFechaAtencion()
                : lastSession != null ? lastSession.getFechaHoraInicio() : null;

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDate.now()
                .with(TemporalAdjusters.lastDayOfMonth())
                .plusDays(1)
                .atStartOfDay();
        long sesionesMes = sesionesClinicas.stream()
                .filter(t -> !t.getFechaAtencion().isBefore(monthStart) && t.getFechaAtencion().isBefore(monthEnd))
                .count();
        int diagnosticosActivos = Math.toIntExact(diagnosticos.stream()
                .filter(d -> d.getEstado() == DiagnosticoClinicoEstado.ACTIVO)
                .count());

        List<Paciente360SummaryResult.AlertItem> alertas = buildSummaryAlerts(paciente, nextTurno, lastAttentionAt, coverage);
        List<Paciente360SummaryResult.ActionItem> acciones = buildSummaryActions(nextTurno, lastAttentionAt != null, coverage);
        List<Paciente360SummaryResult.ActivityItem> actividad = turnos.stream()
                .sorted(Comparator.comparing(Turno::getFechaHoraInicio).reversed())
                .limit(5)
                .map(turno -> new Paciente360SummaryResult.ActivityItem(
                        turno.getId().toString(),
                        activityType(turno),
                        activityTitle(turno),
                        activityDetail(turno, profesionales.get(turno.getProfesionalId())),
                        turno.getFechaHoraInicio(),
                        activityRoute(turno)
                ))
                .toList();

        return new Paciente360SummaryResult(
                new Paciente360SummaryResult.Kpis(
                        nextTurno != null ? nextTurno.getFechaHoraInicio() : null,
                        fullName(profesionales.get(nextTurno != null ? nextTurno.getProfesionalId() : null)),
                        nextTurno != null ? turnoLabel(nextTurno.getEstado()) : null,
                        lastClinicalSession != null ? lastClinicalSession.getFechaAtencion() : null,
                        fullName(profesionales.get(lastClinicalSession != null ? lastClinicalSession.getProfesionalId() : null)),
                        lastClinicalSession != null
                                ? fallbackText(lastClinicalSession.getResumenClinico(), lastClinicalSession.getMotivoConsulta(), "Sesion registrada")
                                : null,
                        diagnosticosActivos,
                        sesionesMes,
                        coverage.resumen(),
                        BigDecimal.ZERO
                ),
                alertas,
                acciones,
                actividad
        );
    }

    public Paciente360HistoriaClinicaResult getHistoriaClinica(UUID consultorioId,
                                                               UUID pacienteId,
                                                               String tipo,
                                                               UUID profesionalId,
                                                               LocalDate from,
                                                               LocalDate to,
                                                               int page,
                                                               int size,
                                                               String userEmail,
                                                               Set<String> roles) {
        loadPacienteWithAccess(consultorioId, pacienteId, userEmail, roles, TabScope.CLINICAL);
        List<SesionClinica> sesiones = findAllSesionesPaciente(consultorioId, pacienteId);
        Map<UUID, Profesional> profesionales = buildProfesionalMap(consultorioId);

        List<Paciente360HistoriaClinicaResult.Item> items = sesiones.stream()
                .filter(t -> tipo == null || tipo.isBlank() || "SESION".equalsIgnoreCase(tipo))
                .filter(t -> profesionalId == null || profesionalId.equals(t.getProfesionalId()))
                .filter(t -> from == null || !t.getFechaAtencion().toLocalDate().isBefore(from))
                .filter(t -> to == null || !t.getFechaAtencion().toLocalDate().isAfter(to))
                .sorted(Comparator.comparing(SesionClinica::getFechaAtencion).reversed())
                .map(t -> new Paciente360HistoriaClinicaResult.Item(
                        t.getId().toString(),
                        t.getFechaAtencion(),
                        t.getProfesionalId(),
                        fullName(profesionales.get(t.getProfesionalId())),
                        "SESION",
                        fallbackText(t.getResumenClinico(), t.getMotivoConsulta(), "Sesion clinica"),
                        fallbackText(t.getEvaluacion(), t.getSubjetivo(), fallbackText(t.getObjetivo(), t.getPlan(), "Sin detalle ampliado.")),
                        t.getTurnoId() != null ? t.getTurnoId().toString() : null,
                        t.getUpdatedAt()
                ))
                .toList();

        return new Paciente360HistoriaClinicaResult(
                buildHistoriaProfesionales(items, profesionales),
                paginate(items, page, size),
                safePage(page),
                safeSize(size),
                items.size()
        );
    }

    public Paciente360DiagnosticosResult getDiagnosticos(UUID consultorioId,
                                                         UUID pacienteId,
                                                         int page,
                                                         int size,
                                                         String userEmail,
                                                         Set<String> roles) {
        loadPacienteWithAccess(consultorioId, pacienteId, userEmail, roles, TabScope.CLINICAL);
        Map<UUID, Profesional> profesionales = buildProfesionalMap(consultorioId);
        Map<UUID, SesionClinica> sesiones = findAllSesionesPaciente(consultorioId, pacienteId).stream()
                .collect(Collectors.toMap(SesionClinica::getId, s -> s, (left, right) -> left));
        List<DiagnosticoClinico> diagnosticos = findAllDiagnosticosPaciente(consultorioId, pacienteId).stream()
                .sorted(Comparator.comparing((DiagnosticoClinico d) -> d.getEstado() == DiagnosticoClinicoEstado.ACTIVO).reversed()
                        .thenComparing(DiagnosticoClinico::getFechaInicio, Comparator.reverseOrder()))
                .toList();

        List<Paciente360DiagnosticosResult.Item> items = diagnosticos.stream()
                .map(d -> new Paciente360DiagnosticosResult.Item(
                        d.getId(),
                        d.getDescripcion(),
                        d.getEstado().name(),
                        d.getFechaInicio(),
                        d.getFechaFin(),
                        d.getProfesionalId(),
                        fullName(profesionales.get(d.getProfesionalId())),
                        d.getNotas(),
                        d.getSesionId() != null && sesiones.containsKey(d.getSesionId())
                                ? fallbackText(sesiones.get(d.getSesionId()).getResumenClinico(),
                                sesiones.get(d.getSesionId()).getMotivoConsulta(),
                                "Sin resumen de atencion")
                                : null
                ))
                .toList();

        return new Paciente360DiagnosticosResult(
                diagnosticos.stream().filter(d -> d.getEstado() == DiagnosticoClinicoEstado.ACTIVO).count(),
                diagnosticos.stream().map(DiagnosticoClinico::getFechaInicio).max(LocalDate::compareTo).orElse(null),
                paginate(items, page, size),
                safePage(page),
                safeSize(size),
                items.size()
        );
    }

    public Paciente360AtencionesResult getAtenciones(UUID consultorioId,
                                                     UUID pacienteId,
                                                     UUID profesionalId,
                                                     LocalDate from,
                                                     LocalDate to,
                                                     int page,
                                                     int size,
                                                     String userEmail,
                                                     Set<String> roles) {
        loadPacienteWithAccess(consultorioId, pacienteId, userEmail, roles, TabScope.CLINICAL);
        List<SesionClinica> sesiones = findAllSesionesPaciente(consultorioId, pacienteId).stream()
                .filter(t -> profesionalId == null || profesionalId.equals(t.getProfesionalId()))
                .filter(t -> from == null || !t.getFechaAtencion().toLocalDate().isBefore(from))
                .filter(t -> to == null || !t.getFechaAtencion().toLocalDate().isAfter(to))
                .sorted(Comparator.comparing(SesionClinica::getFechaAtencion).reversed())
                .toList();
        Map<UUID, Profesional> profesionales = buildProfesionalMap(consultorioId);
        Map<UUID, Box> boxes = buildBoxMap(consultorioId);
        Consultorio consultorio = consultorioRepo.findById(consultorioId)
                .orElseThrow(() -> new PacienteNotFoundException("Consultorio no encontrado"));

        List<Paciente360AtencionesResult.Item> items = sesiones.stream()
                .map(t -> new Paciente360AtencionesResult.Item(
                        t.getId(),
                        t.getFechaAtencion(),
                        t.getProfesionalId(),
                        fullName(profesionales.get(t.getProfesionalId())),
                        consultorio.getName(),
                        boxes.containsKey(t.getBoxId()) ? boxes.get(t.getBoxId()).getNombre() : null,
                        sessionStatusLabel(t.getEstado()),
                        fallbackText(t.getResumenClinico(), t.getMotivoConsulta(), "Atencion registrada"),
                        t.getTurnoId()
                ))
                .toList();

        return new Paciente360AtencionesResult(
                items.size(),
                items.isEmpty() ? null : items.get(0).fecha(),
                buildAtencionesProfesionales(items, profesionales),
                paginate(items, page, size),
                safePage(page),
                safeSize(size)
        );
    }

    public Paciente360TurnosResult getTurnos(UUID consultorioId,
                                             UUID pacienteId,
                                             String scope,
                                             UUID profesionalId,
                                             String estado,
                                             LocalDate from,
                                             LocalDate to,
                                             int page,
                                             int size,
                                             String userEmail,
                                             Set<String> roles) {
        loadPacienteWithAccess(consultorioId, pacienteId, userEmail, roles, TabScope.GENERAL);
        String safeScope = scope == null || scope.isBlank()
                ? "PROXIMOS"
                : scope.trim().toUpperCase(Locale.ROOT);

        List<Turno> allTurnos = findAllTurnosPaciente(consultorioId, pacienteId);
        Map<UUID, Profesional> profesionales = buildProfesionalMap(consultorioId);
        Map<UUID, Box> boxes = buildBoxMap(consultorioId);

        List<Turno> filtered = allTurnos.stream()
                .filter(t -> profesionalId == null || profesionalId.equals(t.getProfesionalId()))
                .filter(t -> estado == null || estado.isBlank() || t.getEstado().name().equalsIgnoreCase(estado))
                .filter(t -> from == null || !t.getFechaHoraInicio().toLocalDate().isBefore(from))
                .filter(t -> to == null || !t.getFechaHoraInicio().toLocalDate().isAfter(to))
                .filter(t -> filterTurnoScope(t, safeScope))
                .sorted(Comparator.comparing(Turno::getFechaHoraInicio).reversed())
                .toList();

        List<Paciente360TurnosResult.Item> items = filtered.stream()
                .map(t -> new Paciente360TurnosResult.Item(
                        t.getId(),
                        t.getFechaHoraInicio(),
                        t.getFechaHoraFin(),
                        t.getProfesionalId(),
                        fullName(profesionales.get(t.getProfesionalId())),
                        boxes.containsKey(t.getBoxId()) ? boxes.get(t.getBoxId()).getNombre() : null,
                        turnoLabel(t.getEstado()),
                        t.getTipoConsulta() != null ? t.getTipoConsulta().name() : null,
                        t.getMotivoConsulta(),
                        "Backoffice",
                        turnoAlert(t)
                ))
                .toList();

        return new Paciente360TurnosResult(
                safeScope,
                allTurnos.stream().filter(this::isFutureScheduled).count(),
                allTurnos.stream().filter(t -> t.getFechaHoraInicio().isBefore(LocalDateTime.now())).count(),
                allTurnos.stream().filter(t -> t.getEstado() == TurnoEstado.CANCELADO || t.getEstado() == TurnoEstado.AUSENTE).count(),
                buildTurnosProfesionales(allTurnos, profesionales),
                paginate(items, page, size),
                safePage(page),
                safeSize(size),
                items.size()
        );
    }

    public Paciente360ObraSocialResult getObraSocial(UUID consultorioId,
                                                     UUID pacienteId,
                                                     String userEmail,
                                                     Set<String> roles) {
        Paciente paciente = loadPacienteWithAccess(consultorioId, pacienteId, userEmail, roles, TabScope.GENERAL);
        CoverageInsight coverage = resolveCoverageInsight(
                consultorioId,
                paciente,
                findAllTurnosPaciente(consultorioId, pacienteId)
        );

        return new Paciente360ObraSocialResult(
                new Paciente360ObraSocialResult.Overview(
                        paciente.getObraSocialNombre(),
                        paciente.getObraSocialPlan(),
                        paciente.getObraSocialNroAfiliado(),
                        coverage.vigente(),
                        null,
                        coverage.tipoCobertura(),
                        coverage.valorCobertura(),
                        coverage.tipoCoseguro(),
                        coverage.valorCoseguro(),
                        coverage.observaciones()
                ),
                new Paciente360ObraSocialResult.Coverage(
                        coverage.prestacionesSinAutorizacion(),
                        coverage.sesionesUsadasMes(),
                        coverage.sesionesDisponibles(),
                        coverage.autorizacionRequerida(),
                        coverage.resumen()
                ),
                List.of()
        );
    }

    public Paciente360PagosResult getPagos(UUID consultorioId,
                                           UUID pacienteId,
                                           int page,
                                           int size,
                                           String userEmail,
                                           Set<String> roles) {
        loadPacienteWithAccess(consultorioId, pacienteId, userEmail, roles, TabScope.FINANCIAL);
        return new Paciente360PagosResult(
                new Paciente360PagosResult.Summary(
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        null,
                        BigDecimal.ZERO
                ),
                List.of(),
                List.of(new Paciente360PagosResult.ConciliationItem(
                        "sin-movimientos",
                        "OK",
                        "Todavia no hay movimientos de caja o cobranzas vinculados a este paciente."
                )),
                safePage(page),
                safeSize(size),
                0
        );
    }

    private List<Paciente360SummaryResult.AlertItem> buildSummaryAlerts(Paciente paciente,
                                                                        Turno nextTurno,
                                                                        LocalDateTime lastAttentionAt,
                                                                        CoverageInsight coverage) {
        List<Paciente360SummaryResult.AlertItem> alertas = new ArrayList<>();
        if (paciente.getObraSocialNombre() == null || paciente.getObraSocialNombre().isBlank()) {
            alertas.add(new Paciente360SummaryResult.AlertItem("warning", "Paciente sin obra social registrada.", "../obra-social"));
        } else if (!coverage.planEncontrado()) {
            alertas.add(new Paciente360SummaryResult.AlertItem("info", "La cobertura no coincide con un convenio activo del consultorio.", "../obra-social"));
        }
        if (nextTurno == null) {
            alertas.add(new Paciente360SummaryResult.AlertItem("info", "No hay turnos proximos programados.", "../turnos"));
        }
        if (lastAttentionAt != null && lastAttentionAt.isBefore(LocalDateTime.now().minusDays(60))) {
            alertas.add(new Paciente360SummaryResult.AlertItem("warning", "Pasaron mas de 60 dias desde la ultima atencion registrada.", "../atenciones"));
        }
        if (coverage.autorizacionRequerida()) {
            alertas.add(new Paciente360SummaryResult.AlertItem("warning", "La cobertura requiere autorizacion o ya consumio las prestaciones sin autorizacion.", "../obra-social"));
        }
        if (alertas.isEmpty()) {
            alertas.add(new Paciente360SummaryResult.AlertItem("info", "No hay alertas operativas para este paciente.", "../resumen"));
        }
        return alertas;
    }

    private List<Paciente360SummaryResult.ActionItem> buildSummaryActions(Turno nextTurno,
                                                                          boolean hasClinicalHistory,
                                                                          CoverageInsight coverage) {
        List<Paciente360SummaryResult.ActionItem> acciones = new ArrayList<>();
        if (nextTurno != null) {
            acciones.add(new Paciente360SummaryResult.ActionItem(
                    "turno",
                    "Revisar proximo turno",
                    "../turnos",
                    nextTurno.getFechaHoraInicio()
            ));
        } else {
            acciones.add(new Paciente360SummaryResult.ActionItem("turno", "Programar nuevo turno", "../turnos", null));
        }
        if (coverage.autorizacionRequerida() || !coverage.planEncontrado()) {
            acciones.add(new Paciente360SummaryResult.ActionItem(
                    "cobertura",
                    "Validar cobertura y autorizacion",
                    "../obra-social",
                    null
            ));
        }
        if (!hasClinicalHistory) {
            acciones.add(new Paciente360SummaryResult.ActionItem(
                    "historia",
                    "Registrar primera atencion",
                    "../historia-clinica",
                    null
            ));
        }
        return acciones;
    }

    private Paciente loadPacienteWithAccess(UUID consultorioId,
                                            UUID pacienteId,
                                            String userEmail,
                                            Set<String> roles,
                                            TabScope scope) {
        assertCanAccess(consultorioId, userEmail, roles, scope);
        Paciente paciente = pacienteRepo.findById(pacienteId)
                .orElseThrow(() -> new PacienteNotFoundException("Paciente no encontrado"));
        if (!pacienteConsultorioRepo.existsByPacienteIdAndConsultorioId(pacienteId, consultorioId)) {
            throw new PacienteNotFoundException("Paciente no encontrado en este consultorio");
        }
        return paciente;
    }

    private void assertCanAccess(UUID consultorioId,
                                 String userEmail,
                                 Set<String> roles,
                                 TabScope scope) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
        if (!hasRequiredRole(roles, scope)) {
            throw new AccessDeniedException("Permiso denegado");
        }
        if (roles.contains("ROLE_ADMIN")) {
            return;
        }
        UUID userId = resolveUserId(userEmail);
        if (!consultorioRepo.findConsultorioIdsByUserId(userId).contains(consultorioId)) {
            throw new AccessDeniedException("Sin acceso a este consultorio");
        }
    }

    private boolean hasRequiredRole(Set<String> roles, TabScope scope) {
        return switch (scope) {
            case GENERAL -> roles.contains("ROLE_ADMIN")
                    || roles.contains("ROLE_PROFESIONAL_ADMIN")
                    || roles.contains("ROLE_ADMINISTRATIVO")
                    || roles.contains("ROLE_PROFESIONAL");
            case CLINICAL -> roles.contains("ROLE_ADMIN")
                    || roles.contains("ROLE_PROFESIONAL_ADMIN")
                    || roles.contains("ROLE_PROFESIONAL");
            case FINANCIAL -> roles.contains("ROLE_ADMIN");
        };
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private List<Turno> findAllTurnosPaciente(UUID consultorioId, UUID pacienteId) {
        return turnoRepo.findByPacienteIdAndRange(pacienteId, HISTORY_MIN, HISTORY_MAX).stream()
                .filter(t -> consultorioId.equals(t.getConsultorioId()))
                .sorted(Comparator.comparing(Turno::getFechaHoraInicio))
                .toList();
    }

    private List<SesionClinica> findAllSesionesPaciente(UUID consultorioId, UUID pacienteId) {
        return sesionClinicaRepo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId).stream()
                .sorted(Comparator.comparing(SesionClinica::getFechaAtencion))
                .toList();
    }

    private List<DiagnosticoClinico> findAllDiagnosticosPaciente(UUID consultorioId, UUID pacienteId) {
        return diagnosticoClinicoRepo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId);
    }

    private Map<UUID, Profesional> buildProfesionalMap(UUID consultorioId) {
        return profesionalRepo.findByConsultorioId(consultorioId).stream()
                .collect(Collectors.toMap(Profesional::getId, p -> p, (left, right) -> left));
    }

    private Map<UUID, Box> buildBoxMap(UUID consultorioId) {
        return boxRepo.findByConsultorioId(consultorioId).stream()
                .collect(Collectors.toMap(Box::getId, b -> b, (left, right) -> left));
    }

    private List<Paciente360HistoriaClinicaResult.ProfesionalOption> buildHistoriaProfesionales(
            List<Paciente360HistoriaClinicaResult.Item> items,
            Map<UUID, Profesional> profesionales
    ) {
        return items.stream()
                .map(Paciente360HistoriaClinicaResult.Item::profesionalId)
                .filter(Objects::nonNull)
                .distinct()
                .map(profesionales::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(this::fullName, String.CASE_INSENSITIVE_ORDER))
                .map(p -> new Paciente360HistoriaClinicaResult.ProfesionalOption(p.getId(), fullName(p)))
                .toList();
    }

    private List<Paciente360AtencionesResult.ProfesionalOption> buildAtencionesProfesionales(
            List<Paciente360AtencionesResult.Item> items,
            Map<UUID, Profesional> profesionales
    ) {
        return items.stream()
                .map(Paciente360AtencionesResult.Item::profesionalId)
                .filter(Objects::nonNull)
                .distinct()
                .map(profesionales::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(this::fullName, String.CASE_INSENSITIVE_ORDER))
                .map(p -> new Paciente360AtencionesResult.ProfesionalOption(p.getId(), fullName(p)))
                .toList();
    }

    private List<Paciente360TurnosResult.ProfesionalOption> buildTurnosProfesionales(
            List<Turno> turnos,
            Map<UUID, Profesional> profesionales
    ) {
        return turnos.stream()
                .map(Turno::getProfesionalId)
                .filter(Objects::nonNull)
                .distinct()
                .map(profesionales::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(this::fullName, String.CASE_INSENSITIVE_ORDER))
                .map(p -> new Paciente360TurnosResult.ProfesionalOption(p.getId(), fullName(p)))
                .toList();
    }

    private <T> List<T> paginate(List<T> items, int page, int size) {
        int safePage = safePage(page);
        int safeSize = safeSize(size);
        int fromIndex = safePage * safeSize;
        if (fromIndex >= items.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + safeSize, items.size());
        return items.subList(fromIndex, toIndex);
    }

    private int safePage(int page) {
        return Math.max(page, 0);
    }

    private int safeSize(int size) {
        return size <= 0 ? 20 : Math.min(size, 100);
    }

    private boolean isFutureScheduled(Turno turno) {
        return turno.getFechaHoraInicio().isAfter(LocalDateTime.now())
                && turno.getEstado() != TurnoEstado.CANCELADO
                && turno.getEstado() != TurnoEstado.AUSENTE;
    }

    private boolean isSessionTurno(Turno turno) {
        return turno.getEstado() == TurnoEstado.COMPLETADO || turno.getEstado() == TurnoEstado.EN_CURSO;
    }

    private boolean filterTurnoScope(Turno turno, String scope) {
        if ("HISTORICOS".equals(scope)) {
            return turno.getFechaHoraInicio().isBefore(LocalDateTime.now());
        }
        if ("PROXIMOS".equals(scope)) {
            return isFutureScheduled(turno);
        }
        return true;
    }

    private String activityType(Turno turno) {
        if (turno.getEstado() == TurnoEstado.CANCELADO || turno.getEstado() == TurnoEstado.AUSENTE) {
            return "ALERTA";
        }
        if (turno.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            return "TURNO";
        }
        return "ATENCION";
    }

    private String activityTitle(Turno turno) {
        if (turno.getEstado() == TurnoEstado.CANCELADO) {
            return "Turno cancelado";
        }
        if (turno.getEstado() == TurnoEstado.AUSENTE) {
            return "Paciente ausente";
        }
        if (turno.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            return "Proximo turno";
        }
        return "Atencion registrada";
    }

    private String activityDetail(Turno turno, Profesional profesional) {
        String profesionalNombre = fullName(profesional);
        return fallbackText(
                turno.getMotivoConsulta(),
                turno.getNotas(),
                profesionalNombre != null ? "Profesional: " + profesionalNombre : "Sin detalle adicional"
        );
    }

    private String activityRoute(Turno turno) {
        return turno.getFechaHoraInicio().isAfter(LocalDateTime.now()) ? "../turnos" : "../atenciones";
    }

    private String turnoAlert(Turno turno) {
        if (turno.getEstado() == TurnoEstado.AUSENTE) {
            return "Ausencia";
        }
        if (turno.getEstado() == TurnoEstado.CANCELADO) {
            return "Cancelado";
        }
        return null;
    }

    private String turnoLabel(TurnoEstado estado) {
        if (estado == null) {
            return null;
        }
        return switch (estado) {
            case PROGRAMADO -> "Programado";
            case CONFIRMADO -> "Confirmado";
            case EN_ESPERA -> "En espera";
            case EN_CURSO -> "En curso";
            case COMPLETADO -> "Completado";
            case CANCELADO -> "Cancelado";
            case AUSENTE -> "Ausente";
        };
    }

    private String sessionStatusLabel(com.akine_api.domain.model.HistoriaClinicaSesionEstado estado) {
        if (estado == null) {
            return null;
        }
        return switch (estado) {
            case BORRADOR -> "Borrador";
            case CERRADA -> "Cerrada";
            case ANULADA -> "Anulada";
        };
    }

    private String fallbackText(String primary, String secondary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        if (secondary != null && !secondary.isBlank()) {
            return secondary;
        }
        return fallback;
    }

    private String fullName(Profesional profesional) {
        if (profesional == null) {
            return null;
        }
        return (Optional.ofNullable(profesional.getNombre()).orElse("")
                + " "
                + Optional.ofNullable(profesional.getApellido()).orElse("")).trim();
    }

    private CoverageInsight resolveCoverageInsight(UUID consultorioId, Paciente paciente, List<Turno> turnos) {
        if (paciente.getObraSocialNombre() == null || paciente.getObraSocialNombre().isBlank()) {
            return new CoverageInsight(
                    false,
                    false,
                    "Sin cobertura registrada",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    0,
                    null,
                    false
            );
        }

        String obraSocialName = paciente.getObraSocialNombre();
        String planName = paciente.getObraSocialPlan();
        ObraSocial matched = obraSocialRepo.search(
                        consultorioId,
                        obraSocialName,
                        ObraSocialEstado.ACTIVE,
                        null,
                        PageRequest.of(0, 25)
                )
                .getContent()
                .stream()
                .filter(item -> normalizedEquals(item.acronimo(), obraSocialName)
                        || normalizedEquals(item.nombreCompleto(), obraSocialName))
                .findFirst()
                .flatMap(item -> obraSocialRepo.findByIdAndConsultorioId(item.id(), consultorioId))
                .orElse(null);

        ObraSocialPlan plan = null;
        if (matched != null && planName != null && !planName.isBlank()) {
            plan = matched.getPlanes().stream()
                    .filter(ObraSocialPlan::isActivo)
                    .filter(p -> normalizedEquals(p.getNombreCorto(), planName)
                            || normalizedEquals(p.getNombreCompleto(), planName))
                    .findFirst()
                    .orElse(null);
        }

        long sesionesUsadasMes = turnos.stream()
                .filter(this::isSessionTurno)
                .filter(t -> t.getTipoConsulta() == TipoConsulta.OBRA_SOCIAL)
                .filter(t -> t.getFechaHoraInicio().toLocalDate().getMonth() == LocalDate.now().getMonth()
                        && t.getFechaHoraInicio().toLocalDate().getYear() == LocalDate.now().getYear())
                .count();

        Integer prestaciones = plan != null ? plan.getPrestacionesSinAutorizacion() : null;
        Integer disponibles = prestaciones != null ? Math.max(prestaciones - Math.toIntExact(sesionesUsadasMes), 0) : null;
        boolean autorizacionRequerida = prestaciones != null && sesionesUsadasMes >= prestaciones;

        String resumen = plan == null
                ? "Cobertura informada por el paciente sin convenio exacto asociado"
                : "Cobertura vigente"
                + (disponibles != null ? " - " + disponibles + " prestaciones disponibles" : "");

        return new CoverageInsight(
                true,
                plan != null,
                resumen,
                plan != null ? plan.getTipoCobertura().name() : null,
                plan != null ? plan.getValorCobertura() : null,
                plan != null ? plan.getTipoCoseguro().name() : null,
                plan != null ? plan.getValorCoseguro() : null,
                plan != null ? plan.getPrestacionesSinAutorizacion() : null,
                disponibles,
                sesionesUsadasMes,
                plan != null ? plan.getObservaciones() : null,
                autorizacionRequerida
        );
    }

    private boolean normalizedEquals(String left, String right) {
        return normalizeKey(left).equals(normalizeKey(right));
    }

    private String normalizeKey(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }

    private enum TabScope {
        GENERAL,
        CLINICAL,
        FINANCIAL
    }

    private record CoverageInsight(
            boolean vigente,
            boolean planEncontrado,
            String resumen,
            String tipoCobertura,
            BigDecimal valorCobertura,
            String tipoCoseguro,
            BigDecimal valorCoseguro,
            Integer prestacionesSinAutorizacion,
            Integer sesionesDisponibles,
            long sesionesUsadasMes,
            String observaciones,
            boolean autorizacionRequerida
    ) {}
}
