package com.akine_api.interfaces.api.v1.facturacion;

import com.akine_api.application.dto.facturacion.LiquidacionFinanciadorDTO;
import com.akine_api.application.dto.facturacion.PagoFinanciadorDTO;
import com.akine_api.application.mapper.facturacion.LiquidacionFinanciadorDTOMapper;
import com.akine_api.application.mapper.facturacion.PagoFinanciadorDTOMapper;
import com.akine_api.application.service.facturacion.CobranzaFinanciadorService;
import com.akine_api.domain.model.facturacion.LiquidacionFinanciador;
import com.akine_api.domain.model.facturacion.PagoFinanciador;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/facturacion/cobranzas")
@RequiredArgsConstructor
public class CobranzaFinanciadorController {

    private final CobranzaFinanciadorService service;
    private final LiquidacionFinanciadorDTOMapper liqMapper;
    private final PagoFinanciadorDTOMapper pagoMapper;

    @PostMapping("/liquidaciones")
    public ResponseEntity<LiquidacionFinanciadorDTO> registrarLiquidacion(@RequestBody LiquidacionFinanciadorDTO dto) {
        LiquidacionFinanciador domain = liqMapper.toDomain(dto);
        LiquidacionFinanciador saved = service.registrarLiquidacion(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(liqMapper.toDto(saved));
    }

    @PostMapping("/pagos")
    public ResponseEntity<PagoFinanciadorDTO> registrarPago(@RequestBody PagoFinanciadorDTO dto) {
        PagoFinanciador domain = pagoMapper.toDomain(dto);
        PagoFinanciador saved = service.registrarPago(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoMapper.toDto(saved));
    }
}
