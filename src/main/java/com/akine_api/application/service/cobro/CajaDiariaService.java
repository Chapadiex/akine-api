package com.akine_api.application.service.cobro;

import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.exception.CajaConflictException;
import com.akine_api.domain.exception.CajaNotFoundException;
import com.akine_api.domain.model.cobro.CajaDiaria;
import com.akine_api.domain.model.cobro.CajaDiariaEstado;
import com.akine_api.domain.repository.cobro.CajaDiariaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CajaDiariaService {

    private final CajaDiariaRepositoryPort repositoryPort;
    private final AuditoriaEventoService auditoriaService;
    private final UserRepositoryPort userRepo;

    @Transactional
    public CajaDiaria abrir(UUID consultorioId, LocalDate fechaOperativa, String turnoCaja,
                             BigDecimal saldoInicial, String observaciones, String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);
        if (repositoryPort.existsAbierta(consultorioId, fechaOperativa, turnoCaja)) {
            throw new CajaConflictException(
                    "Ya existe una caja abierta para consultorio " + consultorioId
                    + " fecha " + fechaOperativa
                    + (turnoCaja != null ? " turno " + turnoCaja : ""));
        }

        CajaDiaria caja = CajaDiaria.builder()
                .consultorioId(consultorioId)
                .fechaOperativa(fechaOperativa)
                .turnoCaja(turnoCaja)
                .estado(CajaDiariaEstado.ABIERTA)
                .saldoInicial(saldoInicial != null ? saldoInicial : BigDecimal.ZERO)
                .totalIngresosPaciente(BigDecimal.ZERO)
                .totalIngresosOs(BigDecimal.ZERO)
                .totalEgresos(BigDecimal.ZERO)
                .observacionesApertura(observaciones)
                .abiertaPor(usuarioId)
                .abiertaEn(Instant.now())
                .build();

        CajaDiaria saved = repositoryPort.save(caja);
        auditoriaService.registrar(consultorioId, "CajaDiaria", saved.getId(),
                "APERTURA", null, CajaDiariaEstado.ABIERTA.name(), usuarioId, null);
        return saved;
    }

    @Transactional
    public CajaDiaria cerrar(UUID cajaId, BigDecimal saldoReal, String observaciones, String userEmail) {
        UUID usuarioId = resolveUserId(userEmail);
        CajaDiaria caja = findById(cajaId);
        if (!CajaDiariaEstado.ABIERTA.equals(caja.getEstado())) {
            throw new CajaConflictException("La caja " + cajaId + " no está en estado ABIERTA");
        }

        BigDecimal ingresos = safe(caja.getTotalIngresosPaciente()).add(safe(caja.getTotalIngresosOs()));
        BigDecimal saldoTeorico = safe(caja.getSaldoInicial()).add(ingresos).subtract(safe(caja.getTotalEgresos()));
        BigDecimal diferencia = saldoReal.subtract(saldoTeorico);

        CajaDiariaEstado nuevoEstado = diferencia.compareTo(BigDecimal.ZERO) == 0
                ? CajaDiariaEstado.CERRADA
                : CajaDiariaEstado.CERRADA_CON_DIFERENCIA;

        caja.setSaldoTeoricoCierre(saldoTeorico);
        caja.setSaldoRealCierre(saldoReal);
        caja.setDiferenciaCierre(diferencia);
        caja.setEstado(nuevoEstado);
        caja.setObservacionesCierre(observaciones);
        caja.setCerradaPor(usuarioId);
        caja.setCerradaEn(Instant.now());

        CajaDiaria saved = repositoryPort.save(caja);
        auditoriaService.registrar(caja.getConsultorioId(), "CajaDiaria", cajaId,
                "CIERRE", CajaDiariaEstado.ABIERTA.name(), nuevoEstado.name(), usuarioId, null);
        return saved;
    }

    @Transactional(readOnly = true)
    public CajaDiaria findById(UUID cajaId) {
        return repositoryPort.findById(cajaId)
                .orElseThrow(() -> new CajaNotFoundException("Caja no encontrada: " + cajaId));
    }

    @Transactional(readOnly = true)
    public CajaDiaria findAbiertaByConsultorioIdAndFechaAndTurno(UUID consultorioId, LocalDate fecha, String turnoCaja) {
        return repositoryPort.findAbiertaByConsultorioIdAndFechaAndTurno(consultorioId, fecha, turnoCaja)
                .orElseThrow(() -> new CajaNotFoundException(
                        "No hay caja abierta para consultorio " + consultorioId + " fecha " + fecha));
    }

    @Transactional(readOnly = true)
    public List<CajaDiaria> findByConsultorioIdAndFecha(UUID consultorioId, LocalDate fecha) {
        return repositoryPort.findByConsultorioIdAndFecha(consultorioId, fecha);
    }

    public void validarCajaAbierta(UUID cajaId) {
        CajaDiaria caja = findById(cajaId);
        if (!CajaDiariaEstado.ABIERTA.equals(caja.getEstado())) {
            throw new CajaConflictException("La caja " + cajaId + " no está abierta");
        }
    }

    @Transactional
    public CajaDiaria sumarIngresoPaciente(UUID cajaId, BigDecimal importe) {
        CajaDiaria caja = findById(cajaId);
        caja.setTotalIngresosPaciente(safe(caja.getTotalIngresosPaciente()).add(importe));
        return repositoryPort.save(caja);
    }

    @Transactional
    public CajaDiaria restarIngresoPaciente(UUID cajaId, BigDecimal importe) {
        CajaDiaria caja = findById(cajaId);
        caja.setTotalIngresosPaciente(safe(caja.getTotalIngresosPaciente()).subtract(importe));
        return repositoryPort.save(caja);
    }

    @Transactional
    public CajaDiaria sumarIngresosOs(UUID cajaId, BigDecimal importe) {
        CajaDiaria caja = findById(cajaId);
        caja.setTotalIngresosOs(safe(caja.getTotalIngresosOs()).add(importe));
        return repositoryPort.save(caja);
    }

    @Transactional
    public CajaDiaria restarIngresosOs(UUID cajaId, BigDecimal importe) {
        CajaDiaria caja = findById(cajaId);
        caja.setTotalIngresosOs(safe(caja.getTotalIngresosOs()).subtract(importe));
        return repositoryPort.save(caja);
    }

    private BigDecimal safe(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private UUID resolveUserId(String email) {
        return userRepo.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado"));
    }
}
