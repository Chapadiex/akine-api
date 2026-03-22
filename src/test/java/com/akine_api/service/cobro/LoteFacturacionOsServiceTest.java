package com.akine_api.service.cobro;

import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.cobro.AuditoriaEventoService;
import com.akine_api.application.service.cobro.LoteFacturacionOsService;
import com.akine_api.domain.exception.LoteOsConflictException;
import com.akine_api.domain.exception.LoteOsNotFoundException;
import com.akine_api.domain.model.cobro.*;
import com.akine_api.domain.model.UserStatus;
import com.akine_api.domain.repository.cobro.LiquidacionSesionRepositoryPort;
import com.akine_api.domain.repository.cobro.LoteFacturacionOsRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoteFacturacionOsServiceTest {

    @Mock LoteFacturacionOsRepositoryPort loteRepo;
    @Mock LiquidacionSesionRepositoryPort liquidacionRepo;
    @Mock AuditoriaEventoService auditoriaService;
    @Mock UserRepositoryPort userRepo;

    private LoteFacturacionOsService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID FINANCIADOR_ID  = UUID.randomUUID();
    private static final UUID LOTE_ID         = UUID.randomUUID();
    private static final UUID USER_ID         = UUID.randomUUID();
    private static final String USER_EMAIL    = "admin@test.com";

    @BeforeEach
    void setUp() {
        service = new LoteFacturacionOsService(loteRepo, liquidacionRepo, auditoriaService, userRepo);
        when(userRepo.findByEmail(USER_EMAIL)).thenReturn(Optional.of(
                new com.akine_api.domain.model.User(USER_ID, USER_EMAIL, "hash",
                        "Test", "Admin", null, UserStatus.ACTIVE, Instant.now())));
    }

    // ─── generarLote ─────────────────────────────────────────────────────────

    @Test
    void generarLote_sinCandidatas_lanzaError() {
        when(liquidacionRepo.findFacturablesByConsultorioAndFinanciador(CONSULTORIO_ID, FINANCIADOR_ID))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.generarLote(CONSULTORIO_ID, FINANCIADOR_ID, null, "2026-03", USER_EMAIL))
                .isInstanceOf(LoteOsConflictException.class)
                .hasMessageContaining("pendientes");
    }

    @Test
    void generarLote_todasYaEnLote_lanzaError() {
        LiquidacionSesion liq = buildLiquidacion();
        when(liquidacionRepo.findFacturablesByConsultorioAndFinanciador(CONSULTORIO_ID, FINANCIADOR_ID))
                .thenReturn(List.of(liq));
        when(loteRepo.findLiquidacionIdsEnLotesActivos(CONSULTORIO_ID))
                .thenReturn(List.of(liq.getId()));

        assertThatThrownBy(() -> service.generarLote(CONSULTORIO_ID, FINANCIADOR_ID, null, "2026-03", USER_EMAIL))
                .isInstanceOf(LoteOsConflictException.class)
                .hasMessageContaining("ya están incluidas");
    }

    @Test
    void generarLote_conCandidatas_creaLote() {
        LiquidacionSesion liq = buildLiquidacion();
        when(liquidacionRepo.findFacturablesByConsultorioAndFinanciador(CONSULTORIO_ID, FINANCIADOR_ID))
                .thenReturn(List.of(liq));
        when(loteRepo.findLiquidacionIdsEnLotesActivos(CONSULTORIO_ID)).thenReturn(List.of());
        when(loteRepo.save(any())).thenAnswer(inv -> {
            LoteFacturacionOs l = inv.getArgument(0);
            l.setId(LOTE_ID);
            return l;
        });

        LoteFacturacionOs result = service.generarLote(CONSULTORIO_ID, FINANCIADOR_ID, null, "2026-03", USER_EMAIL);

        assertThat(result.getEstado()).isEqualTo(EstadoLoteOs.BORRADOR);
        assertThat(result.getCantidadSesiones()).isEqualTo(1);
        assertThat(result.getImporteTotalOs()).isEqualByComparingTo(new BigDecimal("500.00"));
        verify(loteRepo).save(any());
    }

    // ─── cerrarLote ───────────────────────────────────────────────────────────

    @Test
    void cerrarLote_noBorrador_lanzaError() {
        LoteFacturacionOs lote = buildLote(EstadoLoteOs.CERRADO);
        when(loteRepo.findById(LOTE_ID)).thenReturn(Optional.of(lote));

        assertThatThrownBy(() -> service.cerrarLote(LOTE_ID, USER_EMAIL))
                .isInstanceOf(LoteOsConflictException.class)
                .hasMessageContaining("BORRADOR");
    }

    @Test
    void cerrarLote_borrador_transitaACerrado() {
        LoteFacturacionOs lote = buildLote(EstadoLoteOs.BORRADOR);
        when(loteRepo.findById(LOTE_ID)).thenReturn(Optional.of(lote));
        when(loteRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoteFacturacionOs result = service.cerrarLote(LOTE_ID, USER_EMAIL);

        assertThat(result.getEstado()).isEqualTo(EstadoLoteOs.CERRADO);
        assertThat(result.getCerradoPor()).isEqualTo(USER_ID);
    }

    // ─── marcarPresentado ─────────────────────────────────────────────────────

    @Test
    void marcarPresentado_noCerrado_lanzaError() {
        LoteFacturacionOs lote = buildLote(EstadoLoteOs.BORRADOR);
        when(loteRepo.findById(LOTE_ID)).thenReturn(Optional.of(lote));

        assertThatThrownBy(() -> service.marcarPresentado(LOTE_ID, USER_EMAIL))
                .isInstanceOf(LoteOsConflictException.class)
                .hasMessageContaining("CERRADO");
    }

    @Test
    void marcarPresentado_cerrado_transitaAPresentado() {
        LoteFacturacionOs lote = buildLote(EstadoLoteOs.CERRADO);
        when(loteRepo.findById(LOTE_ID)).thenReturn(Optional.of(lote));
        when(loteRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoteFacturacionOs result = service.marcarPresentado(LOTE_ID, USER_EMAIL);

        assertThat(result.getEstado()).isEqualTo(EstadoLoteOs.PRESENTADO);
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    void findById_noExiste_lanzaNotFound() {
        when(loteRepo.findById(LOTE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(LOTE_ID))
                .isInstanceOf(LoteOsNotFoundException.class);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private LiquidacionSesion buildLiquidacion() {
        return LiquidacionSesion.builder()
                .id(UUID.randomUUID())
                .consultorioId(CONSULTORIO_ID)
                .sesionId(UUID.randomUUID())
                .pacienteId(UUID.randomUUID())
                .financiadorId(FINANCIADOR_ID)
                .tipoLiquidacion(TipoLiquidacion.OS)
                .estado(EstadoLiquidacion.LIQUIDADA_OS)
                .esFacturableOs(true)
                .importeObraSocial(new BigDecimal("500.00"))
                .importeTotalLiquidado(new BigDecimal("500.00"))
                .valorBruto(new BigDecimal("500.00"))
                .descuentoImporte(BigDecimal.ZERO)
                .copagoImporte(BigDecimal.ZERO)
                .coseguroImporte(BigDecimal.ZERO)
                .importePaciente(BigDecimal.ZERO)
                .origenTipoCobro(OrigenTipoCobro.AUTOMATICO)
                .liquidadoPor(USER_ID)
                .version(0L)
                .build();
    }

    private LoteFacturacionOs buildLote(EstadoLoteOs estado) {
        return LoteFacturacionOs.builder()
                .id(LOTE_ID)
                .consultorioId(CONSULTORIO_ID)
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
}
