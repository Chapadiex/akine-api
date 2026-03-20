package com.akine_api.interfaces.api.v1.facturacion;

import com.akine_api.application.dto.facturacion.ConvenioPrestacionValorDTO;
import com.akine_api.application.mapper.facturacion.ConvenioPrestacionValorDTOMapper;
import com.akine_api.application.service.facturacion.ConvenioPrestacionValorService;
import com.akine_api.domain.model.facturacion.ConvenioPrestacionValor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/facturacion/aranceles")
@RequiredArgsConstructor
public class ConvenioPrestacionValorController {

    private final ConvenioPrestacionValorService service;
    private final ConvenioPrestacionValorDTOMapper mapper;

    @PostMapping
    public ResponseEntity<ConvenioPrestacionValorDTO> create(@RequestBody ConvenioPrestacionValorDTO dto) {
        ConvenioPrestacionValor domain = mapper.toDomain(dto);
        ConvenioPrestacionValor saved = service.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(saved));
    }

    @GetMapping("/convenio/{convenioId}")
    public ResponseEntity<List<ConvenioPrestacionValorDTO>> findByConvenioId(@PathVariable UUID convenioId) {
        List<ConvenioPrestacionValorDTO> dtos = service.findByConvenioId(convenioId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
