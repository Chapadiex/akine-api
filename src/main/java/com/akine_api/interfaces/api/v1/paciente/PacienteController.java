package com.akine_api.interfaces.api.v1.paciente;

import com.akine_api.application.dto.command.CreateMyPacienteCommand;
import com.akine_api.application.dto.command.CreatePacienteAdminCommand;
import com.akine_api.application.dto.command.UpdatePacienteAdminCommand;
import com.akine_api.application.dto.result.PacienteResult;
import com.akine_api.application.dto.result.PacienteSearchResult;
import com.akine_api.application.service.PacienteService;
import com.akine_api.interfaces.api.v1.paciente.dto.*;
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
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @PostMapping("/me")
    public ResponseEntity<PacienteResponse> createMe(@Valid @RequestBody PacienteSelfCreateRequest req,
                                                     @AuthenticationPrincipal UserDetails principal) {
        PacienteResult result = service.createMe(
                new CreateMyPacienteCommand(
                        req.dni(), req.nombre(), req.apellido(), req.telefono(), req.email(),
                        req.fechaNacimiento(), req.sexo(), req.domicilio(), req.nacionalidad(),
                        req.estadoCivil(), req.profesion(), req.obraSocialNombre(),
                        req.obraSocialPlan(), req.obraSocialNroAfiliado()
                ),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping("/me")
    public ResponseEntity<PacienteResponse> getMe(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(toResponse(service.getMe(principal.getUsername(), roles(principal))));
    }

    @PostMapping
    public ResponseEntity<PacienteResponse> createAdmin(@RequestParam UUID consultorioId,
                                                        @Valid @RequestBody PacienteAdminCreateRequest req,
                                                        @AuthenticationPrincipal UserDetails principal) {
        PacienteResult result = service.createAdmin(
                consultorioId,
                new CreatePacienteAdminCommand(
                        req.dni(), req.nombre(), req.apellido(), req.telefono(), req.email(),
                        req.fechaNacimiento(), req.sexo(), req.domicilio(), req.nacionalidad(),
                        req.estadoCivil(), req.profesion(), req.obraSocialNombre(),
                        req.obraSocialPlan(), req.obraSocialNroAfiliado()
                ),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping
    public ResponseEntity<List<PacienteSearchResponse>> list(@RequestParam UUID consultorioId,
                                                             @AuthenticationPrincipal UserDetails principal) {
        List<PacienteSearchResponse> result = service.listByConsultorio(
                consultorioId, principal.getUsername(), roles(principal)
        ).stream().map(this::toSearchResponse).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PacienteSearchResponse>> search(@RequestParam UUID consultorioId,
                                                               @RequestParam(required = false) String dni,
                                                               @RequestParam(required = false) String q,
                                                               @AuthenticationPrincipal UserDetails principal) {
        List<PacienteSearchResponse> result = service.search(
                consultorioId, dni, q, principal.getUsername(), roles(principal)
        ).stream().map(this::toSearchResponse).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> getById(@PathVariable UUID id,
                                                    @RequestParam UUID consultorioId,
                                                    @AuthenticationPrincipal UserDetails principal) {
        PacienteResult result = service.getById(id, consultorioId, principal.getUsername(), roles(principal));
        return ResponseEntity.ok(toResponse(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponse> updateAdmin(@PathVariable UUID id,
                                                        @RequestParam UUID consultorioId,
                                                        @Valid @RequestBody PacienteAdminUpdateRequest req,
                                                        @AuthenticationPrincipal UserDetails principal) {
        PacienteResult result = service.updateAdmin(
                id,
                consultorioId,
                new UpdatePacienteAdminCommand(
                        req.nombre(), req.apellido(), req.telefono(), req.email(),
                        req.fechaNacimiento(), req.sexo(), req.domicilio(), req.nacionalidad(),
                        req.estadoCivil(), req.profesion(), req.obraSocialNombre(),
                        req.obraSocialPlan(), req.obraSocialNroAfiliado()
                ),
                principal.getUsername(),
                roles(principal)
        );
        return ResponseEntity.ok(toResponse(result));
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }

    private PacienteResponse toResponse(PacienteResult r) {
        return new PacienteResponse(
                r.id(), r.dni(), r.nombre(), r.apellido(), r.telefono(), r.email(),
                r.fechaNacimiento(), r.sexo(), r.domicilio(), r.nacionalidad(), r.estadoCivil(),
                r.profesion(), r.obraSocialNombre(), r.obraSocialPlan(), r.obraSocialNroAfiliado(),
                r.userId(), r.activo(), r.createdAt(), r.updatedAt()
        );
    }

    private PacienteSearchResponse toSearchResponse(PacienteSearchResult r) {
        return new PacienteSearchResponse(
                r.id(), r.dni(), r.nombre(), r.apellido(), r.telefono(), r.email(),
                r.activo(), r.linkedToConsultorio()
        );
    }
}
