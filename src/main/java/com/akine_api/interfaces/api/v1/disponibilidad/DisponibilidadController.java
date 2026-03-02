package com.akine_api.interfaces.api.v1.disponibilidad;

import com.akine_api.application.dto.command.CreateDisponibilidadCommand;
import com.akine_api.application.dto.command.DeleteDisponibilidadCommand;
import com.akine_api.application.dto.command.UpdateDisponibilidadCommand;
import com.akine_api.application.dto.result.DisponibilidadProfesionalResult;
import com.akine_api.application.service.DisponibilidadProfesionalService;
import com.akine_api.interfaces.api.v1.disponibilidad.dto.DisponibilidadRequest;
import com.akine_api.interfaces.api.v1.disponibilidad.dto.DisponibilidadResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/profesionales/{profesionalId}/disponibilidad")
public class DisponibilidadController {

    private final DisponibilidadProfesionalService service;

    public DisponibilidadController(DisponibilidadProfesionalService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DisponibilidadResponse>> list(
            @PathVariable UUID consultorioId,
            @PathVariable UUID profesionalId,
            @AuthenticationPrincipal UserDetails principal) {
        List<DisponibilidadResponse> result = service
                .list(profesionalId, consultorioId, principal.getUsername(), roles(principal))
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<DisponibilidadResponse> create(
            @PathVariable UUID consultorioId,
            @PathVariable UUID profesionalId,
            @Valid @RequestBody DisponibilidadRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        DisponibilidadProfesionalResult result = service.create(
                new CreateDisponibilidadCommand(profesionalId, consultorioId, req.diaSemana(), req.horaInicio(), req.horaFin()),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadResponse> update(
            @PathVariable UUID consultorioId,
            @PathVariable UUID profesionalId,
            @PathVariable UUID id,
            @Valid @RequestBody DisponibilidadRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        DisponibilidadProfesionalResult result = service.update(
                new UpdateDisponibilidadCommand(id, profesionalId, consultorioId, req.diaSemana(), req.horaInicio(), req.horaFin()),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID consultorioId,
            @PathVariable UUID profesionalId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        service.delete(new DeleteDisponibilidadCommand(id, profesionalId, consultorioId), principal.getUsername(), roles(principal));
        return ResponseEntity.noContent().build();
    }

    private DisponibilidadResponse toResponse(DisponibilidadProfesionalResult r) {
        return new DisponibilidadResponse(r.id(), r.profesionalId(), r.consultorioId(), r.diaSemana(), r.horaInicio(), r.horaFin(), r.activo());
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
    }
}
