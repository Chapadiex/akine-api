package com.akine_api.interfaces.api.v1.box;

import com.akine_api.application.dto.command.CreateBoxCommand;
import com.akine_api.application.dto.command.UpdateBoxCapacidadCommand;
import com.akine_api.application.dto.command.UpdateBoxCommand;
import com.akine_api.application.dto.result.BoxResult;
import com.akine_api.application.service.BoxService;
import com.akine_api.interfaces.api.v1.box.dto.BoxCapacidadRequest;
import com.akine_api.interfaces.api.v1.box.dto.BoxRequest;
import com.akine_api.interfaces.api.v1.box.dto.BoxResponse;
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
@RequestMapping("/api/v1/consultorios/{consultorioId}/boxes")
public class BoxController {

    private final BoxService service;

    public BoxController(BoxService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<BoxResponse>> list(
            @PathVariable UUID consultorioId,
            @AuthenticationPrincipal UserDetails principal) {
        List<BoxResponse> result = service
                .list(consultorioId, principal.getUsername(), roles(principal))
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<BoxResponse> create(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody BoxRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        CreateBoxCommand cmd = new CreateBoxCommand(consultorioId, req.nombre(), req.codigo(), req.tipo());
        BoxResponse response = toResponse(service.create(cmd, principal.getUsername(), roles(principal)));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoxResponse> getById(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(toResponse(
                service.getById(consultorioId, id, principal.getUsername(), roles(principal))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoxResponse> update(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody BoxRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        UpdateBoxCommand cmd = new UpdateBoxCommand(id, consultorioId, req.nombre(), req.codigo(), req.tipo());
        return ResponseEntity.ok(toResponse(
                service.update(cmd, principal.getUsername(), roles(principal))));
    }

    @PatchMapping("/{id}/capacidad")
    public ResponseEntity<BoxResponse> updateCapacidad(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @Valid @RequestBody BoxCapacidadRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        UpdateBoxCapacidadCommand cmd = new UpdateBoxCapacidadCommand(
                id, consultorioId, req.capacityType(), req.capacity());
        return ResponseEntity.ok(toResponse(
                service.updateCapacidad(cmd, principal.getUsername(), roles(principal))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inactivate(
            @PathVariable UUID consultorioId,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        service.inactivate(consultorioId, id, principal.getUsername(), roles(principal));
        return ResponseEntity.noContent().build();
    }

    private BoxResponse toResponse(BoxResult r) {
        return new BoxResponse(r.id(), r.consultorioId(), r.nombre(), r.codigo(),
                r.tipo(), r.capacityType(), r.capacity(), r.activo(), r.createdAt(), r.updatedAt());
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }
}
