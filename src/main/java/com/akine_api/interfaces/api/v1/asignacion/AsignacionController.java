package com.akine_api.interfaces.api.v1.asignacion;

import com.akine_api.application.dto.command.AsignarProfesionalCommand;
import com.akine_api.application.dto.command.DesasignarProfesionalCommand;
import com.akine_api.application.dto.result.ProfesionalConsultorioResult;
import com.akine_api.application.service.ProfesionalConsultorioService;
import com.akine_api.interfaces.api.v1.asignacion.dto.AsignacionRequest;
import com.akine_api.interfaces.api.v1.asignacion.dto.AsignacionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/asignaciones")
public class AsignacionController {

    private final ProfesionalConsultorioService service;

    public AsignacionController(ProfesionalConsultorioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AsignacionResponse>> list(
            @PathVariable UUID consultorioId,
            @AuthenticationPrincipal UserDetails principal) {
        List<AsignacionResponse> result = service.list(consultorioId, principal.getUsername(), roles(principal))
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<AsignacionResponse> asignar(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody AsignacionRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        ProfesionalConsultorioResult result = service.asignar(
                new AsignarProfesionalCommand(req.profesionalId(), consultorioId),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @DeleteMapping("/{profesionalId}")
    public ResponseEntity<Void> desasignar(
            @PathVariable UUID consultorioId,
            @PathVariable UUID profesionalId,
            @AuthenticationPrincipal UserDetails principal) {
        service.desasignar(new DesasignarProfesionalCommand(profesionalId, consultorioId), principal.getUsername(), roles(principal));
        return ResponseEntity.noContent().build();
    }

    private AsignacionResponse toResponse(ProfesionalConsultorioResult r) {
        return new AsignacionResponse(
                r.id(), r.profesionalId(), r.consultorioId(), r.profesionalNombre(), r.profesionalApellido(), r.activo());
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
    }
}
