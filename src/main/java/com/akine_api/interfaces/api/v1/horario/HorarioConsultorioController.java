package com.akine_api.interfaces.api.v1.horario;

import com.akine_api.application.dto.command.DeleteHorarioConsultorioCommand;
import com.akine_api.application.dto.command.SetHorarioConsultorioCommand;
import com.akine_api.application.dto.result.ConsultorioHorarioResult;
import com.akine_api.application.service.ConsultorioHorarioService;
import com.akine_api.interfaces.api.v1.horario.dto.HorarioRequest;
import com.akine_api.interfaces.api.v1.horario.dto.HorarioResponse;
import jakarta.validation.Valid;
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

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultorios/{consultorioId}/horarios")
public class HorarioConsultorioController {

    private final ConsultorioHorarioService service;

    public HorarioConsultorioController(ConsultorioHorarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<HorarioResponse>> list(
            @PathVariable UUID consultorioId,
            @AuthenticationPrincipal UserDetails principal) {
        List<HorarioResponse> result = service.list(consultorioId, principal.getUsername(), roles(principal))
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{dia}")
    public ResponseEntity<HorarioResponse> set(
            @PathVariable UUID consultorioId,
            @PathVariable DayOfWeek dia,
            @Valid @RequestBody HorarioRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        SetHorarioConsultorioCommand cmd = new SetHorarioConsultorioCommand(
                consultorioId, dia, req.horaApertura(), req.horaCierre());
        return ResponseEntity.ok(toResponse(service.set(cmd, principal.getUsername(), roles(principal))));
    }

    @PostMapping
    public ResponseEntity<HorarioResponse> add(
            @PathVariable UUID consultorioId,
            @Valid @RequestBody HorarioRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        SetHorarioConsultorioCommand cmd = new SetHorarioConsultorioCommand(
                consultorioId, req.diaSemana(), req.horaApertura(), req.horaCierre());
        return ResponseEntity.ok(toResponse(service.add(cmd, principal.getUsername(), roles(principal))));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<HorarioResponse>> addBatch(
            @PathVariable UUID consultorioId,
            @RequestBody List<@Valid HorarioRequest> requests,
            @AuthenticationPrincipal UserDetails principal) {
        List<SetHorarioConsultorioCommand> commands = requests.stream()
                .map(req -> new SetHorarioConsultorioCommand(
                        consultorioId, req.diaSemana(), req.horaApertura(), req.horaCierre()))
                .toList();

        List<HorarioResponse> result = service.addBatch(consultorioId, commands, principal.getUsername(), roles(principal))
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{dia}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID consultorioId,
            @PathVariable DayOfWeek dia,
            @AuthenticationPrincipal UserDetails principal) {
        service.delete(new DeleteHorarioConsultorioCommand(consultorioId, dia), principal.getUsername(), roles(principal));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tramos/{horarioId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable UUID consultorioId,
            @PathVariable UUID horarioId,
            @AuthenticationPrincipal UserDetails principal) {
        service.deleteById(consultorioId, horarioId, principal.getUsername(), roles(principal));
        return ResponseEntity.noContent().build();
    }

    private HorarioResponse toResponse(ConsultorioHorarioResult r) {
        return new HorarioResponse(r.id(), r.consultorioId(), r.diaSemana(), r.horaApertura(), r.horaCierre(), r.activo());
    }

    private Set<String> roles(UserDetails principal) {
        return principal.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
    }
}
