package com.akine_api.interfaces.api.v1.duracion;

import com.akine_api.application.dto.command.AddDuracionTurnoCommand;
import com.akine_api.application.dto.command.RemoveDuracionTurnoCommand;
import com.akine_api.application.dto.result.ConsultorioDuracionTurnoResult;
import com.akine_api.application.service.ConsultorioDuracionTurnoService;
import com.akine_api.interfaces.api.v1.duracion.dto.DuracionRequest;
import com.akine_api.interfaces.api.v1.duracion.dto.DuracionResponse;
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
@RequestMapping("/api/v1/consultorios/{consultorioId}/duraciones")
public class DuracionTurnoController {

    private final ConsultorioDuracionTurnoService service;

    public DuracionTurnoController(ConsultorioDuracionTurnoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DuracionResponse>> list(
            @PathVariable UUID consultorioId,
            @AuthenticationPrincipal UserDetails principal) {
        List<DuracionResponse> result = service.list(consultorioId, principal.getUsername(), roles(principal))
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<DuracionResponse> add(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody DuracionRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        ConsultorioDuracionTurnoResult result = service.add(
                new AddDuracionTurnoCommand(consultorioId, req.minutos()),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @DeleteMapping("/{minutos}")
    public ResponseEntity<Void> remove(
            @PathVariable UUID consultorioId,
            @PathVariable int minutos,
            @AuthenticationPrincipal UserDetails principal) {
        service.remove(new RemoveDuracionTurnoCommand(consultorioId, minutos), principal.getUsername(), roles(principal));
        return ResponseEntity.noContent().build();
    }

    private DuracionResponse toResponse(ConsultorioDuracionTurnoResult r) {
        return new DuracionResponse(r.id(), r.consultorioId(), r.minutos());
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
    }
}
