package com.akine_api.interfaces.api.v1.casoatencion;

import com.akine_api.application.dto.command.CambiarEstadoCasoAtencionCommand;
import com.akine_api.application.dto.command.CreateCasoAtencionCommand;
import com.akine_api.application.dto.command.UpdateCasoAtencionCommand;
import com.akine_api.application.dto.result.AdjuntoClinicoDownloadResult;
import com.akine_api.application.dto.result.AdjuntoClinicoResult;
import com.akine_api.application.dto.result.CasoAtencionResult;
import com.akine_api.application.dto.result.CasoAtencionSummaryResult;
import com.akine_api.application.service.CasoAtencionService;
import com.akine_api.interfaces.api.v1.casoatencion.dto.CambiarEstadoCasoAtencionRequest;
import com.akine_api.interfaces.api.v1.casoatencion.dto.CreateCasoAtencionRequest;
import com.akine_api.interfaces.api.v1.casoatencion.dto.UpdateCasoAtencionRequest;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class CasoAtencionController {

    private final CasoAtencionService service;

    public CasoAtencionController(CasoAtencionService service) {
        this.service = service;
    }

    // POST /api/v1/consultorios/{consultorioId}/historia-clinica/legajos/{legajoId}/casos
    @PostMapping("/api/v1/consultorios/{consultorioId}/historia-clinica/legajos/{legajoId}/casos")
    public ResponseEntity<CasoAtencionResult> create(
            @PathVariable UUID consultorioId,
            @PathVariable UUID legajoId,
            @Valid @RequestBody CreateCasoAtencionRequest req,
            @AuthenticationPrincipal UserDetails principal) {

        CasoAtencionResult result = service.createCasoAtencion(
                consultorioId,
                new CreateCasoAtencionCommand(
                        legajoId,
                        req.pacienteId(),
                        req.profesionalResponsableId(),
                        req.tipoOrigen() != null ? req.tipoOrigen() : "CONSULTA_DIRECTA",
                        req.fechaApertura() != null ? req.fechaApertura() : LocalDateTime.now(),
                        req.motivoConsulta(),
                        req.diagnosticoMedico(),
                        req.diagnosticoFuncional(),
                        req.afeccionPrincipal(),
                        req.coberturaId(),
                        req.prioridad() != null ? req.prioridad() : "NORMAL"
                ),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // GET /api/v1/consultorios/{consultorioId}/historia-clinica/legajos/{legajoId}/casos
    @GetMapping("/api/v1/consultorios/{consultorioId}/historia-clinica/legajos/{legajoId}/casos")
    public ResponseEntity<List<CasoAtencionSummaryResult>> listByLegajo(
            @PathVariable UUID consultorioId,
            @PathVariable UUID legajoId,
            @AuthenticationPrincipal UserDetails principal) {

        List<CasoAtencionSummaryResult> result = service.getCasosPorLegajo(
                legajoId, consultorioId, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(result);
    }

    // GET /api/v1/consultorios/{consultorioId}/casos-atencion/{id}
    @GetMapping("/api/v1/consultorios/{consultorioId}/casos-atencion/{id}")
    public ResponseEntity<CasoAtencionResult> getById(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {

        CasoAtencionResult result = service.getCasoAtencion(
                id, consultorioId, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(result);
    }

    // PUT /api/v1/consultorios/{consultorioId}/casos-atencion/{id}
    @PutMapping("/api/v1/consultorios/{consultorioId}/casos-atencion/{id}")
    public ResponseEntity<CasoAtencionResult> update(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCasoAtencionRequest req,
            @AuthenticationPrincipal UserDetails principal) {

        CasoAtencionResult result = service.updateCasoAtencion(
                id,
                consultorioId,
                new UpdateCasoAtencionCommand(
                        req.profesionalResponsableId(),
                        req.motivoConsulta(),
                        req.diagnosticoMedico(),
                        req.diagnosticoFuncional(),
                        req.afeccionPrincipal(),
                        req.prioridad()
                ),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(result);
    }

    // PATCH /api/v1/consultorios/{consultorioId}/casos-atencion/{id}/estado
    @PatchMapping("/api/v1/consultorios/{consultorioId}/casos-atencion/{id}/estado")
    public ResponseEntity<CasoAtencionResult> cambiarEstado(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody CambiarEstadoCasoAtencionRequest req,
            @AuthenticationPrincipal UserDetails principal) {

        CasoAtencionResult result = service.cambiarEstado(
                id,
                consultorioId,
                new CambiarEstadoCasoAtencionCommand(req.nuevoEstado()),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/api/v1/consultorios/{consultorioId}/casos-atencion/{id}/adjuntos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdjuntoClinicoResult> uploadAdjunto(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addAdjunto(
                consultorioId, id, file, principal.getUsername(), roles(principal)
        ));
    }

    @GetMapping("/api/v1/consultorios/{consultorioId}/casos-atencion/{id}/adjuntos/{adjuntoId}")
    public ResponseEntity<byte[]> downloadAdjunto(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @PathVariable UUID adjuntoId,
            @AuthenticationPrincipal UserDetails principal) {
        AdjuntoClinicoDownloadResult result = service.downloadAdjunto(
                consultorioId, id, adjuntoId, principal.getUsername(), roles(principal)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(result.contentType()));
        headers.setContentDisposition(ContentDisposition.attachment().filename(result.originalFilename()).build());
        headers.setContentLength(result.sizeBytes());
        return new ResponseEntity<>(result.content(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/api/v1/consultorios/{consultorioId}/casos-atencion/{id}/adjuntos/{adjuntoId}")
    public ResponseEntity<Void> deleteAdjunto(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @PathVariable UUID adjuntoId,
            @AuthenticationPrincipal UserDetails principal) {
        service.deleteAdjunto(
                consultorioId, id, adjuntoId, principal.getUsername(), roles(principal)
        );
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/consultorios/{consultorioId}/pacientes/{pacienteId}/casos-activos
    @GetMapping("/api/v1/consultorios/{consultorioId}/pacientes/{pacienteId}/casos-activos")
    public ResponseEntity<List<CasoAtencionSummaryResult>> casosActivosPorPaciente(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @AuthenticationPrincipal UserDetails principal) {

        List<CasoAtencionSummaryResult> result = service.getCasosActivosPorPaciente(
                pacienteId, consultorioId, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(result);
    }

    // GET /api/v1/consultorios/{consultorioId}/pacientes/{pacienteId}/casos
    @GetMapping("/api/v1/consultorios/{consultorioId}/pacientes/{pacienteId}/casos")
    public ResponseEntity<List<CasoAtencionSummaryResult>> casosPorPaciente(
            @PathVariable UUID consultorioId,
            @PathVariable UUID pacienteId,
            @AuthenticationPrincipal UserDetails principal) {

        List<CasoAtencionSummaryResult> result = service.getCasosPorPaciente(
                pacienteId, consultorioId, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(result);
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }
}
