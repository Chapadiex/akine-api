package com.akine_api.service.cobro;

import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.cobro.*;
import com.akine_api.domain.exception.LoteOsConflictException;
import com.akine_api.domain.exception.PagoOsConflictException;
import com.akine_api.domain.model.cobro.*;
import com.akine_api.domain.model.UserStatus;
import com.akine_api.domain.repository.cobro.PagoObraSocialRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PagoObraSocialServiceTest {

    @Mock PagoObraSocialRepositoryPort pagoRepo;
    @Mock LoteFacturacionOsService loteService;
    @Mock CajaDiariaService cajaDiariaService;
    @Mock MovimientoCajaService movimientoCajaService;
    @Mock AuditoriaEventoService auditoriaService;
    @Mock UserRepositoryPort userRepo;

    private PagoObraSocialService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID FINANCIADOR_ID  = UUID.randomUUID();
    private static final UUID LOTE_ID         = UUID.randomUUID();
    private static final UUID PAGO_ID         = UUID.randomUUID();
    private static final UUID CAJA_ID         = UUID.randomUUID();
    private static final UUID USER_ID         = UUID.randomUUID();
    private static final String USER_EMAIL    = "admin@test.com";

    @BeforeEach
    void setUp() {
        service = new PagoObraSocialService(
                pagoRepo, loteService, cajaDiariaService,
                movimientoCajaService, auditoriaService, userRepo);
        when(userRepo.findByEmail(USER_EMAIL)).thenReturn(Optional.of(
                new com.akine_api.domain.model.User(USER_ID, USER_EMAIL, "hash",
                        "Test", "Admin", null, UserStatus.ACTIVE, Instant.now())));
    }

    // ─── registrar ────────────────────────────────────────────────────────────

    @Test
    void registrar_loteNoPertenece_lanzaError() {
        LoteFacturacionOs lote = buildLote(EstadoLoteOs.PRESENTADO, UUID.randomUUID()); // different consultorio
        when(loteService.findById(LOTE_ID)).thenReturn(lote);

        assertThatThrownBy(() -> service.registrar(CONSULTORIO_ID, LOTE_ID,
                new BigDecimal("1500.00"), LocalDate.now(), null, USER_EMAIL))
                .isInstanceOf(LoteOsConflictException.class)
                .hasMessageContaining("consultorio");
    }

    @Test
    void registrar_estadoInvalido_lanzaError() {
        LoteFacturacionOs lote = buildLote(EstadoLoteOs.BORRADOR, CONSULTORIO_ID);
        when(loteService.findById(LOTE_ID)).thenReturn(lote);

        assertThatThrownBy(() -> service.registrar(CONSULTORIO_ID, LOTE_ID,
                new BigDecimal("1500.00"), LocalDate.now(), null, USER_EMAIL))
                .isInstanceOf(LoteOsConflictException.class)
                .hasMessageContaining("CERRADO");
    }

    @Test
    void registrar_loteValido_creaRegistroSinMovimientoCaja() {
        LoteFacturacionOs lote = buildLote(EstadoLoteOs.PRESENTADO, CONSULTORIO_ID);
        when(loteService.findById(LOTE_ID)).thenReturn(lote);
        when(loteService.marcarLiquidado(eq(LOTE_ID), any())).thenReturn(lote);
        when(pagoRepo.save(any())).thenAnswer(inv -> {
            PagoObraSocial p = inv.getArgument(0);
            p.setId(PAGO_ID);
            return p;
        });

        PagoObraSocial result = service.registrar(CONSULTORIO_ID, LOTE_ID,
                new BigDecimal("1400.00"), LocalDate.now(), "pago parcial", USER_EMAIL);

        assertThat(result.getImporteRecibido()).isEqualByComparingTo(new BigDecimal("1400.00"));
        assertThat(result.getDiferencia()).isEqualByComparingTo(new BigDecimal("-100.00")); // 1400 - 1500
        assertThat(result.getCajaDiariaId()).isNull(); // NOT imputado yet

        // CRITICAL: no caja movement on registro
        verify(movimientoCajaService, never()).generarDesde(any(), any(), any(), any());
        verify(cajaDiariaService, never()).sumarIngresosOs(any(), any());
    }

    // ─── imputar ─────────────────────────────────────────────────────────────

    @Test
    void imputar_yaImputado_lanzaError() {
        PagoObraSocial pago = buildPago(CAJA_ID); // already imputado
        when(pagoRepo.findById(PAGO_ID)).thenReturn(Optional.of(pago));

        assertThatThrownBy(() -> service.imputar(PAGO_ID, CAJA_ID, USER_EMAIL))
                .isInstanceOf(PagoOsConflictException.class)
                .hasMessageContaining("imputado");
    }

    @Test
    void imputar_noImputado_creaMovimientoCaja() {
        PagoObraSocial pago = buildPago(null); // not yet imputado
        when(pagoRepo.findById(PAGO_ID)).thenReturn(Optional.of(pago));
        when(pagoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(cajaDiariaService).validarCajaAbierta(CAJA_ID);

        PagoObraSocial result = service.imputar(PAGO_ID, CAJA_ID, USER_EMAIL);

        assertThat(result.getCajaDiariaId()).isEqualTo(CAJA_ID);
        assertThat(result.getImputadoPor()).isEqualTo(USER_ID);

        // CRITICAL: caja movement IS created on imputar
        verify(movimientoCajaService).generarDesde(any(), eq(CAJA_ID), any(), eq(USER_ID));
        verify(cajaDiariaService).sumarIngresosOs(eq(CAJA_ID), eq(pago.getImporteRecibido()));
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private LoteFacturacionOs buildLote(EstadoLoteOs estado, UUID consultorioId) {
        return LoteFacturacionOs.builder()
                .id(LOTE_ID)
                .consultorioId(consultorioId)
                .financiadorId(FINANCIADOR_ID)
                .periodo("2026-03")
                .estado(estado)
                .cantidadSesiones(3)
                .importeTotalOs(new BigDecimal("1500.00"))
                .importeNeto(new BigDecimal("1500.00"))
                .creadoPor(USER_ID)
                .version(0L)
                .build();
    }

    private PagoObraSocial buildPago(UUID cajaDiariaId) {
        return PagoObraSocial.builder()
                .id(PAGO_ID)
                .consultorioId(CONSULTORIO_ID)
                .loteId(LOTE_ID)
                .financiadorId(FINANCIADOR_ID)
                .importeEsperado(new BigDecimal("1500.00"))
                .importeRecibido(new BigDecimal("1500.00"))
                .diferencia(BigDecimal.ZERO)
                .fechaNotificacion(LocalDate.now())
                .cajaDiariaId(cajaDiariaId)
                .registradoPor(USER_ID)
                .version(0L)
                .build();
    }
}
