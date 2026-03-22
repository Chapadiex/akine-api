package com.akine_api.service.cobro;

import com.akine_api.application.service.cobro.LiquidacionCalculadorService;
import com.akine_api.domain.model.HistoriaClinicaOrigenRegistro;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;
import com.akine_api.domain.model.SesionClinica;
import com.akine_api.domain.model.cobro.EstadoLiquidacion;
import com.akine_api.domain.model.cobro.LiquidacionSesion;
import com.akine_api.domain.model.cobro.TipoLiquidacion;
import com.akine_api.domain.model.facturacion.ConvenioFinanciador;
import com.akine_api.domain.model.facturacion.ConvenioPrestacionValor;
import com.akine_api.domain.model.sesion.CoberturaTipo;
import com.akine_api.domain.model.sesion.SesionAdministrativa;
import com.akine_api.domain.model.sesion.ValidacionCoberturaEstado;
import com.akine_api.domain.repository.facturacion.ConvenioFinanciadorRepositoryPort;
import com.akine_api.domain.repository.facturacion.ConvenioPrestacionValorRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LiquidacionCalculadorServiceTest {

    @Mock ConvenioFinanciadorRepositoryPort convenioRepo;
    @Mock ConvenioPrestacionValorRepositoryPort convenioPrestacionRepo;

    private LiquidacionCalculadorService service;

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final UUID PACIENTE_ID    = UUID.randomUUID();
    private static final UUID ACTOR_ID       = UUID.randomUUID();
    private static final UUID FINANCIADOR_ID = UUID.randomUUID();
    private static final UUID PLAN_ID        = UUID.randomUUID();
    private static final UUID CONVENIO_ID    = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new LiquidacionCalculadorService(convenioRepo, convenioPrestacionRepo);
    }

    // ─── Circuito 1: Particular puro (sin convenio) ──────────────────────────

    @Test
    void sinConvenio_liquidaComoParticular() {
        when(convenioRepo.findVigenteByFinanciadorPlanConsultorio(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        SesionClinica sesion = buildSesion();
        SesionAdministrativa adm = buildAdm(true, true);

        LiquidacionSesion result = service.calcular(sesion, adm, ACTOR_ID);

        assertThat(result.getTipoLiquidacion()).isEqualTo(TipoLiquidacion.PARTICULAR);
        assertThat(result.getEstado()).isEqualTo(EstadoLiquidacion.LIQUIDADA_PARTICULAR);
        assertThat(result.getImporteObraSocial()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.isEsFacturableOs()).isFalse();
        assertThat(result.getLiquidadoPor()).isEqualTo(ACTOR_ID);
    }

    // ─── Circuito 2: OS sin copago → tipo OS ─────────────────────────────────

    @Test
    void conConvenioSinCopago_liquidaComoOS() {
        ConvenioFinanciador convenio = buildConvenio();
        ConvenioPrestacionValor prestacion = ConvenioPrestacionValor.builder()
                .id(UUID.randomUUID())
                .convenioId(CONVENIO_ID)
                .importeBase(new BigDecimal("100.00"))
                .importeCopago(null)
                .copajoPorcentaje(null)
                .coseguroImporte(null)
                .activo(true)
                .build();

        when(convenioRepo.findVigenteByFinanciadorPlanConsultorio(any(), any(), any(), any()))
                .thenReturn(Optional.of(convenio));
        when(convenioPrestacionRepo.findByConvenioId(CONVENIO_ID)).thenReturn(List.of(prestacion));

        SesionClinica sesion = buildSesion();
        SesionAdministrativa adm = buildAdm(true, true);

        LiquidacionSesion result = service.calcular(sesion, adm, ACTOR_ID);

        assertThat(result.getTipoLiquidacion()).isEqualTo(TipoLiquidacion.OS);
        assertThat(result.getEstado()).isEqualTo(EstadoLiquidacion.LIQUIDADA_OS);
        assertThat(result.getImportePaciente()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getImporteObraSocial()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(result.getImporteTotalLiquidado()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    // ─── Circuito 3: Mixta con copago fijo ───────────────────────────────────

    @Test
    void conCopagFijo_liquidaComoMixta() {
        ConvenioFinanciador convenio = buildConvenio();
        ConvenioPrestacionValor prestacion = ConvenioPrestacionValor.builder()
                .id(UUID.randomUUID())
                .convenioId(CONVENIO_ID)
                .importeBase(new BigDecimal("200.00"))
                .importeCopago(new BigDecimal("50.00"))
                .copajoPorcentaje(null)
                .coseguroImporte(null)
                .activo(true)
                .build();

        when(convenioRepo.findVigenteByFinanciadorPlanConsultorio(any(), any(), any(), any()))
                .thenReturn(Optional.of(convenio));
        when(convenioPrestacionRepo.findByConvenioId(CONVENIO_ID)).thenReturn(List.of(prestacion));

        SesionClinica sesion = buildSesion();
        SesionAdministrativa adm = buildAdm(true, true);

        LiquidacionSesion result = service.calcular(sesion, adm, ACTOR_ID);

        assertThat(result.getTipoLiquidacion()).isEqualTo(TipoLiquidacion.MIXTA);
        assertThat(result.getEstado()).isEqualTo(EstadoLiquidacion.LIQUIDADA_MIXTA);
        assertThat(result.getImportePaciente()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(result.getImporteObraSocial()).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    // ─── Copago por porcentaje ────────────────────────────────────────────────

    @Test
    void copagoPorcentaje_calculaCorrectamente() {
        ConvenioFinanciador convenio = buildConvenio();
        ConvenioPrestacionValor prestacion = ConvenioPrestacionValor.builder()
                .id(UUID.randomUUID())
                .convenioId(CONVENIO_ID)
                .importeBase(new BigDecimal("100.00"))
                .importeCopago(null)
                .copajoPorcentaje(new BigDecimal("20"))   // 20%
                .coseguroImporte(null)
                .activo(true)
                .build();

        when(convenioRepo.findVigenteByFinanciadorPlanConsultorio(any(), any(), any(), any()))
                .thenReturn(Optional.of(convenio));
        when(convenioPrestacionRepo.findByConvenioId(CONVENIO_ID)).thenReturn(List.of(prestacion));

        SesionClinica sesion = buildSesion();
        SesionAdministrativa adm = buildAdm(true, true);

        LiquidacionSesion result = service.calcular(sesion, adm, ACTOR_ID);

        assertThat(result.getTipoLiquidacion()).isEqualTo(TipoLiquidacion.MIXTA);
        assertThat(result.getImportePaciente()).isEqualByComparingTo(new BigDecimal("20.0000"));
        assertThat(result.getImporteObraSocial()).isEqualByComparingTo(new BigDecimal("80.0000"));
    }

    // ─── Paso 9: documentacion_completa = false → BLOQUEADA ──────────────────

    @Test
    void documentacionIncompleta_conConvenioOS_quedaraBloqueada() {
        ConvenioFinanciador convenio = buildConvenio();
        ConvenioPrestacionValor prestacion = ConvenioPrestacionValor.builder()
                .id(UUID.randomUUID())
                .convenioId(CONVENIO_ID)
                .importeBase(new BigDecimal("100.00"))
                .importeCopago(null)
                .copajoPorcentaje(null)
                .coseguroImporte(null)
                .activo(true)
                .build();

        when(convenioRepo.findVigenteByFinanciadorPlanConsultorio(any(), any(), any(), any()))
                .thenReturn(Optional.of(convenio));
        when(convenioPrestacionRepo.findByConvenioId(CONVENIO_ID)).thenReturn(List.of(prestacion));

        SesionClinica sesion = buildSesion();
        SesionAdministrativa adm = buildAdm(false, false); // documentacion incompleta

        LiquidacionSesion result = service.calcular(sesion, adm, ACTOR_ID);

        assertThat(result.getEstado()).isEqualTo(EstadoLiquidacion.BLOQUEADA_POR_DOCUMENTACION);
        assertThat(result.getMotivoBloqueo()).isNotBlank();
    }

    @Test
    void documentacionIncompleta_particular_noBloqueada() {
        when(convenioRepo.findVigenteByFinanciadorPlanConsultorio(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        SesionClinica sesion = buildSesion();
        SesionAdministrativa adm = buildAdm(false, false);

        LiquidacionSesion result = service.calcular(sesion, adm, ACTOR_ID);

        // Particular never gets blocked (no OS involved)
        assertThat(result.getTipoLiquidacion()).isEqualTo(TipoLiquidacion.PARTICULAR);
        assertThat(result.getEstado()).isEqualTo(EstadoLiquidacion.LIQUIDADA_PARTICULAR);
    }

    // ─── Sin sesionAdm (null) → particular ───────────────────────────────────

    @Test
    void sinSesionAdministrativa_liquidaComoParticular() {
        LiquidacionSesion result = service.calcular(buildSesion(), null, ACTOR_ID);

        assertThat(result.getTipoLiquidacion()).isEqualTo(TipoLiquidacion.PARTICULAR);
        assertThat(result.getFinanciadorId()).isNull();
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private SesionClinica buildSesion() {
        return new SesionClinica(
                UUID.randomUUID(),
                CONSULTORIO_ID,
                PACIENTE_ID,
                null, null, null, null,
                LocalDateTime.now(),
                HistoriaClinicaSesionEstado.BORRADOR,
                HistoriaClinicaTipoAtencion.SEGUIMIENTO,
                null, null, null, null, null, null,
                HistoriaClinicaOrigenRegistro.MANUAL,
                ACTOR_ID, ACTOR_ID, null,
                Instant.now(), Instant.now(), null);
    }

    private SesionAdministrativa buildAdm(boolean documentacionCompleta, boolean esFacturableOs) {
        return SesionAdministrativa.builder()
                .id(UUID.randomUUID())
                .sesionId(UUID.randomUUID())
                .consultorioId(CONSULTORIO_ID)
                .pacienteId(PACIENTE_ID)
                .financiadorId(FINANCIADOR_ID)
                .planId(PLAN_ID)
                .coberturaTipo(CoberturaTipo.OBRA_SOCIAL)
                .asistenciaConfirmada(true)
                .documentacionCompleta(documentacionCompleta)
                .esFacturableOs(esFacturableOs)
                .validacionCoberturaEstado(ValidacionCoberturaEstado.VALIDADA)
                .build();
    }

    private ConvenioFinanciador buildConvenio() {
        return ConvenioFinanciador.builder()
                .id(CONVENIO_ID)
                .financiadorId(FINANCIADOR_ID)
                .consultorioId(CONSULTORIO_ID)
                .planId(PLAN_ID)
                .nombre("Convenio Test")
                .vigenciaDesde(LocalDate.now().minusYears(1))
                .vigenciaHasta(null)
                .activo(true)
                .build();
    }
}
