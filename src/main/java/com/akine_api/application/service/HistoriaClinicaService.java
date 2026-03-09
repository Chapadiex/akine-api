package com.akine_api.application.service;

import com.akine_api.application.dto.command.ChangeSesionClinicaEstadoCommand;
import com.akine_api.application.dto.command.CreateAtencionInicialCommand;
import com.akine_api.application.dto.command.CreateHistoriaClinicaLegajoCommand;
import com.akine_api.application.dto.command.CreateDiagnosticoClinicoCommand;
import com.akine_api.application.dto.command.CreateSesionClinicaCommand;
import com.akine_api.application.dto.command.DiscardDiagnosticoClinicoCommand;
import com.akine_api.application.dto.command.HistoriaClinicaAntecedenteItemCommand;
import com.akine_api.application.dto.command.PlanTratamientoDetalleCommand;
import com.akine_api.application.dto.command.ResolveDiagnosticoClinicoCommand;
import com.akine_api.application.dto.command.UpdateHistoriaClinicaAntecedentesCommand;
import com.akine_api.application.dto.command.UpdateDiagnosticoClinicoCommand;
import com.akine_api.application.dto.command.UpdateSesionClinicaCommand;
import com.akine_api.application.dto.result.AdjuntoClinicoDownloadResult;
import com.akine_api.application.dto.result.AdjuntoClinicoResult;
import com.akine_api.application.dto.result.AtencionInicialEvaluacionResult;
import com.akine_api.application.dto.result.AtencionInicialSummaryResult;
import com.akine_api.application.dto.result.DiagnosticoClinicoResult;
import com.akine_api.application.dto.result.HistoriaClinicaActiveCaseSummaryResult;
import com.akine_api.application.dto.result.HistoriaClinicaAntecedenteResult;
import com.akine_api.application.dto.result.HistoriaClinicaLegajoStatusResult;
import com.akine_api.application.dto.result.HistoriaClinicaOverviewResult;
import com.akine_api.application.dto.result.HistoriaClinicaPacienteResult;
import com.akine_api.application.dto.result.HistoriaClinicaSesionSummaryResult;
import com.akine_api.application.dto.result.HistoriaClinicaTimelineEventResult;
import com.akine_api.application.dto.result.HistoriaClinicaWorkspaceItem;
import com.akine_api.application.dto.result.HistoriaClinicaWorkspaceResult;
import com.akine_api.application.dto.result.PlanTerapeuticoSummaryResult;
import com.akine_api.application.dto.result.PlanTratamientoDetalleResult;
import com.akine_api.application.dto.result.SesionClinicaResult;
import com.akine_api.application.port.output.AdjuntoClinicoRepositoryPort;
import com.akine_api.application.port.output.AtencionInicialEvaluacionRepositoryPort;
import com.akine_api.application.port.output.AtencionInicialRepositoryPort;
import com.akine_api.application.port.output.AttachmentStoragePort;
import com.akine_api.application.port.output.BoxRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.DiagnosticoClinicoRepositoryPort;
import com.akine_api.application.port.output.HistoriaClinicaAntecedenteRepositoryPort;
import com.akine_api.application.port.output.HistoriaClinicaLegajoRepositoryPort;
import com.akine_api.application.port.output.PacienteConsultorioRepositoryPort;
import com.akine_api.application.port.output.PacienteRepositoryPort;
import com.akine_api.application.port.output.PlanTerapeuticoRepositoryPort;
import com.akine_api.application.port.output.PlanTratamientoDetalleRepositoryPort;
import com.akine_api.application.port.output.ProfesionalConsultorioRepositoryPort;
import com.akine_api.application.port.output.ProfesionalRepositoryPort;
import com.akine_api.application.port.output.SesionClinicaRepositoryPort;
import com.akine_api.application.port.output.TurnoRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.AdjuntoClinicoNotFoundException;
import com.akine_api.domain.exception.DiagnosticoClinicoNotFoundException;
import com.akine_api.domain.exception.HistoriaClinicaConflictException;
import com.akine_api.domain.exception.HistoriaClinicaValidationException;
import com.akine_api.domain.exception.PacienteNotFoundException;
import com.akine_api.domain.exception.ProfesionalNotFoundException;
import com.akine_api.domain.exception.SesionClinicaNotFoundException;
import com.akine_api.domain.model.AdjuntoClinico;
import com.akine_api.domain.model.AtencionInicial;
import com.akine_api.domain.model.AtencionInicialEvaluacion;
import com.akine_api.domain.model.AtencionInicialTipoIngreso;
import com.akine_api.domain.model.Box;
import com.akine_api.domain.model.DiagnosticoClinico;
import com.akine_api.domain.model.DiagnosticoClinicoEstado;
import com.akine_api.domain.model.HistoriaClinicaAntecedente;
import com.akine_api.domain.model.HistoriaClinicaLegajo;
import com.akine_api.domain.model.HistoriaClinicaOrigenRegistro;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.HistoriaClinicaTimelineEventType;
import com.akine_api.domain.model.Paciente;
import com.akine_api.domain.model.PlanTerapeutico;
import com.akine_api.domain.model.PlanTerapeuticoEstado;
import com.akine_api.domain.model.PlanTratamientoDetalle;
import com.akine_api.domain.model.Profesional;
import com.akine_api.domain.model.ProfesionalConsultorio;
import com.akine_api.domain.model.SesionClinica;
import com.akine_api.domain.model.Turno;
import com.akine_api.domain.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class HistoriaClinicaService {

    private static final long MAX_ATTACHMENT_BYTES = 10L * 1024L * 1024L;
    private static final Set<String> ALLOWED_ATTACHMENT_EXTENSIONS = Set.of(".pdf", ".jpg", ".jpeg", ".png");

    private final SesionClinicaRepositoryPort sesionRepo;
    private final DiagnosticoClinicoRepositoryPort diagnosticoRepo;
    private final AdjuntoClinicoRepositoryPort adjuntoRepo;
    private final AtencionInicialRepositoryPort atencionInicialRepo;
    private final AtencionInicialEvaluacionRepositoryPort atencionEvaluacionRepo;
    private final HistoriaClinicaLegajoRepositoryPort legajoRepo;
    private final HistoriaClinicaAntecedenteRepositoryPort antecedenteRepo;
    private final PlanTerapeuticoRepositoryPort planTerapeuticoRepo;
    private final PlanTratamientoDetalleRepositoryPort planDetalleRepo;
    private final AttachmentStoragePort attachmentStorage;
    private final PacienteRepositoryPort pacienteRepo;
    private final PacienteConsultorioRepositoryPort pacienteConsultorioRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final ProfesionalRepositoryPort profesionalRepo;
    private final ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    private final TurnoRepositoryPort turnoRepo;
    private final UserRepositoryPort userRepo;
    private final BoxRepositoryPort boxRepo;
    private final ConsultorioDiagnosticosMedicosService diagnosticosMedicosService;
    private final ConsultorioTratamientoCatalogService tratamientoCatalogService;

    public HistoriaClinicaService(SesionClinicaRepositoryPort sesionRepo,
                                  DiagnosticoClinicoRepositoryPort diagnosticoRepo,
                                  AdjuntoClinicoRepositoryPort adjuntoRepo,
                                  AtencionInicialRepositoryPort atencionInicialRepo,
                                  AtencionInicialEvaluacionRepositoryPort atencionEvaluacionRepo,
                                  HistoriaClinicaLegajoRepositoryPort legajoRepo,
                                  HistoriaClinicaAntecedenteRepositoryPort antecedenteRepo,
                                  PlanTerapeuticoRepositoryPort planTerapeuticoRepo,
                                  PlanTratamientoDetalleRepositoryPort planDetalleRepo,
                                  AttachmentStoragePort attachmentStorage,
                                  PacienteRepositoryPort pacienteRepo,
                                  PacienteConsultorioRepositoryPort pacienteConsultorioRepo,
                                  ConsultorioRepositoryPort consultorioRepo,
                                  ProfesionalRepositoryPort profesionalRepo,
                                  ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo,
                                  TurnoRepositoryPort turnoRepo,
                                  UserRepositoryPort userRepo,
                                  BoxRepositoryPort boxRepo,
                                  ConsultorioDiagnosticosMedicosService diagnosticosMedicosService,
                                  ConsultorioTratamientoCatalogService tratamientoCatalogService) {
        this.sesionRepo = sesionRepo;
        this.diagnosticoRepo = diagnosticoRepo;
        this.adjuntoRepo = adjuntoRepo;
        this.atencionInicialRepo = atencionInicialRepo;
        this.atencionEvaluacionRepo = atencionEvaluacionRepo;
        this.legajoRepo = legajoRepo;
        this.antecedenteRepo = antecedenteRepo;
        this.planTerapeuticoRepo = planTerapeuticoRepo;
        this.planDetalleRepo = planDetalleRepo;
        this.attachmentStorage = attachmentStorage;
        this.pacienteRepo = pacienteRepo;
        this.pacienteConsultorioRepo = pacienteConsultorioRepo;
        this.consultorioRepo = consultorioRepo;
        this.profesionalRepo = profesionalRepo;
        this.profesionalConsultorioRepo = profesionalConsultorioRepo;
        this.turnoRepo = turnoRepo;
        this.userRepo = userRepo;
        this.boxRepo = boxRepo;
        this.diagnosticosMedicosService = diagnosticosMedicosService;
        this.tratamientoCatalogService = tratamientoCatalogService;
    }

    @Transactional(readOnly = true)
    public HistoriaClinicaWorkspaceResult getWorkspace(UUID consultorioId,
                                                       UUID pacienteId,
                                                       String q,
                                                       UUID profesionalId,
                                                       LocalDate from,
                                                       LocalDate to,
                                                       HistoriaClinicaSesionEstado estado,
                                                       int page,
                                                       int size,
                                                       String userEmail,
                                                       Set<String> roles) {
        assertClinicalAccess(consultorioId, userEmail, roles);
        Map<UUID, Profesional> profesionales = buildProfesionalMap(consultorioId);
        List<SesionClinica> sesiones = sesionRepo.findByConsultorioId(consultorioId);
        Map<UUID, Paciente> pacientes = loadPacientes(sesiones.stream()
                .map(SesionClinica::getPacienteId)
                .filter(Objects::nonNull)
                .distinct()
                .toList());

        List<HistoriaClinicaWorkspaceItem> filtered = sesiones.stream()
                .filter(session -> pacienteId == null || pacienteId.equals(session.getPacienteId()))
                .filter(session -> profesionalId == null || profesionalId.equals(session.getProfesionalId()))
                .filter(session -> estado == null || estado == session.getEstado())
                .filter(session -> from == null || !session.getFechaAtencion().toLocalDate().isBefore(from))
                .filter(session -> to == null || !session.getFechaAtencion().toLocalDate().isAfter(to))
                .filter(session -> matchesWorkspaceQuery(session, pacientes.get(session.getPacienteId()), q))
                .sorted(Comparator.comparing(SesionClinica::getFechaAtencion).reversed()
                        .thenComparing(SesionClinica::getUpdatedAt).reversed())
                .map(session -> toWorkspaceItem(session, pacientes.get(session.getPacienteId()), profesionales.get(session.getProfesionalId())))
                .toList();

        return new HistoriaClinicaWorkspaceResult(
                buildWorkspaceProfesionales(profesionales),
                paginate(filtered, page, size),
                safePage(page),
                safeSize(size),
                filtered.size()
        );
    }

    @Transactional(readOnly = true)
    public HistoriaClinicaPacienteResult getPaciente(UUID consultorioId,
                                                     UUID pacienteId,
                                                     String userEmail,
                                                     Set<String> roles) {
        Paciente paciente = loadPacienteWithClinicalAccess(consultorioId, pacienteId, userEmail, roles);
        List<SesionClinica> sesiones = sesionRepo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId);
        int diagnosticosActivos = (int) diagnosticoRepo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId).stream()
                .filter(d -> d.getEstado() == DiagnosticoClinicoEstado.ACTIVO)
                .count();
        return new HistoriaClinicaPacienteResult(
                paciente.getId(),
                consultorioId,
                paciente.getDni(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getTelefono(),
                paciente.getEmail(),
                paciente.getFechaNacimiento(),
                paciente.getObraSocialNombre(),
                paciente.getObraSocialPlan(),
                paciente.getObraSocialNroAfiliado(),
                paciente.isActivo(),
                diagnosticosActivos,
                sesiones.isEmpty() ? null : sesiones.get(0).getFechaAtencion(),
                paciente.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public HistoriaClinicaOverviewResult getOverview(UUID consultorioId,
                                                     UUID pacienteId,
                                                     String userEmail,
                                                     Set<String> roles) {
        HistoriaClinicaPacienteResult paciente = getPaciente(consultorioId, pacienteId, userEmail, roles);
        List<SesionClinica> sesiones = sesionRepo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId);
        List<DiagnosticoClinico> diagnosticos = diagnosticoRepo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId);
        List<HistoriaClinicaAntecedente> antecedentes = antecedenteRepo.findByConsultorioIdAndPacienteId(consultorioId, pacienteId);
        HistoriaClinicaLegajo legajo = legajoRepo.findByConsultorioIdAndPacienteId(consultorioId, pacienteId).orElse(null);
        AtencionInicial atencionInicial = atencionInicialRepo.findLatestByConsultorioIdAndPacienteId(consultorioId, pacienteId).orElse(null);
        AtencionInicialEvaluacion evaluacionInicial = atencionInicial != null
                ? atencionEvaluacionRepo.findByAtencionInicialId(atencionInicial.getId()).orElse(null)
                : null;
        PlanTerapeutico planTerapeutico = planTerapeuticoRepo.findLatestByConsultorioIdAndPacienteId(consultorioId, pacienteId).orElse(null);
        List<PlanTratamientoDetalle> planTratamientos = planTerapeutico != null
                ? planDetalleRepo.findByPlanTerapeuticoId(planTerapeutico.getId())
                : List.of();
        Map<UUID, Profesional> profesionales = buildProfesionalMap(consultorioId);
        List<AdjuntoClinico> adjuntos = new ArrayList<>(loadAdjuntosForSesiones(sesiones));
        if (atencionInicial != null) {
            adjuntos.addAll(adjuntoRepo.findByAtencionInicialId(atencionInicial.getId()));
        }

        HistoriaClinicaLegajoStatusResult legajoStatus = toLegajoStatus(
                legajo,
                !sesiones.isEmpty() || !diagnosticos.isEmpty() || !antecedentes.isEmpty() || atencionInicial != null || planTerapeutico != null
        );
        List<HistoriaClinicaAntecedenteResult> antecedentesRelevantes = antecedentes.stream()
                .sorted(Comparator.comparing(HistoriaClinicaAntecedente::isCritical).reversed()
                        .thenComparing(HistoriaClinicaAntecedente::getUpdatedAt, Comparator.reverseOrder()))
                .limit(4)
                .map(this::toAntecedenteResult)
                .toList();
        List<HistoriaClinicaActiveCaseSummaryResult> casosActivos = diagnosticos.stream()
                .filter(diagnostico -> diagnostico.getEstado() == DiagnosticoClinicoEstado.ACTIVO)
                .sorted(Comparator.comparing(DiagnosticoClinico::getFechaInicio, Comparator.reverseOrder()))
                .map(diagnostico -> toCaseSummary(diagnostico, sesiones, profesionales))
                .toList();
        List<String> alertasClinicas = antecedentes.stream()
                .filter(HistoriaClinicaAntecedente::isCritical)
                .map(antecedente -> firstNonBlank(
                        antecedente.getLabel() + (antecedente.getValueText() != null ? ": " + antecedente.getValueText() : ""),
                        antecedente.getLabel(),
                        "Alerta clinica"
                ))
                .limit(4)
                .toList();
        HistoriaClinicaSesionSummaryResult ultimaSesion = sesiones.stream()
                .filter(sesion -> sesion.getEstado() != HistoriaClinicaSesionEstado.ANULADA)
                .sorted(Comparator.comparing(SesionClinica::getFechaAtencion).reversed())
                .findFirst()
                .map(sesion -> toSesionSummary(sesion, profesionales.get(sesion.getProfesionalId())))
                .orElse(null);
        List<AdjuntoClinicoResult> adjuntosRecientes = adjuntos.stream()
                .sorted(Comparator.comparing(AdjuntoClinico::getCreatedAt).reversed())
                .limit(4)
                .map(this::toAdjuntoResult)
                .toList();

        return new HistoriaClinicaOverviewResult(
                paciente,
                legajoStatus,
                alertasClinicas,
                antecedentesRelevantes,
                casosActivos,
                ultimaSesion,
                adjuntosRecientes,
                resolveProfesionalHabitual(sesiones, profesionales),
                toAtencionInicialSummary(atencionInicial, profesionales.get(atencionInicial != null ? atencionInicial.getProfesionalId() : null)),
                toAtencionInicialEvaluacionResult(evaluacionInicial),
                toPlanTerapeuticoSummary(planTerapeutico, planTratamientos, profesionales.get(planTerapeutico != null ? planTerapeutico.getProfesionalId() : null))
        );
    }

    public HistoriaClinicaOverviewResult createAtencionInicial(CreateAtencionInicialCommand command,
                                                               String userEmail,
                                                               Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(command.consultorioId(), command.pacienteId(), userEmail, roles);
        validateProfesionalCanWrite(command.consultorioId(), command.profesionalId(), userEmail, roles);
        validateAtencionInicialPayload(command);

        HistoriaClinicaLegajo legajo = legajoRepo.findByConsultorioIdAndPacienteId(command.consultorioId(), command.pacienteId())
                .orElseGet(() -> legajoRepo.save(new HistoriaClinicaLegajo(
                        UUID.randomUUID(),
                        command.consultorioId(),
                        command.pacienteId(),
                        actorUserId,
                        actorUserId,
                        Instant.now(),
                        Instant.now()
                )));

        ConsultorioDiagnosticosMedicosService.DiagnosticoMedicoSnapshot diagnosticoSeleccionado =
                resolveAtencionInicialDiagnostico(command);

        AtencionInicial atencionInicial = atencionInicialRepo.save(new AtencionInicial(
                UUID.randomUUID(),
                legajo.getId(),
                command.consultorioId(),
                command.pacienteId(),
                command.profesionalId(),
                command.fechaHora(),
                command.tipoIngreso(),
                trimToNull(command.motivoConsultaBreve()),
                trimToNull(command.sintomasPrincipales()),
                trimToNull(command.tiempoEvolucion()),
                trimToNull(command.observaciones()),
                trimToNull(command.especialidadDerivante()),
                trimToNull(command.profesionalDerivante()),
                command.fechaPrescripcion(),
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.codigo() : null,
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.nombre() : null,
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.tipo() : null,
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.categoriaCodigo() : null,
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.categoriaNombre() : null,
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.subcategoria() : null,
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.regionAnatomica() : null,
                trimToNull(command.diagnosticoObservacion()),
                trimToNull(command.observacionesPrescripcion()),
                trimToNull(command.resumenClinicoInicial()),
                trimToNull(command.hallazgosRelevantes()),
                actorUserId,
                actorUserId,
                Instant.now(),
                Instant.now()
        ));

        if (hasEvaluacionPayload(command)) {
            atencionEvaluacionRepo.save(new AtencionInicialEvaluacion(
                    UUID.randomUUID(),
                    atencionInicial.getId(),
                    command.evaluacion().peso(),
                    command.evaluacion().altura(),
                    resolveImc(command.evaluacion().peso(), command.evaluacion().altura(), command.evaluacion().imc()),
                    trimToNull(command.evaluacion().presionArterial()),
                    command.evaluacion().frecuenciaCardiaca(),
                    command.evaluacion().saturacion(),
                    command.evaluacion().temperatura(),
                    trimToNull(command.evaluacion().observaciones()),
                    Instant.now(),
                    Instant.now()
            ));
        }

        persistAntecedentes(legajo, command.antecedentes(), actorUserId);

        PlanTerapeutico planTerapeutico = planTerapeuticoRepo.save(new PlanTerapeutico(
                UUID.randomUUID(),
                atencionInicial.getId(),
                command.consultorioId(),
                command.pacienteId(),
                command.profesionalId(),
                PlanTerapeuticoEstado.ACTIVO,
                trimToNull(command.planObservacionesGenerales()),
                actorUserId,
                actorUserId,
                Instant.now(),
                Instant.now()
        ));

        List<PlanTratamientoDetalle> detalles = new ArrayList<>();
        for (int index = 0; index < command.tratamientos().size(); index++) {
            PlanTratamientoDetalleCommand tratamiento = command.tratamientos().get(index);
            ConsultorioTratamientoCatalogService.TratamientoSnapshot tratamientoSeleccionado =
                    tratamientoCatalogService.requireActiveTreatment(command.consultorioId(), tratamiento.tratamientoId());
            detalles.add(new PlanTratamientoDetalle(
                    UUID.randomUUID(),
                    planTerapeutico.getId(),
                    tratamiento.tratamientoId().trim(),
                    tratamientoSeleccionado.nombre(),
                    tratamientoSeleccionado.categoriaCodigo(),
                    tratamientoSeleccionado.categoriaNombre(),
                    tratamientoSeleccionado.tipo(),
                    tratamientoSeleccionado.requiereAutorizacion(),
                    tratamientoSeleccionado.requierePrescripcionMedica(),
                    tratamientoSeleccionado.duracionSugeridaMinutos(),
                    tratamiento.cantidadSesiones(),
                    trimToNull(tratamiento.frecuenciaSugerida()),
                    tratamiento.caracterCaso(),
                    tratamiento.fechaEstimadaInicio(),
                    tratamiento.requiereAutorizacion(),
                    trimToNull(tratamiento.observaciones()),
                    trimToNull(tratamiento.observacionesAdministrativas()),
                    index,
                    Instant.now()
            ));
        }
        planDetalleRepo.saveAll(detalles);

        diagnosticoRepo.save(new DiagnosticoClinico(
                UUID.randomUUID(),
                command.consultorioId(),
                command.pacienteId(),
                command.profesionalId(),
                null,
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.codigo() : null,
                buildInitialCaseDescription(detalles, command, diagnosticoSeleccionado),
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.tipo() : null,
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.categoriaCodigo() : null,
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.categoriaNombre() : null,
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.subcategoria() : null,
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.regionAnatomica() : null,
                DiagnosticoClinicoEstado.ACTIVO,
                command.fechaHora().toLocalDate(),
                null,
                trimToNull(command.planObservacionesGenerales()),
                actorUserId,
                actorUserId,
                Instant.now(),
                Instant.now()
        ));

        return getOverview(command.consultorioId(), command.pacienteId(), userEmail, roles);
    }

    public HistoriaClinicaOverviewResult createLegajo(CreateHistoriaClinicaLegajoCommand command,
                                                      String userEmail,
                                                      Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(command.consultorioId(), command.pacienteId(), userEmail, roles);
        if (legajoRepo.findByConsultorioIdAndPacienteId(command.consultorioId(), command.pacienteId()).isPresent()) {
            throw new HistoriaClinicaConflictException("La historia clinica ya existe para este paciente");
        }

        boolean hasInitialSessionPayload = command.fechaAtencion() != null
                || hasText(command.motivoConsulta())
                || hasText(command.resumenClinico())
                || hasText(command.subjetivo())
                || hasText(command.objetivo())
                || hasText(command.evaluacion())
                || hasText(command.plan());
        boolean hasInitialCasePayload = hasText(command.casoDescripcion())
                || hasText(command.casoCodigo())
                || hasText(command.casoNotas())
                || command.casoFechaInicio() != null;

        if ((hasInitialSessionPayload || hasInitialCasePayload) && command.profesionalId() == null) {
            throw new HistoriaClinicaValidationException("Debe indicar el profesional responsable para crear contexto clinico inicial");
        }
        if (command.profesionalId() != null) {
            validateProfesionalCanWrite(command.consultorioId(), command.profesionalId(), userEmail, roles);
        }

        HistoriaClinicaLegajo legajo = legajoRepo.save(new HistoriaClinicaLegajo(
                UUID.randomUUID(),
                command.consultorioId(),
                command.pacienteId(),
                actorUserId,
                actorUserId,
                Instant.now(),
                Instant.now()
        ));

        persistAntecedentes(legajo, command.antecedentes(), actorUserId);

        SesionClinica sesionInicial = null;
        if (hasInitialSessionPayload && command.profesionalId() != null) {
            sesionInicial = sesionRepo.save(new SesionClinica(
                    UUID.randomUUID(),
                    command.consultorioId(),
                    command.pacienteId(),
                    command.profesionalId(),
                    null,
                    null,
                    command.fechaAtencion() != null ? command.fechaAtencion() : LocalDateTime.now(),
                    HistoriaClinicaSesionEstado.BORRADOR,
                    com.akine_api.domain.model.HistoriaClinicaTipoAtencion.EVALUACION,
                    trimToNull(command.motivoConsulta()),
                    trimToNull(command.resumenClinico()),
                    trimToNull(command.subjetivo()),
                    trimToNull(command.objetivo()),
                    trimToNull(command.evaluacion()),
                    trimToNull(command.plan()),
                    HistoriaClinicaOrigenRegistro.MANUAL,
                    actorUserId,
                    actorUserId,
                    null,
                    Instant.now(),
                    Instant.now(),
                    null
            ));
        }

        if (hasInitialCasePayload && command.profesionalId() != null) {
            diagnosticoRepo.save(new DiagnosticoClinico(
                    UUID.randomUUID(),
                    command.consultorioId(),
                    command.pacienteId(),
                    command.profesionalId(),
                    sesionInicial != null ? sesionInicial.getId() : null,
                    trimToNull(command.casoCodigo()),
                    trimToNull(command.casoDescripcion()),
                    null,
                    null,
                    null,
                    null,
                    null,
                    DiagnosticoClinicoEstado.ACTIVO,
                    command.casoFechaInicio() != null
                            ? command.casoFechaInicio()
                            : (sesionInicial != null ? sesionInicial.getFechaAtencion().toLocalDate() : LocalDate.now()),
                    null,
                    trimToNull(command.casoNotas()),
                    actorUserId,
                    actorUserId,
                    Instant.now(),
                    Instant.now()
            ));
        }

        return getOverview(command.consultorioId(), command.pacienteId(), userEmail, roles);
    }

    @Transactional(readOnly = true)
    public List<HistoriaClinicaAntecedenteResult> getAntecedentes(UUID consultorioId,
                                                                  UUID pacienteId,
                                                                  String userEmail,
                                                                  Set<String> roles) {
        loadPacienteWithClinicalAccess(consultorioId, pacienteId, userEmail, roles);
        return antecedenteRepo.findByConsultorioIdAndPacienteId(consultorioId, pacienteId).stream()
                .map(this::toAntecedenteResult)
                .toList();
    }

    public List<HistoriaClinicaAntecedenteResult> updateAntecedentes(UpdateHistoriaClinicaAntecedentesCommand command,
                                                                     String userEmail,
                                                                     Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(command.consultorioId(), command.pacienteId(), userEmail, roles);
        HistoriaClinicaLegajo legajo = ensureLegajoExists(command.consultorioId(), command.pacienteId(), actorUserId);
        return persistAntecedentes(legajo, command.antecedentes(), actorUserId);
    }

    @Transactional(readOnly = true)
    public List<HistoriaClinicaTimelineEventResult> getTimeline(UUID consultorioId,
                                                                UUID pacienteId,
                                                                String type,
                                                                String userEmail,
                                                                Set<String> roles) {
        loadPacienteWithClinicalAccess(consultorioId, pacienteId, userEmail, roles);
        List<SesionClinica> sesiones = sesionRepo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId);
        List<DiagnosticoClinico> diagnosticos = diagnosticoRepo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId);
        List<HistoriaClinicaAntecedente> antecedentes = antecedenteRepo.findByConsultorioIdAndPacienteId(consultorioId, pacienteId);
        HistoriaClinicaLegajo legajo = legajoRepo.findByConsultorioIdAndPacienteId(consultorioId, pacienteId).orElse(null);
        List<AtencionInicial> atencionesIniciales = atencionInicialRepo.findByConsultorioIdAndPacienteId(consultorioId, pacienteId);
        PlanTerapeutico planTerapeutico = planTerapeuticoRepo.findLatestByConsultorioIdAndPacienteId(consultorioId, pacienteId).orElse(null);
        List<PlanTratamientoDetalle> planTratamientos = planTerapeutico != null
                ? planDetalleRepo.findByPlanTerapeuticoId(planTerapeutico.getId())
                : List.of();
        Map<UUID, Profesional> profesionales = buildProfesionalMap(consultorioId);
        Map<UUID, SesionClinica> sesionesById = sesiones.stream()
                .collect(Collectors.toMap(SesionClinica::getId, Function.identity(), (left, right) -> left));
        Map<UUID, AtencionInicial> atencionesById = atencionesIniciales.stream()
                .collect(Collectors.toMap(AtencionInicial::getId, Function.identity(), (left, right) -> left));
        List<AdjuntoClinico> adjuntos = new ArrayList<>(loadAdjuntosForSesiones(sesiones));
        adjuntos.addAll(adjuntoRepo.findByAtencionInicialIds(atencionesIniciales.stream().map(AtencionInicial::getId).toList()));

        List<HistoriaClinicaTimelineEventResult> events = new ArrayList<>();
        if (legajo != null) {
            events.add(new HistoriaClinicaTimelineEventResult(
                    legajo.getId().toString(),
                    HistoriaClinicaTimelineEventType.HC_CREATED,
                    toLocalDateTime(legajo.getCreatedAt()),
                    null,
                    null,
                    "Historia clinica creada",
                    "Se creo el legajo clinico del paciente en este consultorio.",
                    null,
                    legajo.getId()
            ));
        }
        atencionesIniciales.forEach(atencion -> {
            Profesional profesional = profesionales.get(atencion.getProfesionalId());
            events.add(new HistoriaClinicaTimelineEventResult(
                    atencion.getId().toString(),
                    HistoriaClinicaTimelineEventType.ATENCION_INICIAL,
                    atencion.getFechaHora(),
                    atencion.getProfesionalId(),
                    fullName(profesional),
                    "Atencion inicial registrada",
                    firstNonBlank(atencion.getMotivoConsultaBreve(), atencion.getDiagnosticoNombre(), "Base clinica inicial"),
                    atencion.getTipoIngreso().name(),
                    atencion.getId()
            ));
        });
        antecedentes.forEach(antecedente -> events.add(new HistoriaClinicaTimelineEventResult(
                antecedente.getId().toString(),
                HistoriaClinicaTimelineEventType.ANTECEDENTE_UPDATED,
                toLocalDateTime(antecedente.getUpdatedAt()),
                null,
                null,
                "Antecedente actualizado",
                firstNonBlank(
                        antecedente.getLabel() + (antecedente.getValueText() != null ? ": " + antecedente.getValueText() : ""),
                        antecedente.getLabel(),
                        "Antecedente"
                ),
                antecedente.isCritical() ? "Critico" : null,
                antecedente.getId()
        )));
        diagnosticos.forEach(diagnostico -> {
            Profesional profesional = profesionales.get(diagnostico.getProfesionalId());
            events.add(new HistoriaClinicaTimelineEventResult(
                    diagnostico.getId() + ":open",
                    HistoriaClinicaTimelineEventType.CASO_OPENED,
                    diagnostico.getFechaInicio().atStartOfDay(),
                    diagnostico.getProfesionalId(),
                    fullName(profesional),
                    "Apertura de caso clinico",
                    diagnostico.getDescripcion(),
                    diagnostico.getEstado().name(),
                    diagnostico.getId()
            ));
            if (diagnostico.getEstado() != DiagnosticoClinicoEstado.ACTIVO && diagnostico.getFechaFin() != null) {
                events.add(new HistoriaClinicaTimelineEventResult(
                        diagnostico.getId() + ":close",
                        HistoriaClinicaTimelineEventType.CASO_CLOSED,
                        diagnostico.getFechaFin().atStartOfDay(),
                        diagnostico.getProfesionalId(),
                        fullName(profesional),
                        "Cierre de caso clinico",
                        diagnostico.getDescripcion(),
                        diagnostico.getEstado().name(),
                        diagnostico.getId()
                ));
            }
        });
        if (planTerapeutico != null) {
            Profesional profesional = profesionales.get(planTerapeutico.getProfesionalId());
            events.add(new HistoriaClinicaTimelineEventResult(
                    planTerapeutico.getId().toString(),
                    HistoriaClinicaTimelineEventType.PLAN_TERAPEUTICO,
                    toLocalDateTime(planTerapeutico.getCreatedAt()),
                    planTerapeutico.getProfesionalId(),
                    fullName(profesional),
                    "Plan terapeutico inicial",
                    summarizePlanTratamientos(planTratamientos),
                    planTerapeutico.getEstado().name(),
                    planTerapeutico.getId()
            ));
        }
        sesiones.forEach(sesion -> {
            Profesional profesional = profesionales.get(sesion.getProfesionalId());
            events.add(new HistoriaClinicaTimelineEventResult(
                    sesion.getId().toString(),
                    HistoriaClinicaTimelineEventType.SESION,
                    sesion.getFechaAtencion(),
                    sesion.getProfesionalId(),
                    fullName(profesional),
                    "Sesion " + tipoToTimelineLabel(sesion),
                    firstNonBlank(sesion.getResumenClinico(), sesion.getMotivoConsulta(), "Sesion clinica"),
                    sesion.getEstado().name(),
                    sesion.getId()
            ));
        });
        adjuntos.forEach(adjunto -> {
            SesionClinica sesion = sesionesById.get(adjunto.getSesionId());
            AtencionInicial atencion = adjunto.getAtencionInicialId() != null ? atencionesById.get(adjunto.getAtencionInicialId()) : null;
            Profesional profesional = sesion != null
                    ? profesionales.get(sesion.getProfesionalId())
                    : atencion != null ? profesionales.get(atencion.getProfesionalId()) : null;
            events.add(new HistoriaClinicaTimelineEventResult(
                    adjunto.getId().toString(),
                    HistoriaClinicaTimelineEventType.ADJUNTO,
                    toLocalDateTime(adjunto.getCreatedAt()),
                    sesion != null ? sesion.getProfesionalId() : atencion != null ? atencion.getProfesionalId() : null,
                    fullName(profesional),
                    "Adjunto agregado",
                    adjunto.getOriginalFilename(),
                    null,
                    adjunto.getId()
            ));
        });

        return events.stream()
                .filter(event -> matchesTimelineType(type, event.type()))
                .sorted(Comparator.comparing(HistoriaClinicaTimelineEventResult::occurredAt).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SesionClinicaResult> listSesiones(UUID consultorioId,
                                                  UUID pacienteId,
                                                  UUID profesionalId,
                                                  LocalDate from,
                                                  LocalDate to,
                                                  HistoriaClinicaSesionEstado estado,
                                                  int page,
                                                  int size,
                                                  String userEmail,
                                                  Set<String> roles) {
        loadPacienteWithClinicalAccess(consultorioId, pacienteId, userEmail, roles);
        List<SesionClinica> sesiones = sesionRepo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId).stream()
                .filter(session -> profesionalId == null || profesionalId.equals(session.getProfesionalId()))
                .filter(session -> estado == null || estado == session.getEstado())
                .filter(session -> from == null || !session.getFechaAtencion().toLocalDate().isBefore(from))
                .filter(session -> to == null || !session.getFechaAtencion().toLocalDate().isAfter(to))
                .sorted(Comparator.comparing(SesionClinica::getFechaAtencion).reversed())
                .toList();
        Map<UUID, List<AdjuntoClinico>> adjuntos = adjuntoRepo.findBySesionIds(
                sesiones.stream().map(SesionClinica::getId).toList()
        ).stream().collect(Collectors.groupingBy(AdjuntoClinico::getSesionId));
        return paginate(sesiones, page, size).stream()
                .map(session -> toSesionResult(session, adjuntos.getOrDefault(session.getId(), List.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    public SesionClinicaResult getSesion(UUID consultorioId,
                                         UUID pacienteId,
                                         UUID sesionId,
                                         String userEmail,
                                         Set<String> roles) {
        loadPacienteWithClinicalAccess(consultorioId, pacienteId, userEmail, roles);
        SesionClinica sesion = loadSesion(consultorioId, pacienteId, sesionId);
        return toSesionResult(sesion, adjuntoRepo.findBySesionId(sesionId));
    }

    public SesionClinicaResult createSesion(CreateSesionClinicaCommand command,
                                            String userEmail,
                                            Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(command.consultorioId(), command.pacienteId(), userEmail, roles);
        ensureLegajoExists(command.consultorioId(), command.pacienteId(), actorUserId);
        validateProfesionalCanWrite(command.consultorioId(), command.profesionalId(), userEmail, roles);
        TurnoLinkValidation turnoLink = validateTurnoLink(
                command.consultorioId(),
                command.pacienteId(),
                command.profesionalId(),
                command.turnoId(),
                null
        );
        UUID boxId = resolveBoxId(command.consultorioId(), command.boxId(), turnoLink.turno());
        SesionClinica sesion = new SesionClinica(
                UUID.randomUUID(),
                command.consultorioId(),
                command.pacienteId(),
                command.profesionalId(),
                command.turnoId(),
                boxId,
                command.fechaAtencion(),
                HistoriaClinicaSesionEstado.BORRADOR,
                command.tipoAtencion(),
                trimToNull(command.motivoConsulta()),
                trimToNull(command.resumenClinico()),
                trimToNull(command.subjetivo()),
                trimToNull(command.objetivo()),
                trimToNull(command.evaluacion()),
                trimToNull(command.plan()),
                HistoriaClinicaOrigenRegistro.MANUAL,
                actorUserId,
                actorUserId,
                null,
                Instant.now(),
                Instant.now(),
                null
        );
        return toSesionResult(sesionRepo.save(sesion), List.of());
    }

    public SesionClinicaResult updateSesion(UpdateSesionClinicaCommand command,
                                            String userEmail,
                                            Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(command.consultorioId(), command.pacienteId(), userEmail, roles);
        SesionClinica sesion = loadSesion(command.consultorioId(), command.pacienteId(), command.sesionId());
        validateProfesionalCanWrite(command.consultorioId(), command.profesionalId(), userEmail, roles);
        assertCanMutateSesion(sesion, userEmail, roles);
        TurnoLinkValidation turnoLink = validateTurnoLink(
                command.consultorioId(),
                command.pacienteId(),
                command.profesionalId(),
                command.turnoId(),
                command.sesionId()
        );
        UUID boxId = resolveBoxId(command.consultorioId(), command.boxId(), turnoLink.turno());
        sesion.update(
                command.profesionalId(),
                command.turnoId(),
                boxId,
                command.fechaAtencion(),
                command.tipoAtencion(),
                trimToNull(command.motivoConsulta()),
                trimToNull(command.resumenClinico()),
                trimToNull(command.subjetivo()),
                trimToNull(command.objetivo()),
                trimToNull(command.evaluacion()),
                trimToNull(command.plan()),
                actorUserId
        );
        return toSesionResult(sesionRepo.save(sesion), adjuntoRepo.findBySesionId(sesion.getId()));
    }

    public SesionClinicaResult closeSesion(ChangeSesionClinicaEstadoCommand command,
                                           String userEmail,
                                           Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(command.consultorioId(), command.pacienteId(), userEmail, roles);
        SesionClinica sesion = loadSesion(command.consultorioId(), command.pacienteId(), command.sesionId());
        assertCanMutateSesion(sesion, userEmail, roles);
        sesion.close(actorUserId);
        return toSesionResult(sesionRepo.save(sesion), adjuntoRepo.findBySesionId(sesion.getId()));
    }

    public SesionClinicaResult annulSesion(ChangeSesionClinicaEstadoCommand command,
                                           String userEmail,
                                           Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(command.consultorioId(), command.pacienteId(), userEmail, roles);
        SesionClinica sesion = loadSesion(command.consultorioId(), command.pacienteId(), command.sesionId());
        assertCanMutateSesion(sesion, userEmail, roles);
        sesion.annul(actorUserId);
        return toSesionResult(sesionRepo.save(sesion), adjuntoRepo.findBySesionId(sesion.getId()));
    }

    @Transactional(readOnly = true)
    public List<DiagnosticoClinicoResult> listDiagnosticos(UUID consultorioId,
                                                           UUID pacienteId,
                                                           String userEmail,
                                                           Set<String> roles) {
        loadPacienteWithClinicalAccess(consultorioId, pacienteId, userEmail, roles);
        return diagnosticoRepo.findByPacienteIdAndConsultorioId(pacienteId, consultorioId).stream()
                .sorted(Comparator.comparing((DiagnosticoClinico d) -> d.getEstado() == DiagnosticoClinicoEstado.ACTIVO).reversed()
                        .thenComparing(DiagnosticoClinico::getFechaInicio, Comparator.reverseOrder()))
                .map(this::toDiagnosticoResult)
                .toList();
    }

    public DiagnosticoClinicoResult createDiagnostico(CreateDiagnosticoClinicoCommand command,
                                                      String userEmail,
                                                      Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(command.consultorioId(), command.pacienteId(), userEmail, roles);
        ensureLegajoExists(command.consultorioId(), command.pacienteId(), actorUserId);
        validateProfesionalCanWrite(command.consultorioId(), command.profesionalId(), userEmail, roles);
        if (command.sesionId() != null) {
            SesionClinica sesion = loadSesion(command.consultorioId(), command.pacienteId(), command.sesionId());
            if (!command.profesionalId().equals(sesion.getProfesionalId())) {
                throw new HistoriaClinicaValidationException("El diagnostico debe pertenecer al mismo profesional de la sesion");
            }
        }
        ConsultorioDiagnosticosMedicosService.DiagnosticoMedicoSnapshot diagnosticoSeleccionado =
                diagnosticosMedicosService.requireActiveDiagnostico(command.consultorioId(), trimToNull(command.diagnosticoCodigo()));
        DiagnosticoClinico diagnostico = new DiagnosticoClinico(
                UUID.randomUUID(),
                command.consultorioId(),
                command.pacienteId(),
                command.profesionalId(),
                command.sesionId(),
                diagnosticoSeleccionado.codigo(),
                diagnosticoSeleccionado.nombre(),
                diagnosticoSeleccionado.tipo(),
                diagnosticoSeleccionado.categoriaCodigo(),
                diagnosticoSeleccionado.categoriaNombre(),
                diagnosticoSeleccionado.subcategoria(),
                diagnosticoSeleccionado.regionAnatomica(),
                DiagnosticoClinicoEstado.ACTIVO,
                command.fechaInicio(),
                null,
                trimToNull(command.notas()),
                actorUserId,
                actorUserId,
                Instant.now(),
                Instant.now()
        );
        return toDiagnosticoResult(diagnosticoRepo.save(diagnostico));
    }

    public DiagnosticoClinicoResult updateDiagnostico(UpdateDiagnosticoClinicoCommand command,
                                                      String userEmail,
                                                      Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(command.consultorioId(), command.pacienteId(), userEmail, roles);
        DiagnosticoClinico diagnostico = loadDiagnostico(command.consultorioId(), command.pacienteId(), command.diagnosticoId());
        validateProfesionalCanWrite(command.consultorioId(), command.profesionalId(), userEmail, roles);
        assertCanMutateDiagnostico(diagnostico, userEmail, roles);
        if (command.sesionId() != null) {
            SesionClinica sesion = loadSesion(command.consultorioId(), command.pacienteId(), command.sesionId());
            if (!command.profesionalId().equals(sesion.getProfesionalId())) {
                throw new HistoriaClinicaValidationException("El diagnostico debe pertenecer al mismo profesional de la sesion");
            }
        }
        ConsultorioDiagnosticosMedicosService.DiagnosticoMedicoSnapshot diagnosticoSeleccionado =
                diagnosticosMedicosService.requireActiveDiagnostico(command.consultorioId(), trimToNull(command.diagnosticoCodigo()));
        diagnostico.update(
                command.profesionalId(),
                command.sesionId(),
                diagnosticoSeleccionado.codigo(),
                diagnosticoSeleccionado.nombre(),
                diagnosticoSeleccionado.tipo(),
                diagnosticoSeleccionado.categoriaCodigo(),
                diagnosticoSeleccionado.categoriaNombre(),
                diagnosticoSeleccionado.subcategoria(),
                diagnosticoSeleccionado.regionAnatomica(),
                command.fechaInicio(),
                trimToNull(command.notas()),
                actorUserId
        );
        return toDiagnosticoResult(diagnosticoRepo.save(diagnostico));
    }

    public DiagnosticoClinicoResult resolveDiagnostico(ResolveDiagnosticoClinicoCommand command,
                                                       String userEmail,
                                                       Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(command.consultorioId(), command.pacienteId(), userEmail, roles);
        DiagnosticoClinico diagnostico = loadDiagnostico(command.consultorioId(), command.pacienteId(), command.diagnosticoId());
        assertCanMutateDiagnostico(diagnostico, userEmail, roles);
        diagnostico.resolve(command.fechaFin(), actorUserId);
        return toDiagnosticoResult(diagnosticoRepo.save(diagnostico));
    }

    public DiagnosticoClinicoResult discardDiagnostico(DiscardDiagnosticoClinicoCommand command,
                                                       String userEmail,
                                                       Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(command.consultorioId(), command.pacienteId(), userEmail, roles);
        DiagnosticoClinico diagnostico = loadDiagnostico(command.consultorioId(), command.pacienteId(), command.diagnosticoId());
        assertCanMutateDiagnostico(diagnostico, userEmail, roles);
        diagnostico.discard(command.fechaFin(), actorUserId);
        return toDiagnosticoResult(diagnosticoRepo.save(diagnostico));
    }

    public AdjuntoClinicoResult addAdjunto(UUID consultorioId,
                                           UUID pacienteId,
                                           UUID sesionId,
                                           MultipartFile file,
                                           String userEmail,
                                           Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(consultorioId, pacienteId, userEmail, roles);
        SesionClinica sesion = loadSesion(consultorioId, pacienteId, sesionId);
        assertCanMutateSesion(sesion, userEmail, roles);
        validateAttachment(file);
        UUID adjuntoId = UUID.randomUUID();
        String originalFilename = sanitizeFilename(file.getOriginalFilename());
        String contentType = sanitizeContentType(file.getContentType());
        String storageKey;
        try {
            storageKey = attachmentStorage.store(
                    consultorioId,
                    pacienteId,
                    sesionId,
                    adjuntoId,
                    originalFilename,
                    file.getBytes()
            );
        } catch (Exception ex) {
            throw new HistoriaClinicaValidationException("No se pudo procesar el adjunto clinico");
        }
        AdjuntoClinico adjunto = new AdjuntoClinico(
                adjuntoId,
                consultorioId,
                pacienteId,
                sesionId,
                null,
                storageKey,
                originalFilename,
                contentType,
                file.getSize(),
                actorUserId,
                Instant.now()
        );
        return toAdjuntoResult(adjuntoRepo.save(adjunto));
    }

    public AdjuntoClinicoResult addAdjuntoAtencionInicial(UUID consultorioId,
                                                          UUID pacienteId,
                                                          UUID atencionInicialId,
                                                          MultipartFile file,
                                                          String userEmail,
                                                          Set<String> roles) {
        UUID actorUserId = resolveUserId(userEmail);
        loadPacienteWithClinicalAccess(consultorioId, pacienteId, userEmail, roles);
        AtencionInicial atencionInicial = loadAtencionInicial(consultorioId, pacienteId, atencionInicialId);
        validateProfesionalCanWrite(consultorioId, atencionInicial.getProfesionalId(), userEmail, roles);
        validateAttachment(file);
        UUID adjuntoId = UUID.randomUUID();
        String originalFilename = sanitizeFilename(file.getOriginalFilename());
        String contentType = sanitizeContentType(file.getContentType());
        String storageKey;
        try {
            storageKey = attachmentStorage.store(
                    consultorioId,
                    pacienteId,
                    atencionInicialId,
                    adjuntoId,
                    originalFilename,
                    file.getBytes()
            );
        } catch (Exception ex) {
            throw new HistoriaClinicaValidationException("No se pudo procesar el adjunto clinico");
        }
        AdjuntoClinico adjunto = new AdjuntoClinico(
                adjuntoId,
                consultorioId,
                pacienteId,
                null,
                atencionInicialId,
                storageKey,
                originalFilename,
                contentType,
                file.getSize(),
                actorUserId,
                Instant.now()
        );
        return toAdjuntoResult(adjuntoRepo.save(adjunto));
    }

    @Transactional(readOnly = true)
    public AdjuntoClinicoDownloadResult downloadAdjunto(UUID consultorioId,
                                                        UUID pacienteId,
                                                        UUID adjuntoId,
                                                        String userEmail,
                                                        Set<String> roles) {
        loadPacienteWithClinicalAccess(consultorioId, pacienteId, userEmail, roles);
        AdjuntoClinico adjunto = loadAdjunto(consultorioId, pacienteId, adjuntoId);
        return new AdjuntoClinicoDownloadResult(
                adjunto.getOriginalFilename(),
                adjunto.getContentType(),
                adjunto.getSizeBytes(),
                attachmentStorage.load(adjunto.getStorageKey())
        );
    }

    public void deleteAdjunto(UUID consultorioId,
                              UUID pacienteId,
                              UUID adjuntoId,
                              String userEmail,
                              Set<String> roles) {
        loadPacienteWithClinicalAccess(consultorioId, pacienteId, userEmail, roles);
        AdjuntoClinico adjunto = loadAdjunto(consultorioId, pacienteId, adjuntoId);
        if (adjunto.getSesionId() != null) {
            SesionClinica sesion = loadSesion(consultorioId, pacienteId, adjunto.getSesionId());
            assertCanMutateSesion(sesion, userEmail, roles);
        } else if (adjunto.getAtencionInicialId() != null) {
            AtencionInicial atencionInicial = loadAtencionInicial(consultorioId, pacienteId, adjunto.getAtencionInicialId());
            validateProfesionalCanWrite(consultorioId, atencionInicial.getProfesionalId(), userEmail, roles);
        }
        attachmentStorage.delete(adjunto.getStorageKey());
        adjuntoRepo.deleteById(adjuntoId);
    }

    private AtencionInicial loadAtencionInicial(UUID consultorioId, UUID pacienteId, UUID atencionInicialId) {
        AtencionInicial atencionInicial = atencionInicialRepo.findById(atencionInicialId)
                .orElseThrow(() -> new HistoriaClinicaValidationException("Atencion inicial no encontrada"));
        if (!consultorioId.equals(atencionInicial.getConsultorioId()) || !pacienteId.equals(atencionInicial.getPacienteId())) {
            throw new HistoriaClinicaValidationException("Atencion inicial no encontrada");
        }
        return atencionInicial;
    }

    private SesionClinica loadSesion(UUID consultorioId, UUID pacienteId, UUID sesionId) {
        SesionClinica sesion = sesionRepo.findById(sesionId)
                .orElseThrow(() -> new SesionClinicaNotFoundException("Sesion clinica no encontrada"));
        if (!consultorioId.equals(sesion.getConsultorioId()) || !pacienteId.equals(sesion.getPacienteId())) {
            throw new SesionClinicaNotFoundException("Sesion clinica no encontrada");
        }
        return sesion;
    }

    private DiagnosticoClinico loadDiagnostico(UUID consultorioId, UUID pacienteId, UUID diagnosticoId) {
        DiagnosticoClinico diagnostico = diagnosticoRepo.findById(diagnosticoId)
                .orElseThrow(() -> new DiagnosticoClinicoNotFoundException("Diagnostico clinico no encontrado"));
        if (!consultorioId.equals(diagnostico.getConsultorioId()) || !pacienteId.equals(diagnostico.getPacienteId())) {
            throw new DiagnosticoClinicoNotFoundException("Diagnostico clinico no encontrado");
        }
        return diagnostico;
    }

    private AdjuntoClinico loadAdjunto(UUID consultorioId, UUID pacienteId, UUID adjuntoId) {
        AdjuntoClinico adjunto = adjuntoRepo.findById(adjuntoId)
                .orElseThrow(() -> new AdjuntoClinicoNotFoundException("Adjunto clinico no encontrado"));
        if (!consultorioId.equals(adjunto.getConsultorioId()) || !pacienteId.equals(adjunto.getPacienteId())) {
            throw new AdjuntoClinicoNotFoundException("Adjunto clinico no encontrado");
        }
        return adjunto;
    }

    private void assertClinicalAccess(UUID consultorioId, String userEmail, Set<String> roles) {
        ConsultorioStateGuardService.requireActive(consultorioRepo, consultorioId);
        if (!roles.contains("ROLE_ADMIN")
                && !roles.contains("ROLE_PROFESIONAL_ADMIN")
                && !roles.contains("ROLE_PROFESIONAL")) {
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

    private Paciente loadPacienteWithClinicalAccess(UUID consultorioId,
                                                    UUID pacienteId,
                                                    String userEmail,
                                                    Set<String> roles) {
        assertClinicalAccess(consultorioId, userEmail, roles);
        Paciente paciente = pacienteRepo.findById(pacienteId)
                .orElseThrow(() -> new PacienteNotFoundException("Paciente no encontrado"));
        if (!pacienteConsultorioRepo.existsByPacienteIdAndConsultorioId(pacienteId, consultorioId)) {
            throw new PacienteNotFoundException("Paciente no encontrado en este consultorio");
        }
        return paciente;
    }

    private void validateProfesionalCanWrite(UUID consultorioId,
                                             UUID profesionalId,
                                             String userEmail,
                                             Set<String> roles) {
        Profesional profesional = profesionalRepo.findById(profesionalId)
                .orElseThrow(() -> new ProfesionalNotFoundException("Profesional no encontrado"));
        ProfesionalConsultorio asignacion = profesionalConsultorioRepo
                .findByProfesionalIdAndConsultorioId(profesionalId, consultorioId)
                .filter(ProfesionalConsultorio::isActivo)
                .orElseThrow(() -> new HistoriaClinicaValidationException("El profesional no tiene acceso activo al consultorio"));

        if (!asignacion.isActivo() || !profesional.isActivo()) {
            throw new HistoriaClinicaValidationException("El profesional no tiene acceso activo al consultorio");
        }
        if (roles.contains("ROLE_PROFESIONAL")) {
            Profesional own = profesionalRepo.findByEmail(userEmail)
                    .orElseThrow(() -> new AccessDeniedException("Profesional no encontrado"));
            if (!own.getId().equals(profesionalId)) {
                throw new AccessDeniedException("Solo puede gestionar registros clinicos propios");
            }
        }
    }

    private void assertCanMutateSesion(SesionClinica sesion, String userEmail, Set<String> roles) {
        if (!sesion.isEditable()) {
            throw new HistoriaClinicaConflictException("La sesion clinica ya no admite cambios");
        }
        if (roles.contains("ROLE_PROFESIONAL")) {
            Profesional own = profesionalRepo.findByEmail(userEmail)
                    .orElseThrow(() -> new AccessDeniedException("Profesional no encontrado"));
            if (!own.getId().equals(sesion.getProfesionalId())) {
                throw new AccessDeniedException("Solo puede editar sesiones propias");
            }
        }
    }

    private void assertCanMutateDiagnostico(DiagnosticoClinico diagnostico, String userEmail, Set<String> roles) {
        if (!diagnostico.isEditable()) {
            throw new HistoriaClinicaConflictException("El diagnostico ya no admite cambios");
        }
        if (roles.contains("ROLE_PROFESIONAL")) {
            Profesional own = profesionalRepo.findByEmail(userEmail)
                    .orElseThrow(() -> new AccessDeniedException("Profesional no encontrado"));
            if (!own.getId().equals(diagnostico.getProfesionalId())) {
                throw new AccessDeniedException("Solo puede editar diagnosticos propios");
            }
        }
    }

    private TurnoLinkValidation validateTurnoLink(UUID consultorioId,
                                                  UUID pacienteId,
                                                  UUID profesionalId,
                                                  UUID turnoId,
                                                  UUID currentSesionId) {
        if (turnoId == null) {
            return new TurnoLinkValidation(null);
        }
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new HistoriaClinicaValidationException("Turno asociado no encontrado"));
        if (!consultorioId.equals(turno.getConsultorioId())) {
            throw new HistoriaClinicaValidationException("El turno no pertenece al consultorio indicado");
        }
        if (!pacienteId.equals(turno.getPacienteId())) {
            throw new HistoriaClinicaValidationException("El turno no pertenece al paciente indicado");
        }
        if (turno.getProfesionalId() != null && !profesionalId.equals(turno.getProfesionalId())) {
            throw new HistoriaClinicaValidationException("El profesional no coincide con el turno asociado");
        }
        sesionRepo.findByTurnoId(turnoId)
                .filter(existing -> currentSesionId == null || !currentSesionId.equals(existing.getId()))
                .ifPresent(existing -> {
                    throw new HistoriaClinicaConflictException("El turno ya se encuentra vinculado a otra sesion clinica");
                });
        return new TurnoLinkValidation(turno);
    }

    private UUID resolveBoxId(UUID consultorioId, UUID requestedBoxId, Turno turno) {
        UUID resolved = requestedBoxId != null ? requestedBoxId : turno != null ? turno.getBoxId() : null;
        if (resolved == null) {
            return null;
        }
        Box box = boxRepo.findById(resolved)
                .orElseThrow(() -> new HistoriaClinicaValidationException("Box no encontrado"));
        if (!consultorioId.equals(box.getConsultorioId())) {
            throw new HistoriaClinicaValidationException("El box no pertenece al consultorio indicado");
        }
        return resolved;
    }

    private Map<UUID, Paciente> loadPacientes(List<UUID> pacienteIds) {
        if (pacienteIds == null || pacienteIds.isEmpty()) {
            return Map.of();
        }
        return pacienteRepo.findByIds(pacienteIds).stream()
                .collect(Collectors.toMap(Paciente::getId, Function.identity(), (left, right) -> left));
    }

    private Map<UUID, Profesional> buildProfesionalMap(UUID consultorioId) {
        List<UUID> ids = profesionalConsultorioRepo.findByConsultorioId(consultorioId).stream()
                .filter(ProfesionalConsultorio::isActivo)
                .map(ProfesionalConsultorio::getProfesionalId)
                .distinct()
                .toList();
        if (ids.isEmpty()) {
            return profesionalRepo.findByConsultorioId(consultorioId).stream()
                    .collect(Collectors.toMap(Profesional::getId, Function.identity(), (left, right) -> left));
        }
        return ids.stream()
                .map(id -> profesionalRepo.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Profesional::getId, Function.identity(), (left, right) -> left));
    }

    private List<HistoriaClinicaWorkspaceResult.ProfesionalOption> buildWorkspaceProfesionales(Map<UUID, Profesional> profesionales) {
        return profesionales.values().stream()
                .sorted(Comparator.comparing(this::fullName, String.CASE_INSENSITIVE_ORDER))
                .map(professional -> new HistoriaClinicaWorkspaceResult.ProfesionalOption(professional.getId(), fullName(professional)))
                .toList();
    }

    private HistoriaClinicaWorkspaceItem toWorkspaceItem(SesionClinica sesion, Paciente paciente, Profesional profesional) {
        return new HistoriaClinicaWorkspaceItem(
                sesion.getId(),
                sesion.getPacienteId(),
                paciente != null ? paciente.getNombre() : "Paciente",
                paciente != null ? paciente.getApellido() : "",
                paciente != null ? paciente.getDni() : "",
                sesion.getProfesionalId(),
                fullName(profesional),
                sesion.getFechaAtencion(),
                sesion.getEstado(),
                sesion.getTipoAtencion(),
                firstNonBlank(sesion.getResumenClinico(), sesion.getMotivoConsulta(), "Sesion clinica"),
                sesion.getUpdatedAt()
        );
    }

    private SesionClinicaResult toSesionResult(SesionClinica sesion, List<AdjuntoClinico> adjuntos) {
        return new SesionClinicaResult(
                sesion.getId(),
                sesion.getConsultorioId(),
                sesion.getPacienteId(),
                sesion.getProfesionalId(),
                sesion.getTurnoId(),
                sesion.getBoxId(),
                sesion.getFechaAtencion(),
                sesion.getEstado(),
                sesion.getTipoAtencion(),
                sesion.getMotivoConsulta(),
                sesion.getResumenClinico(),
                sesion.getSubjetivo(),
                sesion.getObjetivo(),
                sesion.getEvaluacion(),
                sesion.getPlan(),
                sesion.getOrigenRegistro(),
                sesion.getCreatedByUserId(),
                sesion.getUpdatedByUserId(),
                sesion.getClosedByUserId(),
                sesion.getCreatedAt(),
                sesion.getUpdatedAt(),
                sesion.getClosedAt(),
                adjuntos.stream().map(this::toAdjuntoResult).toList()
        );
    }

    private DiagnosticoClinicoResult toDiagnosticoResult(DiagnosticoClinico diagnostico) {
        return new DiagnosticoClinicoResult(
                diagnostico.getId(),
                diagnostico.getConsultorioId(),
                diagnostico.getPacienteId(),
                diagnostico.getProfesionalId(),
                diagnostico.getSesionId(),
                diagnostico.getCodigo(),
                diagnostico.getDescripcion(),
                diagnostico.getDiagnosticoTipo(),
                diagnostico.getDiagnosticoCategoriaCodigo(),
                diagnostico.getDiagnosticoCategoriaNombre(),
                diagnostico.getDiagnosticoSubcategoria(),
                diagnostico.getDiagnosticoRegionAnatomica(),
                diagnostico.getEstado(),
                diagnostico.getFechaInicio(),
                diagnostico.getFechaFin(),
                diagnostico.getNotas(),
                diagnostico.getCreatedAt(),
                diagnostico.getUpdatedAt()
        );
    }

    private AdjuntoClinicoResult toAdjuntoResult(AdjuntoClinico adjunto) {
        return new AdjuntoClinicoResult(
                adjunto.getId(),
                adjunto.getSesionId(),
                adjunto.getAtencionInicialId(),
                adjunto.getOriginalFilename(),
                adjunto.getContentType(),
                adjunto.getSizeBytes(),
                adjunto.getCreatedAt()
        );
    }

    private HistoriaClinicaLegajoStatusResult toLegajoStatus(HistoriaClinicaLegajo legajo, boolean fallbackExists) {
        return new HistoriaClinicaLegajoStatusResult(
                legajo != null || fallbackExists,
                legajo != null ? legajo.getId() : null,
                legajo != null ? legajo.getCreatedAt() : null,
                legajo != null ? legajo.getUpdatedAt() : null
        );
    }

    private HistoriaClinicaAntecedenteResult toAntecedenteResult(HistoriaClinicaAntecedente antecedente) {
        return new HistoriaClinicaAntecedenteResult(
                antecedente.getId(),
                antecedente.getCategoryCode(),
                antecedente.getCatalogItemCode(),
                antecedente.getLabel(),
                antecedente.getValueText(),
                antecedente.isCritical(),
                antecedente.getNotes(),
                antecedente.getUpdatedAt()
        );
    }

    private HistoriaClinicaActiveCaseSummaryResult toCaseSummary(DiagnosticoClinico diagnostico,
                                                                List<SesionClinica> sesiones,
                                                                Map<UUID, Profesional> profesionales) {
        List<SesionClinica> relatedSessions = sesiones.stream()
                .filter(sesion -> sesion.getEstado() != HistoriaClinicaSesionEstado.ANULADA)
                .filter(sesion -> Objects.equals(sesion.getProfesionalId(), diagnostico.getProfesionalId()))
                .filter(sesion -> !sesion.getFechaAtencion().toLocalDate().isBefore(diagnostico.getFechaInicio()))
                .sorted(Comparator.comparing(SesionClinica::getFechaAtencion).reversed())
                .toList();
        Profesional profesional = profesionales.get(diagnostico.getProfesionalId());
        return new HistoriaClinicaActiveCaseSummaryResult(
                diagnostico.getId(),
                diagnostico.getProfesionalId(),
                fullName(profesional),
                diagnostico.getCodigo(),
                diagnostico.getDescripcion(),
                diagnostico.getEstado(),
                diagnostico.getFechaInicio(),
                relatedSessions.size(),
                relatedSessions.isEmpty()
                        ? null
                        : firstNonBlank(
                                relatedSessions.get(0).getResumenClinico(),
                                relatedSessions.get(0).getEvaluacion(),
                                relatedSessions.get(0).getMotivoConsulta()
                        )
        );
    }

    private HistoriaClinicaSesionSummaryResult toSesionSummary(SesionClinica sesion, Profesional profesional) {
        return new HistoriaClinicaSesionSummaryResult(
                sesion.getId(),
                sesion.getProfesionalId(),
                fullName(profesional),
                sesion.getFechaAtencion(),
                sesion.getEstado(),
                sesion.getTipoAtencion(),
                firstNonBlank(sesion.getResumenClinico(), sesion.getMotivoConsulta(), "Sesion clinica")
        );
    }

    private AtencionInicialSummaryResult toAtencionInicialSummary(AtencionInicial atencionInicial, Profesional profesional) {
        if (atencionInicial == null) {
            return null;
        }
        return new AtencionInicialSummaryResult(
                atencionInicial.getId(),
                atencionInicial.getProfesionalId(),
                fullName(profesional),
                atencionInicial.getFechaHora(),
                atencionInicial.getTipoIngreso(),
                atencionInicial.getMotivoConsultaBreve(),
                atencionInicial.getSintomasPrincipales(),
                atencionInicial.getTiempoEvolucion(),
                atencionInicial.getObservaciones(),
                atencionInicial.getEspecialidadDerivante(),
                atencionInicial.getProfesionalDerivante(),
                atencionInicial.getFechaPrescripcion(),
                atencionInicial.getDiagnosticoCodigo(),
                atencionInicial.getDiagnosticoNombre(),
                atencionInicial.getDiagnosticoTipo(),
                atencionInicial.getDiagnosticoCategoriaCodigo(),
                atencionInicial.getDiagnosticoCategoriaNombre(),
                atencionInicial.getDiagnosticoSubcategoria(),
                atencionInicial.getDiagnosticoRegionAnatomica(),
                atencionInicial.getDiagnosticoObservacion(),
                atencionInicial.getObservacionesPrescripcion(),
                atencionInicial.getResumenClinicoInicial(),
                atencionInicial.getHallazgosRelevantes()
        );
    }

    private AtencionInicialEvaluacionResult toAtencionInicialEvaluacionResult(AtencionInicialEvaluacion evaluacion) {
        if (evaluacion == null) {
            return null;
        }
        return new AtencionInicialEvaluacionResult(
                evaluacion.getId(),
                evaluacion.getAtencionInicialId(),
                evaluacion.getPeso(),
                evaluacion.getAltura(),
                evaluacion.getImc(),
                evaluacion.getPresionArterial(),
                evaluacion.getFrecuenciaCardiaca(),
                evaluacion.getSaturacion(),
                evaluacion.getTemperatura(),
                evaluacion.getObservaciones()
        );
    }

    private PlanTerapeuticoSummaryResult toPlanTerapeuticoSummary(PlanTerapeutico planTerapeutico,
                                                                  List<PlanTratamientoDetalle> detalles,
                                                                  Profesional profesional) {
        if (planTerapeutico == null) {
            return null;
        }
        List<PlanTratamientoDetalleResult> tratamientos = detalles.stream()
                .map(detalle -> new PlanTratamientoDetalleResult(
                        detalle.getId(),
                        detalle.getTratamientoId(),
                        detalle.getTratamientoNombreSnapshot(),
                        detalle.getTratamientoCategoriaCodigoSnapshot(),
                        detalle.getTratamientoCategoriaNombreSnapshot(),
                        detalle.getTratamientoTipoSnapshot(),
                        detalle.isTratamientoRequiereAutorizacionSnapshot(),
                        detalle.isTratamientoRequierePrescripcionMedicaSnapshot(),
                        detalle.getTratamientoDuracionSugeridaMinutosSnapshot(),
                        detalle.getCantidadSesiones(),
                        detalle.getFrecuenciaSugerida(),
                        detalle.getCaracterCaso(),
                        detalle.getFechaEstimadaInicio(),
                        detalle.isRequiereAutorizacion(),
                        detalle.getObservaciones(),
                        detalle.getObservacionesAdministrativas()
                ))
                .toList();
        return new PlanTerapeuticoSummaryResult(
                planTerapeutico.getId(),
                planTerapeutico.getAtencionInicialId(),
                planTerapeutico.getProfesionalId(),
                fullName(profesional),
                planTerapeutico.getEstado(),
                planTerapeutico.getObservacionesGenerales(),
                tratamientos
        );
    }

    private HistoriaClinicaLegajo ensureLegajoExists(UUID consultorioId, UUID pacienteId, UUID actorUserId) {
        return legajoRepo.findByConsultorioIdAndPacienteId(consultorioId, pacienteId)
                .orElseGet(() -> legajoRepo.save(new HistoriaClinicaLegajo(
                        UUID.randomUUID(),
                        consultorioId,
                        pacienteId,
                        actorUserId,
                        actorUserId,
                        Instant.now(),
                        Instant.now()
                )));
    }

    private List<HistoriaClinicaAntecedenteResult> persistAntecedentes(HistoriaClinicaLegajo legajo,
                                                                       List<HistoriaClinicaAntecedenteItemCommand> antecedentes,
                                                                       UUID actorUserId) {
        List<HistoriaClinicaAntecedenteItemCommand> normalized = antecedentes == null ? List.of() : antecedentes.stream()
                .filter(Objects::nonNull)
                .filter(item -> hasText(item.label()))
                .toList();
        antecedenteRepo.deleteByConsultorioIdAndPacienteId(legajo.getConsultorioId(), legajo.getPacienteId());
        List<HistoriaClinicaAntecedente> saved = normalized.isEmpty()
                ? List.of()
                : antecedenteRepo.saveAll(normalized.stream()
                        .map(item -> new HistoriaClinicaAntecedente(
                                UUID.randomUUID(),
                                legajo.getId(),
                                legajo.getConsultorioId(),
                                legajo.getPacienteId(),
                                trimToNull(item.categoryCode()),
                                trimToNull(item.catalogItemCode()),
                                item.label().trim(),
                                trimToNull(item.valueText()),
                                item.critical(),
                                trimToNull(item.notes()),
                                actorUserId,
                                actorUserId,
                                Instant.now(),
                                Instant.now()
                        ))
                        .toList());
        legajo.touch(actorUserId);
        legajoRepo.save(legajo);
        return saved.stream()
                .sorted(Comparator.comparing(HistoriaClinicaAntecedente::isCritical).reversed()
                        .thenComparing(HistoriaClinicaAntecedente::getUpdatedAt, Comparator.reverseOrder()))
                .map(this::toAntecedenteResult)
                .toList();
    }

    private List<AdjuntoClinico> loadAdjuntosForSesiones(List<SesionClinica> sesiones) {
        List<UUID> sesionIds = sesiones.stream().map(SesionClinica::getId).toList();
        if (sesionIds.isEmpty()) {
            return List.of();
        }
        return adjuntoRepo.findBySesionIds(sesionIds);
    }

    private String resolveProfesionalHabitual(List<SesionClinica> sesiones, Map<UUID, Profesional> profesionales) {
        return sesiones.stream()
                .filter(sesion -> sesion.getEstado() != HistoriaClinicaSesionEstado.ANULADA)
                .collect(Collectors.groupingBy(SesionClinica::getProfesionalId, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(profesionales::get)
                .map(this::fullName)
                .orElse(null);
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    private String tipoToTimelineLabel(SesionClinica sesion) {
        if (sesion.getTipoAtencion() == null) {
            return "clinica";
        }
        return switch (sesion.getTipoAtencion()) {
            case EVALUACION -> "de evaluacion inicial";
            case TRATAMIENTO -> "de tratamiento";
            case INTERCONSULTA -> "de interconsulta";
            case OTRO -> "clinica";
            case SEGUIMIENTO -> "de seguimiento";
        };
    }

    private boolean matchesTimelineType(String requestedType, HistoriaClinicaTimelineEventType eventType) {
        if (requestedType == null || requestedType.isBlank() || "all".equalsIgnoreCase(requestedType)) {
            return true;
        }
        return switch (requestedType.toLowerCase(Locale.ROOT)) {
            case "sessions", "sesiones" -> eventType == HistoriaClinicaTimelineEventType.SESION;
            case "cases", "casos" -> eventType == HistoriaClinicaTimelineEventType.CASO_OPENED
                    || eventType == HistoriaClinicaTimelineEventType.CASO_CLOSED;
            case "antecedents", "antecedentes" -> eventType == HistoriaClinicaTimelineEventType.ANTECEDENTE_UPDATED;
            case "attachments", "adjuntos" -> eventType == HistoriaClinicaTimelineEventType.ADJUNTO;
            case "initial", "inicial" -> eventType == HistoriaClinicaTimelineEventType.ATENCION_INICIAL
                    || eventType == HistoriaClinicaTimelineEventType.PLAN_TERAPEUTICO;
            default -> true;
        };
    }

    private void validateAtencionInicialPayload(CreateAtencionInicialCommand command) {
        if (command.profesionalId() == null || command.fechaHora() == null || command.tipoIngreso() == null) {
            throw new HistoriaClinicaValidationException("Debe indicar profesional, fecha y tipo de ingreso");
        }
        if (!hasText(command.motivoConsultaBreve()) && !hasUsefulPrescriptionData(command)) {
            throw new HistoriaClinicaValidationException("Debe indicar motivo breve o datos utiles de prescripcion");
        }
        if (command.tratamientos() == null || command.tratamientos().isEmpty()) {
            throw new HistoriaClinicaValidationException("Debe indicar al menos un tratamiento para el plan terapeutico");
        }
        boolean invalidTratamiento = command.tratamientos().stream().anyMatch(item ->
                item == null
                        || !hasText(item.tratamientoId())
                        || item.cantidadSesiones() <= 0
                        || item.caracterCaso() == null
        );
        if (invalidTratamiento) {
            throw new HistoriaClinicaValidationException("El plan terapeutico contiene tratamientos invalidos");
        }
    }

    private boolean hasUsefulPrescriptionData(CreateAtencionInicialCommand command) {
        return hasText(command.especialidadDerivante())
                || hasText(command.profesionalDerivante())
                || command.fechaPrescripcion() != null
                || hasText(command.diagnosticoCodigo())
                || hasText(command.diagnosticoObservacion())
                || hasText(command.observacionesPrescripcion());
    }

    private boolean hasEvaluacionPayload(CreateAtencionInicialCommand command) {
        return command.evaluacion() != null
                && (command.evaluacion().peso() != null
                || command.evaluacion().altura() != null
                || command.evaluacion().imc() != null
                || hasText(command.evaluacion().presionArterial())
                || command.evaluacion().frecuenciaCardiaca() != null
                || command.evaluacion().saturacion() != null
                || command.evaluacion().temperatura() != null
                || hasText(command.evaluacion().observaciones()));
    }

    private BigDecimal resolveImc(BigDecimal peso, BigDecimal altura, BigDecimal requestedImc) {
        if (requestedImc != null) {
            return requestedImc;
        }
        if (peso == null || altura == null || altura.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        BigDecimal alturaMetros = altura.compareTo(BigDecimal.valueOf(3)) > 0
                ? altura.divide(BigDecimal.valueOf(100))
                : altura;
        BigDecimal divisor = alturaMetros.multiply(alturaMetros);
        return divisor.compareTo(BigDecimal.ZERO) == 0 ? null : peso.divide(divisor, 2, java.math.RoundingMode.HALF_UP);
    }

    private String buildInitialCaseDescription(List<PlanTratamientoDetalle> detalles,
                                               CreateAtencionInicialCommand command,
                                               ConsultorioDiagnosticosMedicosService.DiagnosticoMedicoSnapshot diagnosticoSeleccionado) {
        if (detalles != null && !detalles.isEmpty()) {
            return summarizePlanTratamientos(detalles);
        }
        return firstNonBlank(
                diagnosticoSeleccionado != null ? diagnosticoSeleccionado.nombre() : null,
                command.motivoConsultaBreve(),
                "Plan terapeutico inicial"
        );
    }

    private ConsultorioDiagnosticosMedicosService.DiagnosticoMedicoSnapshot resolveAtencionInicialDiagnostico(
            CreateAtencionInicialCommand command) {
        String diagnosticoCodigo = trimToNull(command.diagnosticoCodigo());
        if (command.tipoIngreso() == AtencionInicialTipoIngreso.CON_PRESCRIPCION && diagnosticoCodigo == null) {
            throw new HistoriaClinicaValidationException("Debe seleccionar un diagnostico medico para la prescripcion");
        }
        if (diagnosticoCodigo == null) {
            return null;
        }
        return diagnosticosMedicosService.requireActiveDiagnostico(command.consultorioId(), diagnosticoCodigo);
    }

    private String summarizePlanTratamientos(List<PlanTratamientoDetalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return "Plan terapeutico inicial";
        }
        return detalles.stream()
                .map(PlanTratamientoDetalle::getTratamientoNombreSnapshot)
                .filter(Objects::nonNull)
                .limit(3)
                .collect(Collectors.joining(", "));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean matchesWorkspaceQuery(SesionClinica sesion, Paciente paciente, String q) {
        if (q == null || q.isBlank()) {
            return true;
        }
        String normalizedQuery = normalize(q);
        return containsNormalized(paciente != null ? paciente.getNombre() : null, normalizedQuery)
                || containsNormalized(paciente != null ? paciente.getApellido() : null, normalizedQuery)
                || containsNormalized(paciente != null ? paciente.getDni() : null, normalizedQuery)
                || containsNormalized(sesion.getMotivoConsulta(), normalizedQuery)
                || containsNormalized(sesion.getResumenClinico(), normalizedQuery)
                || containsNormalized(sesion.getEvaluacion(), normalizedQuery)
                || containsNormalized(sesion.getPlan(), normalizedQuery);
    }

    private String sanitizeFilename(String originalFilename) {
        String safe = originalFilename == null ? "" : originalFilename.replace("\\", "/");
        int slash = safe.lastIndexOf('/');
        safe = slash >= 0 ? safe.substring(slash + 1) : safe;
        return safe.isBlank() ? "adjunto-clinico" : safe;
    }

    private String sanitizeContentType(String contentType) {
        return contentType == null || contentType.isBlank() ? "application/octet-stream" : contentType;
    }

    private void validateAttachment(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new HistoriaClinicaValidationException("Debe adjuntar un archivo");
        }
        String extension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_ATTACHMENT_EXTENSIONS.contains(extension)) {
            throw new HistoriaClinicaValidationException("Formato de adjunto no permitido");
        }
        if (file.getSize() > MAX_ATTACHMENT_BYTES) {
            throw new HistoriaClinicaValidationException("El adjunto supera el limite de 10 MB");
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return filename.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }

    private String fullName(Profesional profesional) {
        if (profesional == null) {
            return "Profesional";
        }
        return firstNonBlank(
                (Objects.toString(profesional.getNombre(), "") + " " + Objects.toString(profesional.getApellido(), "")).trim(),
                profesional.getEmail(),
                "Profesional"
        );
    }

    private String firstNonBlank(String primary, String secondary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        if (secondary != null && !secondary.isBlank()) {
            return secondary;
        }
        return fallback;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalize(String value) {
        return Normalizer.normalize(Objects.toString(value, ""), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private boolean containsNormalized(String source, String normalizedQuery) {
        return normalize(source).contains(normalizedQuery);
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

    private record TurnoLinkValidation(Turno turno) {
    }
}
