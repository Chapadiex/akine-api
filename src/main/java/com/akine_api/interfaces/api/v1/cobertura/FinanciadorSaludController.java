package com.akine_api.interfaces.api.v1.cobertura;

import com.akine_api.application.dto.cobertura.FinanciadorSaludDTO;
import com.akine_api.application.mapper.cobertura.FinanciadorSaludDTOMapper;
import com.akine_api.application.service.cobertura.FinanciadorSaludService;
import com.akine_api.application.service.cobertura.FinanciadorSeedService;
import com.akine_api.domain.model.cobertura.FinanciadorSalud;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cobertura/financiadores")
@RequiredArgsConstructor
public class FinanciadorSaludController {

    private final FinanciadorSaludService service;
    private final FinanciadorSaludDTOMapper mapper;
    private final FinanciadorSeedService seedService;

    @PostMapping
    public ResponseEntity<FinanciadorSaludDTO> create(@RequestBody FinanciadorSaludDTO dto) {
        FinanciadorSalud domain = mapper.toDomain(dto);
        FinanciadorSalud saved = service.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<FinanciadorSaludDTO>> findAllByConsultorioId(@RequestParam UUID consultorioId) {
        List<FinanciadorSaludDTO> dtos = service.findAllByConsultorioId(consultorioId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/seed")
    public ResponseEntity<Void> seed(@RequestParam UUID consultorioId,
                                     @RequestParam(required = false) BigDecimal precioLista) {
        seedService.seedForConsultorio(consultorioId, precioLista);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinanciadorSaludDTO> update(@PathVariable UUID id, @RequestBody FinanciadorSaludDTO dto) {
        FinanciadorSalud domain = mapper.toDomain(dto);
        FinanciadorSalud updated = service.update(id, domain);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinanciadorSaludDTO> findById(@PathVariable UUID id) {
        FinanciadorSalud domain = service.findById(id);
        return ResponseEntity.ok(mapper.toDto(domain));
    }
}
