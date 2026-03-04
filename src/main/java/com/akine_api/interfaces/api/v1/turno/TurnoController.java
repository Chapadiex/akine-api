package com.akine_api.interfaces.api.v1.turno;

import com.akine_api.application.dto.command.CambiarEstadoTurnoCommand;
import com.akine_api.application.dto.command.CreateTurnoCommand;
import com.akine_api.application.dto.command.ReprogramarTurnoCommand;
import com.akine_api.application.dto.result.TurnoResult;
import com.akine_api.application.service.TurnoService;
import com.akine_api.domain.model.TipoConsulta;
import com.akine_api.domain.model.TurnoEstado;
import com.akine_api.interfaces.api.v1.turno.dto.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/turnos")
public class TurnoController {

    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TurnoResponse>> list(
            @PathVariable UUID consultorioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) UUID profesionalId,
            @RequestParam(required = false) UUID boxId,
            @RequestParam(required = false) TurnoEstado estado,
            @AuthenticationPrincipal UserDetails principal) {

        List<TurnoResponse> result = service
                .listByRange(consultorioId, from, to, profesionalId, boxId, estado,
                        principal.getUsername(), roles(principal))
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<TurnoResponse> create(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody CreateTurnoRequest req,
            @AuthenticationPrincipal UserDetails principal) {

        TipoConsulta tipoConsulta = null;
        if (req.tipoConsulta() != null) {
            tipoConsulta = TipoConsulta.valueOf(req.tipoConsulta());
        }

        TurnoResult result = service.create(
                new CreateTurnoCommand(
                        consultorioId,
                        req.profesionalId(),
                        req.boxId(),
                        req.pacienteId(),
                        req.fechaHoraInicio(),
                        req.duracionMinutos(),
                        req.motivoConsulta(),
                        req.notas(),
                        tipoConsulta,
                        req.telefonoContacto(),
                        null
                ),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @PatchMapping("/{id}/reprogramar")
    public ResponseEntity<TurnoResponse> reprogramar(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody ReprogramarRequest req,
            @AuthenticationPrincipal UserDetails principal) {

        TurnoResult result = service.reprogramar(
                id,
                new ReprogramarTurnoCommand(id, req.nuevaFechaHoraInicio()),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(result));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<TurnoResponse> cambiarEstado(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody CambiarEstadoRequest req,
            @AuthenticationPrincipal UserDetails principal) {

        TurnoResult result = service.cambiarEstado(
                id,
                new CambiarEstadoTurnoCommand(id, req.nuevoEstado(), req.motivo(), null),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(result));
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<List<SlotDisponibleResponse>> disponibilidad(
            @PathVariable UUID consultorioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) UUID profesionalId,
            @RequestParam int duracion,
            @AuthenticationPrincipal UserDetails principal) {

        List<SlotDisponibleResponse> result = service
                .getDisponibilidad(consultorioId, profesionalId, date, duracion,
                        principal.getUsername(), roles(principal))
                .stream().map(s -> new SlotDisponibleResponse(s.inicio(), s.fin())).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialEstadoTurnoResponse>> historial(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {

        List<HistorialEstadoTurnoResponse> result = service
                .getHistorial(id, principal.getUsername(), roles(principal))
                .stream().map(h -> new HistorialEstadoTurnoResponse(
                        h.id(), h.turnoId(),
                        h.estadoAnterior() != null ? h.estadoAnterior().name() : null,
                        h.estadoNuevo().name(),
                        h.cambiadoPorUserEmail(),
                        h.motivo(),
                        h.createdAt()
                )).toList();
        return ResponseEntity.ok(result);
    }

    private TurnoResponse toResponse(TurnoResult r) {
        return new TurnoResponse(
                r.id(), r.consultorioId(), r.profesionalId(),
                r.profesionalNombre(), r.profesionalApellido(),
                r.boxId(), r.boxNombre(), r.pacienteId(),
                r.pacienteNombre(), r.pacienteApellido(), r.pacienteDni(),
                r.fechaHoraInicio(), r.fechaHoraFin(),
                r.duracionMinutos(), r.estado().name(),
                r.tipoConsulta() != null ? r.tipoConsulta().name() : null,
                r.motivoConsulta(), r.notas(),
                r.telefonoContacto(),
                r.creadoPorUserId(),
                r.motivoCancelacion(),
                r.createdAt(), r.updatedAt()
        );
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }
}
