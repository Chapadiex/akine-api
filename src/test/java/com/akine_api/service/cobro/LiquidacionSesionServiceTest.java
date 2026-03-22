package com.akine_api.service.cobro;

import com.akine_api.application.port.output.SesionClinicaRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.cobro.AuditoriaEventoService;
import com.akine_api.application.service.cobro.LiquidacionCalculadorService;
import com.akine_api.application.service.cobro.LiquidacionSesionService;
import com.akine_api.domain.exception.LiquidacionConflictException;
import com.akine_api.domain.exception.LiquidacionNotFoundException;
import com.akine_api.domain.model.HistoriaClinicaOrigenRegistro;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;
import com.akine_api.domain.model.SesionClinica;
import com.akine_api.domain.model.User;
import com.akine_api.domain.model.UserStatus;
import com.akine_api.domain.model.cobro.EstadoLiquidacion;
import com.akine_api.domain.model.cobro.LiquidacionSesion;
import com.akine_api.domain.model.cobro.OrigenTipoCobro;
import com.akine_api.domain.model.cobro.TipoLiquidacion;
import com.akine_api.domain.repository.cobro.LiquidacionSesionRepositoryPort;
import com.akine_api.domain.repository.sesion.SesionAdministrativaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LiquidacionSesionServiceTest {

    @Mock LiquidacionSesionRepositoryPort liquidacionRepo;
    @Mock SesionClinicaRepositoryPort sesionRepo;
    @Mock SesionAdministrativaRepositoryPort sesionAdmRepo;
    @Mock LiquidacionCalculadorService calculador;
    @Mock AuditoriaEventoService auditoriaService;
    @Mock UserRepositoryPort userRepo;

    private LiquidacionSesionService service;

    private static final UUID CONSULTORIO_ID  = UUID.randomUUID();
    private static final UUID PACIENTE_ID     = UUID.randomUUID();
    private static final UUID SESION_ID       = UUID.randomUUID();
    private static final UUID LIQUIDACION_ID  = UUID.randomUUID();
    private static final UUID USER_ID         = UUID.randomUUID();
    private static final String USER_EMAIL    = "admin@test.com";

    @BeforeEach
    void setUp() {
        service = new LiquidacionSesionService(
                liquidacionRepo, sesionRepo, sesionAdmRepo,
                calculador, auditoriaService, userRepo);
        when(userRepo.findByEmail(USER_EMAIL)).thenReturn(Optional.of(buildUser()));
    }

    // ─── liquidarAutomaticamente ──────────────────────────────────────────────

    @Test
    void liquidarAutomaticamente_creaLiquidacion() {
        when(liquidacionRepo.findActivaBySesionId(SESION_ID)).thenReturn(Optional.empty());
        when(sesionRepo.findById(SESION_ID)).thenReturn(Optional.of(buildSesion()));
        when(sesionAdmRepo.findBySesionId(SESION_ID)).thenReturn(Optional.empty());

        LiquidacionSesion calculada = buildLiquidacion(EstadoLiquidacion.LIQUIDADA_PARTICULAR);
        when(calculador.calcular(any(), any(), any())).thenReturn(calculada);
        when(liquidacionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LiquidacionSesion result = service.liquidarAutomaticamente(SESION_ID, USER_EMAIL);

        assertThat(result.getEstado()).isEqualTo(EstadoLiquidacion.LIQUIDADA_PARTICULAR);
        verify(liquidacionRepo).save(any());
        verify(auditoriaService).registrar(any(), eq("LiquidacionSesion"), any(),
                eq("LIQUIDACION_AUTOMATICA"), any(), any(), any(), any());
    }

    @Test
    void liquidarAutomaticamente_idempotente_siYaExiste() {
        LiquidacionSesion existing = buildLiquidacion(EstadoLiquidacion.LIQUIDADA_MIXTA);
        when(liquidacionRepo.findActivaBySesionId(SESION_ID)).thenReturn(Optional.of(existing));

        LiquidacionSesion result = service.liquidarAutomaticamente(SESION_ID, USER_EMAIL);

        assertThat(result).isSameAs(existing);
        verify(liquidacionRepo, never()).save(any());
    }

    // ─── reliquidar ───────────────────────────────────────────────────────────

    @Test
    void reliquidar_sinMotivo_lanzaError() {
        assertThatThrownBy(() -> service.reliquidar(LIQUIDACION_ID, "", USER_EMAIL))
                .isInstanceOf(LiquidacionConflictException.class)
                .hasMessageContaining("motivo");
    }

    @Test
    void reliquidar_estadoOS_lanzaError() {
        LiquidacionSesion liq = buildLiquidacion(EstadoLiquidacion.LIQUIDADA_OS);
        when(liquidacionRepo.findById(LIQUIDACION_ID)).thenReturn(Optional.of(liq));

        assertThatThrownBy(() -> service.reliquidar(LIQUIDACION_ID, "motivo", USER_EMAIL))
                .isInstanceOf(LiquidacionConflictException.class)
                .hasMessageContaining("Obra Social");
    }

    @Test
    void reliquidar_estadoParticular_recalcula() {
        LiquidacionSesion existing = buildLiquidacion(EstadoLiquidacion.LIQUIDADA_PARTICULAR);
        when(liquidacionRepo.findById(LIQUIDACION_ID)).thenReturn(Optional.of(existing));
        when(sesionRepo.findById(SESION_ID)).thenReturn(Optional.of(buildSesion()));
        when(sesionAdmRepo.findBySesionId(SESION_ID)).thenReturn(Optional.empty());

        LiquidacionSesion recalc = buildLiquidacion(EstadoLiquidacion.LIQUIDADA_PARTICULAR);
        when(calculador.calcular(any(), any(), any())).thenReturn(recalc);
        when(liquidacionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LiquidacionSesion result = service.reliquidar(LIQUIDACION_ID, "corrección de arancel", USER_EMAIL);

        assertThat(result.getOrigenTipoCobro()).isEqualTo(OrigenTipoCobro.MANUAL_ADMINISTRATIVO);
        verify(auditoriaService).registrar(any(), eq("LiquidacionSesion"), any(),
                eq("RELIQUIDACION"), any(), any(), any(), eq("corrección de arancel"));
    }

    // ─── convertirAParticular ─────────────────────────────────────────────────

    @Test
    void convertirAParticular_sinMotivo_lanzaError() {
        assertThatThrownBy(() -> service.convertirAParticular(LIQUIDACION_ID, "  ", USER_EMAIL))
                .isInstanceOf(LiquidacionConflictException.class);
    }

    @Test
    void convertirAParticular_yaParticular_lanzaError() {
        LiquidacionSesion liq = buildLiquidacion(EstadoLiquidacion.LIQUIDADA_PARTICULAR);
        liq.setTipoLiquidacion(TipoLiquidacion.PARTICULAR);
        when(liquidacionRepo.findById(LIQUIDACION_ID)).thenReturn(Optional.of(liq));

        assertThatThrownBy(() -> service.convertirAParticular(LIQUIDACION_ID, "motivo", USER_EMAIL))
                .isInstanceOf(LiquidacionConflictException.class)
                .hasMessageContaining("ya es de tipo particular");
    }

    @Test
    void convertirAParticular_mixta_convierteCorrectamente() {
        LiquidacionSesion liq = buildLiquidacion(EstadoLiquidacion.LIQUIDADA_MIXTA);
        liq.setTipoLiquidacion(TipoLiquidacion.MIXTA);
        liq.setValorBruto(new BigDecimal("100.00"));
        liq.setImportePaciente(new BigDecimal("30.00"));
        liq.setImporteObraSocial(new BigDecimal("70.00"));
        when(liquidacionRepo.findById(LIQUIDACION_ID)).thenReturn(Optional.of(liq));
        when(liquidacionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LiquidacionSesion result = service.convertirAParticular(LIQUIDACION_ID, "sin cobertura", USER_EMAIL);

        assertThat(result.getTipoLiquidacion()).isEqualTo(TipoLiquidacion.PARTICULAR);
        assertThat(result.getEstado()).isEqualTo(EstadoLiquidacion.LIQUIDADA_PARTICULAR);
        assertThat(result.getImportePaciente()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(result.getImporteObraSocial()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getOrigenTipoCobro()).isEqualTo(OrigenTipoCobro.CONVERSION_PARTICULAR);

        verify(auditoriaService).registrar(any(), eq("LiquidacionSesion"), any(),
                eq("CONVERSION_PARTICULAR"), any(), any(), any(), eq("sin cobertura"));
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    void findById_noExiste_lanzaNotFound() {
        when(liquidacionRepo.findById(LIQUIDACION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(LIQUIDACION_ID))
                .isInstanceOf(LiquidacionNotFoundException.class);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private LiquidacionSesion buildLiquidacion(EstadoLiquidacion estado) {
        return LiquidacionSesion.builder()
                .id(LIQUIDACION_ID)
                .consultorioId(CONSULTORIO_ID)
                .sesionId(SESION_ID)
                .pacienteId(PACIENTE_ID)
                .tipoLiquidacion(TipoLiquidacion.PARTICULAR)
                .estado(estado)
                .valorBruto(BigDecimal.ZERO)
                .descuentoImporte(BigDecimal.ZERO)
                .copagoImporte(BigDecimal.ZERO)
                .coseguroImporte(BigDecimal.ZERO)
                .importePaciente(BigDecimal.ZERO)
                .importeObraSocial(BigDecimal.ZERO)
                .importeTotalLiquidado(BigDecimal.ZERO)
                .origenTipoCobro(OrigenTipoCobro.AUTOMATICO)
                .liquidadoPor(USER_ID)
                .version(0L)
                .build();
    }

    private SesionClinica buildSesion() {
        return new SesionClinica(
                SESION_ID, CONSULTORIO_ID, PACIENTE_ID,
                null, null, null, null,
                LocalDateTime.now(),
                HistoriaClinicaSesionEstado.BORRADOR,
                HistoriaClinicaTipoAtencion.SEGUIMIENTO,
                null, null, null, null, null, null,
                HistoriaClinicaOrigenRegistro.MANUAL,
                USER_ID, USER_ID, null,
                Instant.now(), Instant.now(), null);
    }

    private User buildUser() {
        return new User(USER_ID, USER_EMAIL, "hash",
                "Test", "Admin", null, UserStatus.ACTIVE, java.time.Instant.now());
    }
}
