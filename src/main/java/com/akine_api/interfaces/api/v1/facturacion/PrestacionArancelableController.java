package com.akine_api.interfaces.api.v1.facturacion;

import com.akine_api.application.dto.facturacion.PrestacionArancelableDTO;
import com.akine_api.application.mapper.facturacion.PrestacionArancelableDTOMapper;
import com.akine_api.application.service.facturacion.PrestacionArancelableService;
import com.akine_api.domain.model.facturacion.PrestacionArancelable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/facturacion/prestaciones")
@RequiredArgsConstructor
public class PrestacionArancelableController {

    private final PrestacionArancelableService service;
    private final PrestacionArancelableDTOMapper mapper;

    @PostMapping
    public ResponseEntity<PrestacionArancelableDTO> create(@RequestBody PrestacionArancelableDTO dto) {
        PrestacionArancelable domain = mapper.toDomain(dto);
        PrestacionArancelable saved = service.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<PrestacionArancelableDTO>> findAll() {
        List<PrestacionArancelableDTO> dtos = service.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestacionArancelableDTO> findById(@PathVariable UUID id) {
        PrestacionArancelable domain = service.findById(id);
        return ResponseEntity.ok(mapper.toDto(domain));
    }
}
