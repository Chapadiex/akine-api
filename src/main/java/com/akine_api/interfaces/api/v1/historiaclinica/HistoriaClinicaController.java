package com.akine_api.interfaces.api.v1.historiaclinica;

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
import com.akine_api.application.dto.result.DiagnosticoClinicoResult;
import com.akine_api.application.dto.result.HistoriaClinicaAntecedenteResult;
import com.akine_api.application.dto.result.HistoriaClinicaOverviewResult;
import com.akine_api.application.dto.result.HistoriaClinicaPacienteResult;
import com.akine_api.application.dto.result.HistoriaClinicaTimelineEventResult;
import com.akine_api.application.dto.result.HistoriaClinicaWorkspaceResult;
import com.akine_api.application.dto.result.SesionClinicaResult;
import com.akine_api.application.service.HistoriaClinicaService;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.interfaces.api.v1.historiaclinica.dto.AtencionInicialEvaluacionRequest;
import com.akine_api.interfaces.api.v1.historiaclinica.dto.CreateAtencionInicialRequest;
import com.akine_api.interfaces.api.v1.historiaclinica.dto.CreateHistoriaClinicaLegajoRequest;
import com.akine_api.interfaces.api.v1.historiaclinica.dto.DiagnosticoClinicoEstadoRequest;
import com.akine_api.interfaces.api.v1.historiaclinica.dto.DiagnosticoClinicoRequest;
import com.akine_api.interfaces.api.v1.historiaclinica.dto.HistoriaClinicaAntecedenteItemRequest;
import com.akine_api.interfaces.api.v1.historiaclinica.dto.HistoriaClinicaAntecedentesUpdateRequest;
import com.akine_api.interfaces.api.v1.historiaclinica.dto.PlanTratamientoDetalleRequest;
import com.akine_api.interfaces.api.v1.historiaclinica.dto.SesionClinicaRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/historia-clinica")
public class HistoriaClinicaController {

    private final HistoriaClinicaService service;

    public HistoriaClinicaController(HistoriaClinicaService service) {
        this.service = service;
    }

