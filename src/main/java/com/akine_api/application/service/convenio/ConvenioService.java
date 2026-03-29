package com.akine_api.application.service.convenio;

import com.akine_api.application.dto.convenio.ActualizarArancelesRequest;
import com.akine_api.application.dto.convenio.ActualizarConvenioRequest;
import com.akine_api.application.dto.convenio.ArancelDTO;
import com.akine_api.application.dto.convenio.ArancelResumenDTO;
import com.akine_api.application.dto.convenio.ConvenioDTO;
import com.akine_api.application.dto.convenio.ConvenioVersionDTO;
import com.akine_api.application.dto.convenio.NuevoArancelRequest;
import com.akine_api.application.dto.convenio.NuevoConvenioRequest;
import com.akine_api.application.dto.convenio.RenovarConvenioRequest;
import com.akine_api.application.mapper.convenio.ConvenioDTOMapper;
import com.akine_api.domain.model.cobertura.FinanciadorSalud;
import com.akine_api.domain.model.convenio.Arancel;
import com.akine_api.domain.model.convenio.Convenio;
import com.akine_api.domain.model.convenio.ConvenioVersion;
import com.akine_api.domain.model.convenio.ConvenioVersionEstado;
import com.akine_api.domain.model.convenio.CoseguroTipo;
import com.akine_api.domain.model.convenio.ModalidadConvenio;
import com.akine_api.domain.repository.cobertura.FinanciadorSaludRepositoryPort;
import com.akine_api.domain.repository.convenio.ArancelRepositoryPort;
import com.akine_api.domain.repository.convenio.ConvenioRepositoryPort;
import com.akine_api.domain.repository.convenio.ConvenioVersionRepositoryPort;
import com.akine_api.domain.repository.convenio.PrestacionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConvenioService {

    private final ConvenioRepositoryPort convenioRepository;
    private final ConvenioVersionRepositoryPort versionRepository;
    private final ArancelRepositoryPort arancelRepository;
    private final PrestacionRepositoryPort prestacionRepository;
    private final FinanciadorSaludRepositoryPort financiadorRepository;
    private final ConvenioDTOMapper mapper;

    @Transactional
    public ConvenioDTO create(UUID consultorioId, NuevoConvenioRequest req) {
        log.info("Creando convenio para consultorioId={} financiadorId={}", consultorioId, req.getFinanciadorId());

        FinanciadorSalud financiador = financiadorRepository.findById(req.getFinanciadorId())
                .orElseThrow(() -> new RuntimeException("Financiador no encontrado: " + req.getFinanciadorId()));

        String sigla = financiador.getNombreCorto() != null ? financiador.getNombreCorto() : financiador.getNombre();
        String siglaDisplay = sigla + (req.getPlan() != null ? " · " + req.getPlan() : "");

        Convenio convenio = Convenio.builder()
                .consultorioId(consultorioId)
                .financiadorId(req.getFinanciadorId())
                .plan(req.getPlan())
                .siglaDisplay(siglaDisplay)
                .modalidad(req.getModalidad() != null ? ModalidadConvenio.valueOf(req.getModalidad()) : null)
                .diaCierre(req.getDiaCierre())
                .requiereAut(req.getRequiereAut() != null ? req.getRequiereAut() : false)
                .requiereOrden(req.getRequiereOrden() != null ? req.getRequiereOrden() : false)
                .build();

        Convenio savedConvenio = convenioRepository.save(convenio);

        LocalDate vigDesde = LocalDate.parse(req.getVigenciaDesde());
        LocalDate vigHasta = req.getVigenciaHasta() != null ? LocalDate.parse(req.getVigenciaHasta()) : null;

        ConvenioVersion version = ConvenioVersion.builder()
                .convenioId(savedConvenio.getId())
                .versionNum(1)
                .vigenciaDesde(vigDesde)
                .vigenciaHasta(vigHasta)
                .estado(ConvenioVersionEstado.VIGENTE)
                .build();

        ConvenioVersion savedVersion = versionRepository.save(version);

        List<Arancel> aranceles = new ArrayList<>();
        if (req.getAranceles() != null) {
            for (NuevoArancelRequest arReq : req.getAranceles()) {
                Arancel arancel = buildArancel(arReq, savedVersion.getId(), vigDesde);
                aranceles.add(arancelRepository.save(arancel));
            }
        }

        return buildConvenioDTO(savedConvenio, savedVersion, aranceles, financiador);
    }

    @Transactional(readOnly = true)
    public List<ConvenioDTO> findByConsultorio(UUID consultorioId) {
        return convenioRepository.findByConsultorioId(consultorioId).stream()
                .map(convenio -> {
                    ConvenioVersion version = versionRepository.findVigenteByConvenioId(convenio.getId()).orElse(null);
                    List<Arancel> aranceles = version != null
                            ? arancelRepository.findByConvenioVersionId(version.getId())
                            : List.of();
                    FinanciadorSalud financiador = financiadorRepository.findById(convenio.getFinanciadorId()).orElse(null);
                    return buildConvenioDTO(convenio, version, aranceles, financiador);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConvenioDTO findById(UUID id) {
        Convenio convenio = convenioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Convenio no encontrado: " + id));
        ConvenioVersion version = versionRepository.findVigenteByConvenioId(id).orElse(null);
        List<Arancel> aranceles = version != null
                ? arancelRepository.findByConvenioVersionId(version.getId())
                : List.of();
        FinanciadorSalud financiador = financiadorRepository.findById(convenio.getFinanciadorId()).orElse(null);
        return buildConvenioDTO(convenio, version, aranceles, financiador);
    }

    @Transactional
    public ConvenioDTO renovar(UUID convenioId, RenovarConvenioRequest req) {
        log.info("Renovando convenio id={}", convenioId);

        Convenio convenio = convenioRepository.findById(convenioId)
                .orElseThrow(() -> new RuntimeException("Convenio no encontrado: " + convenioId));

        ConvenioVersion versionActual = versionRepository.findVigenteByConvenioId(convenioId)
                .orElseThrow(() -> new RuntimeException("No existe version vigente para convenio: " + convenioId));

        LocalDate nuevaVigDesde = LocalDate.parse(req.getVigenciaDesde());

        // Cerrar version actual
        versionActual.setVigenciaHasta(nuevaVigDesde.minusDays(1));
        versionActual.setEstado(ConvenioVersionEstado.CERRADA);
        versionActual.setMotivoCierre(req.getMotivoCierre());
        versionRepository.save(versionActual);

        // Cerrar aranceles de version anterior
        List<Arancel> arancelesActuales = arancelRepository.findByConvenioVersionId(versionActual.getId());
        for (Arancel a : arancelesActuales) {
            if (a.getVigenciaHasta() == null) {
                a.setVigenciaHasta(nuevaVigDesde.minusDays(1));
                a.setActivo(false);
                arancelRepository.save(a);
            }
        }

        LocalDate vigHasta = req.getVigenciaHasta() != null ? LocalDate.parse(req.getVigenciaHasta()) : null;

        ConvenioVersion nuevaVersion = ConvenioVersion.builder()
                .convenioId(convenioId)
                .versionNum(versionActual.getVersionNum() + 1)
                .vigenciaDesde(nuevaVigDesde)
                .vigenciaHasta(vigHasta)
                .estado(ConvenioVersionEstado.VIGENTE)
                .motivoCierre(null)
                .build();

        ConvenioVersion savedVersion = versionRepository.save(nuevaVersion);

        List<Arancel> nuevosAranceles = new ArrayList<>();
        if (req.getAranceles() != null) {
            for (NuevoArancelRequest arReq : req.getAranceles()) {
                Arancel arancel = buildArancel(arReq, savedVersion.getId(), nuevaVigDesde);
                nuevosAranceles.add(arancelRepository.save(arancel));
            }
        }

        FinanciadorSalud financiador = financiadorRepository.findById(convenio.getFinanciadorId()).orElse(null);
        return buildConvenioDTO(convenio, savedVersion, nuevosAranceles, financiador);
    }

    @Transactional
    public ConvenioDTO actualizarAranceles(UUID convenioId, ActualizarArancelesRequest req) {
        log.info("Actualizando aranceles para convenioId={}", convenioId);

        Convenio convenio = convenioRepository.findById(convenioId)
                .orElseThrow(() -> new RuntimeException("Convenio no encontrado: " + convenioId));

        ConvenioVersion versionActual = versionRepository.findVigenteByConvenioId(convenioId)
                .orElseThrow(() -> new RuntimeException("No existe version vigente para convenio: " + convenioId));

        LocalDate nuevaVigDesde = LocalDate.parse(req.getVigenciaDesde());
        LocalDate nuevaVigHasta = req.getVigenciaHasta() != null ? LocalDate.parse(req.getVigenciaHasta()) : null;

        List<Arancel> arancelesExistentes = arancelRepository.findByConvenioVersionId(versionActual.getId());

        List<Arancel> arancelesActualizados = new ArrayList<>();

        for (Arancel existente : arancelesExistentes) {
            boolean aplica = req.getPrestacionIds() == null
                    || req.getPrestacionIds().contains(existente.getPrestacionId());

            if (!aplica) {
                arancelesActualizados.add(existente);
                continue;
            }

            // Cerrar arancel existente
            existente.setVigenciaHasta(nuevaVigDesde.minusDays(1));
            existente.setActivo(false);
            arancelRepository.save(existente);

            // Calcular nuevo importe
            BigDecimal nuevoImporteOs = calcularNuevoImporte(existente.getImporteOs(), req);
            BigDecimal nuevoImporteTotal = calcularImporteTotal(nuevoImporteOs, existente.getCoseguroTipo(), existente.getCoseguroValor());

            Arancel nuevo = Arancel.builder()
                    .convenioVersionId(versionActual.getId())
                    .prestacionId(existente.getPrestacionId())
                    .prestacionCodigo(existente.getPrestacionCodigo())
                    .prestacionNombre(existente.getPrestacionNombre())
                    .importeOs(nuevoImporteOs)
                    .coseguroTipo(existente.getCoseguroTipo())
                    .coseguroValor(existente.getCoseguroValor())
                    .importeTotal(nuevoImporteTotal)
                    .sesionesMesMax(existente.getSesionesMesMax())
                    .sesionesAnioMax(existente.getSesionesAnioMax())
                    .requiereAutOverride(existente.getRequiereAutOverride())
                    .vigenciaDesde(nuevaVigDesde)
                    .vigenciaHasta(nuevaVigHasta)
                    .activo(true)
                    .build();

            arancelesActualizados.add(arancelRepository.save(nuevo));
        }

        FinanciadorSalud financiador = financiadorRepository.findById(convenio.getFinanciadorId()).orElse(null);
        return buildConvenioDTO(convenio, versionActual, arancelesActualizados, financiador);
    }

    @Transactional
    public ConvenioDTO agregarArancel(UUID convenioId, NuevoArancelRequest req) {
        log.info("Agregando arancel a convenioId={}", convenioId);
        Convenio convenio = convenioRepository.findById(convenioId)
                .orElseThrow(() -> new RuntimeException("Convenio no encontrado: " + convenioId));
        ConvenioVersion version = versionRepository.findVigenteByConvenioId(convenioId)
                .orElseThrow(() -> new RuntimeException("No existe version vigente para convenio: " + convenioId));
        Arancel arancel = buildArancel(req, version.getId(), version.getVigenciaDesde());
        arancelRepository.save(arancel);
        List<Arancel> aranceles = arancelRepository.findByConvenioVersionId(version.getId());
        FinanciadorSalud financiador = financiadorRepository.findById(convenio.getFinanciadorId()).orElse(null);
        return buildConvenioDTO(convenio, version, aranceles, financiador);
    }

    @Transactional
    public ConvenioDTO updateConvenio(UUID convenioId, ActualizarConvenioRequest req) {
        log.info("Actualizando convenio id={}", convenioId);
        Convenio convenio = convenioRepository.findById(convenioId)
                .orElseThrow(() -> new RuntimeException("Convenio no encontrado: " + convenioId));
        if (req.getModalidad() != null) {
            convenio.setModalidad(ModalidadConvenio.valueOf(req.getModalidad()));
        }
        if (req.getDiaCierre() != null) {
            convenio.setDiaCierre(req.getDiaCierre());
        }
        if (req.getRequiereAut() != null) {
            convenio.setRequiereAut(req.getRequiereAut());
        }
        if (req.getRequiereOrden() != null) {
            convenio.setRequiereOrden(req.getRequiereOrden());
        }
        convenioRepository.save(convenio);

        ConvenioVersion version = versionRepository.findVigenteByConvenioId(convenioId).orElse(null);
        if (version != null && req.getVigenciaHasta() != null) {
            version.setVigenciaHasta(req.getVigenciaHasta().isBlank() ? null : LocalDate.parse(req.getVigenciaHasta()));
            versionRepository.save(version);
        }

        List<Arancel> aranceles = version != null
                ? arancelRepository.findByConvenioVersionId(version.getId())
                : List.of();
        FinanciadorSalud financiador = financiadorRepository.findById(convenio.getFinanciadorId()).orElse(null);
        return buildConvenioDTO(convenio, version, aranceles, financiador);
    }

    @Transactional
    public void delete(UUID convenioId) {
        log.info("Eliminando convenio id={}", convenioId);
        convenioRepository.delete(convenioId);
    }

    @Transactional(readOnly = true)
    public List<ConvenioVersionDTO> findVersiones(UUID convenioId) {
        return versionRepository.findByConvenioId(convenioId).stream()
                .map(v -> {
                    ConvenioVersionDTO dto = mapper.toDto(v);
                    dto.setCantidadLotes(versionRepository.countLotesByVersionId(v.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ConvenioDTO cambiarEstadoVersion(UUID convenioId, String estadoStr, String motivo) {
        log.info("Cambiando estado version de convenio id={} a estado={}", convenioId, estadoStr);

        Convenio convenio = convenioRepository.findById(convenioId)
                .orElseThrow(() -> new RuntimeException("Convenio no encontrado: " + convenioId));

        ConvenioVersion versionActual = versionRepository.findVigenteByConvenioId(convenioId)
                .orElseThrow(() -> new RuntimeException("No existe version vigente para convenio: " + convenioId));

        ConvenioVersionEstado nuevoEstado = ConvenioVersionEstado.valueOf(estadoStr);
        versionActual.setEstado(nuevoEstado);
        versionActual.setMotivoCierre(motivo);
        versionRepository.save(versionActual);

        List<Arancel> aranceles = arancelRepository.findByConvenioVersionId(versionActual.getId());
        FinanciadorSalud financiador = financiadorRepository.findById(convenio.getFinanciadorId()).orElse(null);
        return buildConvenioDTO(convenio, versionActual, aranceles, financiador);
    }

    private BigDecimal calcularNuevoImporte(BigDecimal importeOs, ActualizarArancelesRequest req) {
        if ("porcentaje".equals(req.getMetodo())) {
            BigDecimal factor = BigDecimal.ONE.add(req.getValor().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
            return importeOs.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        } else if ("importe_directo".equals(req.getMetodo())) {
            return req.getValor().setScale(2, RoundingMode.HALF_UP);
        }
        return importeOs;
    }

    private Arancel buildArancel(NuevoArancelRequest req, UUID versionId, LocalDate defaultVigDesde) {
        CoseguroTipo coseguroTipo = req.getCoseguroTipo() != null
                ? CoseguroTipo.valueOf(req.getCoseguroTipo())
                : CoseguroTipo.NINGUNO;

        BigDecimal importeTotal = calcularImporteTotal(req.getImporteOs(), coseguroTipo, req.getCoseguroValor());

        LocalDate vigDesde = req.getVigenciaDesde() != null
                ? LocalDate.parse(req.getVigenciaDesde())
                : defaultVigDesde;
        LocalDate vigHasta = req.getVigenciaHasta() != null
                ? LocalDate.parse(req.getVigenciaHasta())
                : null;

        String prestacionCodigo = null;
        String prestacionNombre = null;
        if (req.getPrestacionId() != null) {
            prestacionRepository.findById(req.getPrestacionId()).ifPresent(p -> {
                // campos denormalizados se setean abajo
            });
            var prestacionOpt = prestacionRepository.findById(req.getPrestacionId());
            if (prestacionOpt.isPresent()) {
                prestacionCodigo = prestacionOpt.get().getCodigoNomenclador();
                prestacionNombre = prestacionOpt.get().getNombre();
            }
        }

        return Arancel.builder()
                .convenioVersionId(versionId)
                .prestacionId(req.getPrestacionId())
                .prestacionCodigo(prestacionCodigo)
                .prestacionNombre(prestacionNombre)
                .importeOs(req.getImporteOs())
                .coseguroTipo(coseguroTipo)
                .coseguroValor(req.getCoseguroValor())
                .importeTotal(importeTotal)
                .sesionesMesMax(req.getSesionesMesMax())
                .sesionesAnioMax(req.getSesionesAnioMax())
                .requiereAutOverride(req.getRequiereAutOverride())
                .vigenciaDesde(vigDesde)
                .vigenciaHasta(vigHasta)
                .activo(true)
                .build();
    }

    private BigDecimal calcularImporteTotal(BigDecimal importeOs, CoseguroTipo tipo, BigDecimal valor) {
        if (importeOs == null) {
            return BigDecimal.ZERO;
        }
        if (tipo == null || tipo == CoseguroTipo.NINGUNO) {
            return importeOs;
        }
        if (tipo == CoseguroTipo.FIJO) {
            return importeOs.add(valor != null ? valor : BigDecimal.ZERO);
        }
        if (tipo == CoseguroTipo.PORCENTAJE && valor != null && valor.compareTo(BigDecimal.valueOf(100)) < 0) {
            BigDecimal divisor = BigDecimal.ONE.subtract(valor.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
            return importeOs.divide(divisor, 2, RoundingMode.HALF_UP);
        }
        return importeOs;
    }

    private ConvenioDTO buildConvenioDTO(Convenio convenio, ConvenioVersion version,
                                         List<Arancel> aranceles, FinanciadorSalud financiador) {
        ConvenioDTO dto = mapper.toDto(convenio);

        if (financiador != null) {
            dto.setFinanciadorNombre(financiador.getNombre());
            dto.setFinanciadorSigla(financiador.getNombreCorto());
        }

        if (version != null) {
            ConvenioVersionDTO versionDTO = mapper.toDto(version);
            int cantidadLotes = versionRepository.countLotesByVersionId(version.getId());
            versionDTO.setCantidadLotes(cantidadLotes);

            List<ArancelDTO> arancelDTOs = aranceles.stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList());
            versionDTO.setAranceles(arancelDTOs);

            dto.setVersionActual(versionDTO);
        }

        List<ArancelResumenDTO> resumen = aranceles.stream()
                .filter(a -> Boolean.TRUE.equals(a.getActivo()))
                .map(a -> {
                    ArancelResumenDTO r = new ArancelResumenDTO();
                    r.setCodigoNomenclador(a.getPrestacionCodigo());
                    r.setNombrePrestacion(a.getPrestacionNombre());
                    r.setImporteTotal(a.getImporteTotal());
                    return r;
                })
                .collect(Collectors.toList());
        dto.setArancelesResumen(resumen);

        return dto;
    }
}
