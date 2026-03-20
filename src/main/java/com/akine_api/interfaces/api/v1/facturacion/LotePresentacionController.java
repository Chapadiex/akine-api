package com.akine_api.interfaces.api.v1.facturacion;

import com.akine_api.application.dto.facturacion.LotePresentacionDTO;
import com.akine_api.application.mapper.facturacion.LotePresentacionDTOMapper;
import com.akine_api.application.service.facturacion.LotePresentacionService;
import com.akine_api.domain.model.facturacion.LotePresentacion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/facturacion/lotes")
@RequiredArgsConstructor
public class LotePresentacionController {

    private final LotePresentacionService service;
    private final LotePresentacionDTOMapper mapper;

    @PostMapping("/generar")
    public ResponseEntity<LotePresentacionDTO> generarLote(
            @RequestParam UUID financiadorId,
            @RequestParam UUID convenioId,
            @RequestParam String periodo) {
        
        LotePresentacion domain = service.generarLote(financiadorId, convenioId, periodo);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(domain));
    }
}
