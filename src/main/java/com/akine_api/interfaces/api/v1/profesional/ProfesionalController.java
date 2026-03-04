package com.akine_api.interfaces.api.v1.profesional;

import com.akine_api.application.dto.command.CreateProfesionalCommand;
import com.akine_api.application.dto.command.UpdateProfesionalCommand;
import com.akine_api.application.dto.result.ProfesionalResult;
import com.akine_api.application.service.ProfesionalService;
import com.akine_api.interfaces.api.v1.profesional.dto.ProfesionalEstadoRequest;
import com.akine_api.interfaces.api.v1.profesional.dto.ProfesionalRequest;
import com.akine_api.interfaces.api.v1.profesional.dto.ProfesionalResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/profesionales")
public class ProfesionalController {

    private final ProfesionalService service;

    public ProfesionalController(ProfesionalService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProfesionalResponse>> list(
            @PathVariable UUID consultorioId,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) List<String> especialidades,
            @RequestParam(required = false) Boolean activo,
            @AuthenticationPrincipal UserDetails principal) {
        List<ProfesionalResponse> result = service
                .list(consultorioId, principal.getUsername(), roles(principal), dni, q, matricula, especialidades, activo)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ProfesionalResponse> create(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody ProfesionalRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        CreateProfesionalCommand cmd = new CreateProfesionalCommand(
                consultorioId,
                req.nombre(),
                req.apellido(),
                req.nroDocumento(),
                req.matricula(),
                req.especialidad(),
                req.especialidades(),
                req.email(),
                req.telefono(),
                req.domicilio(),
                req.fotoPerfilUrl()
        );
        ProfesionalResponse response = toResponse(
                service.create(cmd, principal.getUsername(), roles(principal)));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfesionalResponse> getById(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(toResponse(
                service.getById(consultorioId, id, principal.getUsername(), roles(principal))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfesionalResponse> update(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody ProfesionalRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        UpdateProfesionalCommand cmd = new UpdateProfesionalCommand(
                id,
                consultorioId,
                req.nombre(),
                req.apellido(),
                req.nroDocumento(),
                req.matricula(),
                req.especialidad(),
                req.especialidades(),
                req.email(),
                req.telefono(),
                req.domicilio(),
                req.fotoPerfilUrl()
        );
        return ResponseEntity.ok(toResponse(
                service.update(cmd, principal.getUsername(), roles(principal))));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ProfesionalResponse> changeEstado(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody ProfesionalEstadoRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        ProfesionalResult updated = service.changeEstado(
                consultorioId,
                id,
                req.activo(),
                req.fechaDeBaja(),
                req.motivoDeBaja(),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inactivate(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        service.inactivate(consultorioId, id, principal.getUsername(), roles(principal));
        return ResponseEntity.noContent().build();
    }

    private ProfesionalResponse toResponse(ProfesionalResult r) {
        List<String> especialidades = r.especialidades() == null || r.especialidades().isBlank()
                ? List.of()
                : java.util.Arrays.stream(r.especialidades().split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        return new ProfesionalResponse(
                r.id(),
                r.consultorioId(),
                r.nombre(),
                r.apellido(),
                r.nroDocumento(),
                r.matricula(),
                r.especialidad(),
                especialidades,
                r.email(),
                r.telefono(),
                r.domicilio(),
                r.fotoPerfilUrl(),
                r.fechaAlta(),
                r.fechaBaja(),
                r.motivoBaja(),
                r.consultoriosAsociados(),
                r.activo(),
                r.createdAt(),
                r.updatedAt()
        );
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }
}
