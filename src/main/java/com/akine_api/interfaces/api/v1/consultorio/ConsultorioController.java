package com.akine_api.interfaces.api.v1.consultorio;

import com.akine_api.application.dto.command.CreateConsultorioCommand;
import com.akine_api.application.dto.command.UpdateConsultorioCommand;
import com.akine_api.application.dto.result.ConsultorioResult;
import com.akine_api.application.service.ConsultorioService;
import com.akine_api.interfaces.api.v1.consultorio.dto.ConsultorioRequest;
import com.akine_api.interfaces.api.v1.consultorio.dto.ConsultorioResponse;
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
@RequestMapping("/api/v1/consultorios")
public class ConsultorioController {

    private final ConsultorioService service;

    public ConsultorioController(ConsultorioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ConsultorioResponse>> list(
            @AuthenticationPrincipal UserDetails principal) {
        List<ConsultorioResponse> result = service
                .list(principal.getUsername(), roles(principal))
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ConsultorioResponse> create(
            @Valid @RequestBody ConsultorioRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        CreateConsultorioCommand cmd = new CreateConsultorioCommand(
                req.name(), req.cuit(), req.address(), req.phone(), req.email());
        ConsultorioResponse response = toResponse(service.create(cmd, roles(principal)));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultorioResponse> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(toResponse(
                service.getById(id, principal.getUsername(), roles(principal))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultorioResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ConsultorioRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        UpdateConsultorioCommand cmd = new UpdateConsultorioCommand(
                id, req.name(), req.cuit(), req.address(), req.phone(), req.email());
        return ResponseEntity.ok(toResponse(
                service.update(cmd, principal.getUsername(), roles(principal))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inactivate(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        service.inactivate(id, principal.getUsername(), roles(principal));
        return ResponseEntity.noContent().build();
    }

    private ConsultorioResponse toResponse(ConsultorioResult r) {
        return new ConsultorioResponse(r.id(), r.name(), r.cuit(), r.address(),
                r.phone(), r.email(), r.status(), r.createdAt(), r.updatedAt());
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }
}
