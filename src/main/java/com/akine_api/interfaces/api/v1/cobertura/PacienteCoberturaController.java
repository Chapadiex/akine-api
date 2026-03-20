package com.akine_api.interfaces.api.v1.cobertura;

import com.akine_api.application.dto.cobertura.PacienteCoberturaDTO;
import com.akine_api.application.mapper.cobertura.PacienteCoberturaDTOMapper;
import com.akine_api.application.service.cobertura.PacienteCoberturaService;
import com.akine_api.domain.model.cobertura.PacienteCobertura;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cobertura/pacientes")
@RequiredArgsConstructor
public class PacienteCoberturaController {

    private final PacienteCoberturaService service;
    private final PacienteCoberturaDTOMapper mapper;

    @PostMapping
    public ResponseEntity<PacienteCoberturaDTO> create(@RequestBody PacienteCoberturaDTO dto) {
        PacienteCobertura domain = mapper.toDomain(dto);
        PacienteCobertura saved = service.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(saved));
    }

    @GetMapping("/{pacienteId}/coberturas")
    public ResponseEntity<List<PacienteCoberturaDTO>> findByPacienteId(@PathVariable UUID pacienteId) {
        List<PacienteCoberturaDTO> dtos = service.findByPacienteId(pacienteId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
