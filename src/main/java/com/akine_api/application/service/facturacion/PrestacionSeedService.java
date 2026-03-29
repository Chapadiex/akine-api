package com.akine_api.application.service.facturacion;

import com.akine_api.domain.model.convenio.ModalidadPrestacion;
import com.akine_api.domain.model.convenio.Prestacion;
import com.akine_api.domain.repository.convenio.PrestacionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrestacionSeedService {

    private final PrestacionRepositoryPort repositoryPort;

    private record SeedEntry(String codigo, String nombre, ModalidadPrestacion modalidad,
                              boolean esModulo, String codigosIncluidos, boolean requiereAutBase) {}

    private static final List<SeedEntry> DEFAULTS = List.of(
        new SeedEntry("25.01.01", "Sesión fisiokinesica en consultorio",       ModalidadPrestacion.CONSULTORIO, false, null,                              false),
        new SeedEntry("25.01.02", "Sesión kinesioterapia en consultorio",      ModalidadPrestacion.CONSULTORIO, false, null,                              false),
        new SeedEntry("25.01.06", "Sesión fisiokinesica a domicilio",          ModalidadPrestacion.DOMICILIO,   false, null,                              true),
        new SeedEntry("25.01.07", "Sesión con magnetoterapia / láser",         ModalidadPrestacion.CONSULTORIO, false, null,                              false),
        new SeedEntry("25.01.09", "Sesión kinesio + nebulización",             ModalidadPrestacion.CONSULTORIO, false, null,                              false),
        new SeedEntry("25.01.10", "Rehabilitación neurológica",                ModalidadPrestacion.CONSULTORIO, false, null,                              true),
        new SeedEntry("25.01.15", "Drenaje linfático manual",                  ModalidadPrestacion.CONSULTORIO, false, null,                              true),
        new SeedEntry("25.01.16", "Tratamiento neurológico (sin CUD)",         ModalidadPrestacion.CONSULTORIO, false, null,                              false),
        new SeedEntry("25.01.17", "RPG (Reeducación Postural Global)",         ModalidadPrestacion.CONSULTORIO, false, null,                              true),
        new SeedEntry("25.50.01", "Magnetoterapia",                            ModalidadPrestacion.CONSULTORIO, false, null,                              false),
        new SeedEntry("43.04.01", "Kinesioterapia respiratoria",               ModalidadPrestacion.CONSULTORIO, false, null,                              false),
        new SeedEntry("43.04.02", "Kinesio + nebulización (respiratoria)",     ModalidadPrestacion.CONSULTORIO, false, null,                              false),
        new SeedEntry("25.60.11", "Módulo fisio-kinésico completo",            ModalidadPrestacion.COMBINADO,   true,  "25.01.01 + 25.01.02 + 25.01.06",  false),
        new SeedEntry("25.60.17", "Módulo fisio-kinésico + láser/magneto",     ModalidadPrestacion.COMBINADO,   true,  "25.01.01 + 25.01.02 + 25.01.07",  false),
        new SeedEntry("25.60.23", "Módulo práctica especializada",             ModalidadPrestacion.COMBINADO,   true,  null,                              true)
    );

    @Transactional
    public void seedIfEmpty() {
        for (SeedEntry entry : DEFAULTS) {
            if (repositoryPort.findByCodigoNomenclador(entry.codigo()).isEmpty()) {
                Prestacion p = Prestacion.builder()
                        .id(UUID.randomUUID())
                        .codigoNomenclador(entry.codigo())
                        .nombre(entry.nombre())
                        .modalidad(entry.modalidad())
                        .esModulo(entry.esModulo())
                        .codigosIncluidos(entry.codigosIncluidos())
                        .requiereAutBase(entry.requiereAutBase())
                        .activa(true)
                        .build();
                repositoryPort.save(p);
                log.info("Prestación seeded: {} - {}", entry.codigo(), entry.nombre());
            }
        }
    }
}