    @GetMapping("/workspace")
    public ResponseEntity<HistoriaClinicaWorkspaceResult> workspace(
            @PathVariable UUID consultorioId,
            @RequestParam(required = false) UUID pacienteId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID profesionalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) HistoriaClinicaSesionEstado estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getWorkspace(
                consultorioId, pacienteId, q, profesionalId, from, to, estado, page, size,
                principal.getUsername(), roles(principal)
        ));
    }

    @GetMapping("/pacientes/{pacienteId}")
    public ResponseEntity<HistoriaClinicaPacienteResult> paciente(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getPaciente(
                consultorioId, pacienteId, principal.getUsername(), roles(principal)
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/overview")
    public ResponseEntity<HistoriaClinicaOverviewResult> overview(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getOverview(
                consultorioId, pacienteId, principal.getUsername(), roles(principal)
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/legajo")
    public ResponseEntity<HistoriaClinicaOverviewResult> createLegajo(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @Valid @RequestBody CreateHistoriaClinicaLegajoRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createLegajo(
                new CreateHistoriaClinicaLegajoCommand(
                        consultorioId,
                        pacienteId,
                        request.profesionalId(),
                        request.fechaAtencion(),
                        request.motivoConsulta(),
                        request.resumenClinico(),
                        request.subjetivo(),
                        request.objetivo(),
                        request.evaluacion(),
                        request.plan(),
                        request.casoCodigo(),
                        request.casoDescripcion(),
                        request.casoFechaInicio(),
                        request.casoNotas(),
                        toAntecedenteCommands(request.antecedentes()),
                        null
                ),
                principal.getUsername(),
                roles(principal)
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/atencion-inicial")
    public ResponseEntity<HistoriaClinicaOverviewResult> createAtencionInicial(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @Valid @RequestBody CreateAtencionInicialRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAtencionInicial(
                new CreateAtencionInicialCommand(
                        consultorioId,
                        pacienteId,
                        request.profesionalId(),
                        request.fechaHora(),
                        request.tipoIngreso(),
                        request.motivoConsultaBreve(),
                        request.sintomasPrincipales(),
                        request.tiempoEvolucion(),
                        request.observaciones(),
                        request.especialidadDerivante(),
                        request.profesionalDerivante(),
                        request.fechaPrescripcion(),
                        request.diagnosticoCodigo(),
                        request.diagnosticoObservacion(),
                        request.observacionesPrescripcion(),
                        toEvaluacionCommand(request.evaluacion()),
                        request.resumenClinicoInicial(),
                        request.hallazgosRelevantes(),
                        toAntecedenteCommands(request.antecedentes()),
                        request.planObservacionesGenerales(),
                        toPlanTratamientoCommands(request.tratamientos()),
                        null
                ),
                principal.getUsername(),
                roles(principal)
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/antecedentes")
    public ResponseEntity<List<HistoriaClinicaAntecedenteResult>> antecedentes(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getAntecedentes(
                consultorioId, pacienteId, principal.getUsername(), roles(principal)
        ));
    }

    @PutMapping("/pacientes/{pacienteId}/antecedentes")
    public ResponseEntity<List<HistoriaClinicaAntecedenteResult>> updateAntecedentes(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @Valid @RequestBody HistoriaClinicaAntecedentesUpdateRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.updateAntecedentes(
                new UpdateHistoriaClinicaAntecedentesCommand(
                        consultorioId,
                        pacienteId,
                        toAntecedenteCommands(request.antecedentes()),
                        null
                ),
                principal.getUsername(),
                roles(principal)
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/timeline")
    public ResponseEntity<List<HistoriaClinicaTimelineEventResult>> timeline(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @RequestParam(defaultValue = "all") String type,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getTimeline(
                consultorioId, pacienteId, type, principal.getUsername(), roles(principal)
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/sesiones")
    public ResponseEntity<List<SesionClinicaResult>> sesiones(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID profesionalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) HistoriaClinicaSesionEstado estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.listSesiones(
                consultorioId, pacienteId, profesionalId, from, to, estado, page, size,
                principal.getUsername(), roles(principal)
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/sesiones/{sesionId}")
    public ResponseEntity<SesionClinicaResult> sesion(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @PathVariable UUID sesionId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.getSesion(
                consultorioId, pacienteId, sesionId, principal.getUsername(), roles(principal)
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/sesiones")
    public ResponseEntity<SesionClinicaResult> createSesion(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @Valid @RequestBody SesionClinicaRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSesion(
                new CreateSesionClinicaCommand(
                        consultorioId,
                        pacienteId,
                        request.profesionalId(),
                        request.turnoId(),
                        request.casoAtencionId(),
                        request.boxId(),
                        request.fechaAtencion(),
                        request.tipoAtencion(),
                        request.motivoConsulta(),
                        request.resumenClinico(),
                        request.subjetivo(),
                        request.objetivo(),
                        request.evaluacion(),
                        request.plan(),
                        toEvaluacionDTO(request.evaluacionEstructurada()),
                        toExamenDTO(request.examenFisico()),
                        toIntervencionDTOs(request.intervenciones()),
                        null
                ),
                principal.getUsername(),
                roles(principal)
        ));
    }

    @PutMapping("/pacientes/{pacienteId}/sesiones/{sesionId}")
    public ResponseEntity<SesionClinicaResult> updateSesion(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @PathVariable UUID sesionId,
            @Valid @RequestBody SesionClinicaRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.updateSesion(
                new UpdateSesionClinicaCommand(
                        consultorioId,
                        pacienteId,
                        request.profesionalId(),
                        sesionId,
                        request.turnoId(),
                        request.boxId(),
                        request.fechaAtencion(),
                        request.tipoAtencion(),
                        request.motivoConsulta(),
                        request.resumenClinico(),
                        request.subjetivo(),
                        request.objetivo(),
                        request.evaluacion(),
                        request.plan(),
                        toEvaluacionDTO(request.evaluacionEstructurada()),
                        toExamenDTO(request.examenFisico()),
                        toIntervencionDTOs(request.intervenciones()),
                        null
                ),
                principal.getUsername(),
                roles(principal)
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/sesiones/{sesionId}/cerrar")
    public ResponseEntity<SesionClinicaResult> cerrarSesion(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @PathVariable UUID sesionId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.closeSesion(
                new ChangeSesionClinicaEstadoCommand(consultorioId, pacienteId, sesionId, null),
                principal.getUsername(),
                roles(principal)
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/sesiones/{sesionId}/anular")
    public ResponseEntity<SesionClinicaResult> anularSesion(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @PathVariable UUID sesionId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.annulSesion(
                new ChangeSesionClinicaEstadoCommand(consultorioId, pacienteId, sesionId, null),
                principal.getUsername(),
                roles(principal)
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/diagnosticos")
    public ResponseEntity<List<DiagnosticoClinicoResult>> diagnosticos(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.listDiagnosticos(
                consultorioId, pacienteId, principal.getUsername(), roles(principal)
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/diagnosticos")
    public ResponseEntity<DiagnosticoClinicoResult> createDiagnostico(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @Valid @RequestBody DiagnosticoClinicoRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDiagnostico(
                new CreateDiagnosticoClinicoCommand(
                        consultorioId,
                        pacienteId,
                        request.profesionalId(),
                        request.sesionId(),
                        request.diagnosticoCodigo(),
                        request.fechaInicio(),
                        request.notas(),
                        null
                ),
                principal.getUsername(),
                roles(principal)
        ));
    }

    @PutMapping("/pacientes/{pacienteId}/diagnosticos/{diagnosticoId}")
    public ResponseEntity<DiagnosticoClinicoResult> updateDiagnostico(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @PathVariable UUID diagnosticoId,
            @Valid @RequestBody DiagnosticoClinicoRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.updateDiagnostico(
                new UpdateDiagnosticoClinicoCommand(
                        consultorioId,
                        pacienteId,
                        diagnosticoId,
                        request.profesionalId(),
                        request.sesionId(),
                        request.diagnosticoCodigo(),
                        request.fechaInicio(),
                        request.notas(),
                        null
                ),
                principal.getUsername(),
                roles(principal)
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/diagnosticos/{diagnosticoId}/resolver")
    public ResponseEntity<DiagnosticoClinicoResult> resolverDiagnostico(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @PathVariable UUID diagnosticoId,
            @Valid @RequestBody DiagnosticoClinicoEstadoRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.resolveDiagnostico(
                new ResolveDiagnosticoClinicoCommand(
                        consultorioId,
                        pacienteId,
                        diagnosticoId,
                        request.fechaFin(),
                        null
                ),
                principal.getUsername(),
                roles(principal)
        ));
    }

    @PostMapping("/pacientes/{pacienteId}/diagnosticos/{diagnosticoId}/descartar")
    public ResponseEntity<DiagnosticoClinicoResult> descartarDiagnostico(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @PathVariable UUID diagnosticoId,
            @Valid @RequestBody DiagnosticoClinicoEstadoRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(service.discardDiagnostico(
                new DiscardDiagnosticoClinicoCommand(
                        consultorioId,
                        pacienteId,
                        diagnosticoId,
                        request.fechaFin(),
                        null
                ),
                principal.getUsername(),
                roles(principal)
        ));
    }

    @PostMapping(path = "/pacientes/{pacienteId}/sesiones/{sesionId}/adjuntos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdjuntoClinicoResult> uploadAdjunto(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @PathVariable UUID sesionId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addAdjunto(
                consultorioId, pacienteId, sesionId, file, principal.getUsername(), roles(principal)
        ));
    }

    @PostMapping(path = "/pacientes/{pacienteId}/atenciones-iniciales/{atencionInicialId}/adjuntos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdjuntoClinicoResult> uploadAdjuntoAtencionInicial(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @PathVariable UUID atencionInicialId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addAdjuntoAtencionInicial(
                consultorioId, pacienteId, atencionInicialId, file, principal.getUsername(), roles(principal)
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/adjuntos/{adjuntoId}")
    public ResponseEntity<byte[]> downloadAdjunto(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @PathVariable UUID adjuntoId,
            @AuthenticationPrincipal UserDetails principal) {
        AdjuntoClinicoDownloadResult result = service.downloadAdjunto(
                consultorioId, pacienteId, adjuntoId, principal.getUsername(), roles(principal)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(result.contentType()));
        headers.setContentDisposition(ContentDisposition.attachment().filename(result.originalFilename()).build());
        headers.setContentLength(result.sizeBytes());
        return new ResponseEntity<>(result.content(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/pacientes/{pacienteId}/adjuntos/{adjuntoId}")
    public ResponseEntity<Void> deleteAdjunto(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @PathVariable UUID adjuntoId,
            @AuthenticationPrincipal UserDetails principal) {
        service.deleteAdjunto(
                consultorioId, pacienteId, adjuntoId, principal.getUsername(), roles(principal)
        );
        return ResponseEntity.noContent().build();
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }

    private List<HistoriaClinicaAntecedenteItemCommand> toAntecedenteCommands(List<HistoriaClinicaAntecedenteItemRequest> requests) {
        if (requests == null) {
            return List.of();
        }
        return requests.stream()
                .filter(Objects::nonNull)
                .map(request -> new HistoriaClinicaAntecedenteItemCommand(
                        request.categoryCode(),
                        request.catalogItemCode(),
                        request.label(),
                        request.valueText(),
                        request.critical(),
                        request.notes()
                ))
                .toList();
    }

    private com.akine_api.application.dto.command.AtencionInicialEvaluacionCommand toEvaluacionCommand(AtencionInicialEvaluacionRequest request) {
        if (request == null) {
            return null;
        }
        return new com.akine_api.application.dto.command.AtencionInicialEvaluacionCommand(
                request.peso(),
                request.altura(),
                request.imc(),
                request.presionArterial(),
                request.frecuenciaCardiaca(),
                request.saturacion(),
                request.temperatura(),
                request.observaciones()
        );
    }

    private List<PlanTratamientoDetalleCommand> toPlanTratamientoCommands(List<PlanTratamientoDetalleRequest> requests) {
        if (requests == null) {
            return List.of();
        }
        return requests.stream()
                .filter(Objects::nonNull)
                .map(request -> new PlanTratamientoDetalleCommand(
                        request.tratamientoId(),
                        request.cantidadSesiones(),
                        request.frecuenciaSugerida(),
                        request.caracterCaso(),
                        request.fechaEstimadaInicio(),
                        request.requiereAutorizacion(),
                        request.observaciones(),
                        request.observacionesAdministrativas()
                ))
                .toList();
    }

    private com.akine_api.application.dto.command.SesionEvaluacionDTO toEvaluacionDTO(com.akine_api.interfaces.api.v1.historiaclinica.dto.SesionEvaluacionRequest request) {
        if (request == null) return null;
        return new com.akine_api.application.dto.command.SesionEvaluacionDTO(
                request.dolorIntensidad(),
                request.dolorZona(),
                request.dolorLateralidad(),
                request.dolorTipo(),
                request.dolorComportamiento(),
                request.evolucionEstado(),
                request.evolucionNota(),
                request.objetivoSesion(),
                request.limitacionFuncional(),
                request.respuestaPaciente(),
                request.tolerancia(),
                request.indicacionesDomiciliarias(),
                request.proximaConducta()
        );
    }

    private com.akine_api.application.dto.command.SesionExamenFisicoDTO toExamenDTO(com.akine_api.interfaces.api.v1.historiaclinica.dto.SesionExamenFisicoRequest request) {
        if (request == null) return null;
        return new com.akine_api.application.dto.command.SesionExamenFisicoDTO(
                request.rangoMovimientoJson(),
                request.fuerzaMuscularJson(),
                request.funcionalidadNota(),
                request.marchaBalanceNota(),
                request.signosInflamatorios(),
                request.observacionesNeuroResp(),
                request.testsMedidasJson()
        );
    }

    private List<com.akine_api.application.dto.command.SesionIntervencionDTO> toIntervencionDTOs(List<com.akine_api.interfaces.api.v1.historiaclinica.dto.SesionIntervencionRequest> requests) {
        if (requests == null) return List.of();
        return requests.stream()
                .filter(Objects::nonNull)
                .map(request -> new com.akine_api.application.dto.command.SesionIntervencionDTO(
                        request.tratamientoId(),
                        request.tratamientoNombre(),
                        request.técnica(),
                        request.zona(),
                        request.parametrosJson(),
                        request.duracionMinutos(),
                        request.profesionalId(),
                        request.observaciones(),
                        request.orderIndex()
                ))
                .toList();
    }
}
