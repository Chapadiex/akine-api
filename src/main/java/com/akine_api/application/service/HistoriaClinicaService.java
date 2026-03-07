package com.akine_api.application.service;

import com.akine_api.application.dto.command.ChangeSesionClinicaEstadoCommand;
import com.akine_api.application.dto.command.CreateDiagnosticoClinicoCommand;
import com.akine_api.application.dto.command.CreateSesionClinicaCommand;
import com.akine_api.application.dto.command.DiscardDiagnosticoClinicoCommand;
import com.akine_api.application.dto.command.ResolveDiagnosticoClinicoCommand;
import com.akine_api.application.dto.command.UpdateDiagnosticoClinicoCommand;
import com.akine_api.application.dto.command.UpdateSesionClinicaCommand;
import com.akine_api.application.dto.result.AdjuntoClinicoDownloadResult;
import com.akine_api.application.dto.result.AdjuntoClinicoResult;
import com.akine_api.application.dto.result.DiagnosticoClinicoResult;
import com.akine_api.application.dto.result.HistoriaClinicaPacienteResult;
import com.akine_api.application.dto.result.HistoriaClinicaWorkspaceItem;
import com.akine_api.application.dto.result.HistoriaClinicaWorkspaceResult;
import com.akine_api.application.dto.result.SesionClinicaResult;
import com.akine_api.application.port.output.AdjuntoClinicoRepositoryPort;
import com.akine_api.application.port.output.AttachmentStoragePort;
import com.akine_api.application.port.output.BoxRepositoryPort;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.DiagnosticoClinicoRepositoryPort;
import com.akine_api.application.port.output.PacienteConsultorioRepositoryPort;
import com.akine_api.application.port.output.PacienteRepositoryPort;
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
import com.akine_api.domain.model.Box;
import com.akine_api.domain.model.DiagnosticoClinico;
import com.akine_api.domain.model.DiagnosticoClinicoEstado;
import com.akine_api.domain.model.HistoriaClinicaOrigenRegistro;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.Paciente;
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
import java.time.Instant;
import java.time.LocalDate;
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
    private final AttachmentStoragePort attachmentStorage;
    private final PacienteRepositoryPort pacienteRepo;
    private final PacienteConsultorioRepositoryPort pacienteConsultorioRepo;
    private final ConsultorioRepositoryPort consultorioRepo;
    private final ProfesionalRepositoryPort profesionalRepo;
    private final ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo;
    private final TurnoRepositoryPort turnoRepo;
    private final UserRepositoryPort userRepo;
    private final BoxRepositoryPort boxRepo;

    public HistoriaClinicaService(SesionClinicaRepositoryPort sesionRepo,
                                  DiagnosticoClinicoRepositoryPort diagnosticoRepo,
                                  AdjuntoClinicoRepositoryPort adjuntoRepo,
                                  AttachmentStoragePort attachmentStorage,
                                  PacienteRepositoryPort pacienteRepo,
                                  PacienteConsultorioRepositoryPort pacienteConsultorioRepo,
                                  ConsultorioRepositoryPort consultorioRepo,
                                  ProfesionalRepositoryPort profesionalRepo,
                                  ProfesionalConsultorioRepositoryPort profesionalConsultorioRepo,
                                  TurnoRepositoryPort turnoRepo,
                                  UserRepositoryPort userRepo,
                                  BoxRepositoryPort boxRepo) {
        this.sesionRepo = sesionRepo;
        this.diagnosticoRepo = diagnosticoRepo;
        this.adjuntoRepo = adjuntoRepo;
        this.attachmentStorage = attachmentStorage;
        this.pacienteRepo = pacienteRepo;
        this.pacienteConsultorioRepo = pacienteConsultorioRepo;
        this.consultorioRepo = consultorioRepo;
        this.profesionalRepo = profesionalRepo;
        this.profesionalConsultorioRepo = profesionalConsultorioRepo;
        this.turnoRepo = turnoRepo;
        this.userRepo = userRepo;
        this.boxRepo = boxRepo;
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
        validateProfesionalCanWrite(command.consultorioId(), command.profesionalId(), userEmail, roles);
        if (command.sesionId() != null) {
            SesionClinica sesion = loadSesion(command.consultorioId(), command.pacienteId(), command.sesionId());
            if (!command.profesionalId().equals(sesion.getProfesionalId())) {
                throw new HistoriaClinicaValidationException("El diagnostico debe pertenecer al mismo profesional de la sesion");
            }
        }
        DiagnosticoClinico diagnostico = new DiagnosticoClinico(
                UUID.randomUUID(),
                command.consultorioId(),
                command.pacienteId(),
                command.profesionalId(),
                command.sesionId(),
                trimToNull(command.codigo()),
                trimToNull(command.descripcion()),
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
        diagnostico.update(
                command.profesionalId(),
                command.sesionId(),
                trimToNull(command.codigo()),
                trimToNull(command.descripcion()),
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
        SesionClinica sesion = loadSesion(consultorioId, pacienteId, adjunto.getSesionId());
        assertCanMutateSesion(sesion, userEmail, roles);
        attachmentStorage.delete(adjunto.getStorageKey());
        adjuntoRepo.deleteById(adjuntoId);
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
                adjunto.getOriginalFilename(),
                adjunto.getContentType(),
                adjunto.getSizeBytes(),
                adjunto.getCreatedAt()
        );
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
