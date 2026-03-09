package com.akine_api.service;

import com.akine_api.application.dto.result.TratamientoCatalogResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.ConsultorioTratamientoCatalogService;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.infrastructure.persistence.entity.ConsultorioTratamientoCatalogEntity;
import com.akine_api.infrastructure.persistence.repository.ConsultorioTratamientoCatalogJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultorioTratamientoCatalogServiceTest {

    @Mock
    ConsultorioTratamientoCatalogJpaRepository catalogRepo;
    @Mock
    ConsultorioRepositoryPort consultorioRepo;
    @Mock
    UserRepositoryPort userRepo;

    private ConsultorioTratamientoCatalogService service;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");

    @BeforeEach
    void setUp() {
        service = new ConsultorioTratamientoCatalogService(catalogRepo, consultorioRepo, userRepo, mapper);
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
    }

    @Test
    void upsert_asAdmin_succeeds() throws Exception {
        JsonNode root = mapper.readTree(validCatalogRoot());
        when(catalogRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.empty());
        when(catalogRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TratamientoCatalogResult result = service.upsert(
                CONSULTORIO_ID,
                "1.0.0",
                "ARS",
                "AR",
                root.path("observaciones"),
                root.path("tipos"),
                root.path("categorias"),
                root.path("tratamientos"),
                "admin@test.com",
                ADMIN_ROLES
        );

        assertThat(result.consultorioId()).isEqualTo(CONSULTORIO_ID);
        assertThat(result.tratamientos().isArray()).isTrue();
        assertThat(result.tratamientos()).hasSize(2);
    }

    @Test
    void upsert_whenDeletesExistingTreatment_throws() throws Exception {
        JsonNode incoming = mapper.readTree(validCatalogWithoutOneTreatment());
        ConsultorioTratamientoCatalogEntity entity = new ConsultorioTratamientoCatalogEntity();
        entity.setConsultorioId(CONSULTORIO_ID);
        entity.setVersion("1.0.0");
        entity.setCatalogJson(validCatalogRoot());
        entity.setCreatedAt(Instant.now());
        entity.setCreatedBy("admin@test.com");
        entity.setUpdatedAt(Instant.now());
        entity.setUpdatedBy("admin@test.com");

        when(catalogRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.upsert(
                CONSULTORIO_ID,
                "1.0.0",
                "ARS",
                "AR",
                incoming.path("observaciones"),
                incoming.path("tipos"),
                incoming.path("categorias"),
                incoming.path("tratamientos"),
                "admin@test.com",
                ADMIN_ROLES
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se permite borrar tratamientos");
    }

    @Test
    void restoreDefaults_addMissing_keepsCustomAndAddsDefaults() {
        ConsultorioTratamientoCatalogEntity entity = new ConsultorioTratamientoCatalogEntity();
        entity.setConsultorioId(CONSULTORIO_ID);
        entity.setVersion("1.0.0");
        entity.setCatalogJson(validCatalogMissingOneDefault());
        entity.setCreatedAt(Instant.now());
        entity.setCreatedBy("admin@test.com");
        entity.setUpdatedAt(Instant.now());
        entity.setUpdatedBy("admin@test.com");

        when(catalogRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(entity));
        when(catalogRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TratamientoCatalogResult result = service.restoreDefaults(
                CONSULTORIO_ID,
                ConsultorioTratamientoCatalogService.DefaultsMode.ADD_MISSING,
                "admin@test.com",
                ADMIN_ROLES
        );

        assertThat(result.tratamientos().toString()).contains("KIN001");
        assertThat(result.tratamientos().toString()).contains("REH001");
    }

    @Test
    void requireActiveTreatment_whenInactive_throws() throws Exception {
        ConsultorioTratamientoCatalogEntity entity = new ConsultorioTratamientoCatalogEntity();
        entity.setConsultorioId(CONSULTORIO_ID);
        entity.setVersion("1.0.0");
        entity.setCatalogJson(mapper.writeValueAsString(mapper.readTree(validCatalogWithInactiveTreatment())));
        entity.setCreatedAt(Instant.now());
        entity.setCreatedBy("admin@test.com");
        entity.setUpdatedAt(Instant.now());
        entity.setUpdatedBy("admin@test.com");

        when(catalogRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.requireActiveTreatment(CONSULTORIO_ID, "KIN001"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inactivo");
    }

    private String validCatalogRoot() {
        return """
                {
                  "consultorioId": "CONS-001",
                  "version": "1.0.0",
                  "monedaNomenclador": "ARS",
                  "pais": "AR",
                  "observaciones": ["obs"],
                  "tipos": ["PRINCIPAL", "TECNICA"],
                  "categorias": [
                    {"id":"CAT001","codigo":"REHAB_GENERAL","nombre":"Rehabilitacion general"},
                    {"id":"CAT002","codigo":"EJERCICIO_TERAPEUTICO","nombre":"Ejercicio terapeutico"}
                  ],
                  "tratamientos": [
                    {"id":"TR001","codigoInterno":"KIN001","nombre":"Kinesioterapia motora","categoriaCodigo":"REHAB_GENERAL","tipo":"PRINCIPAL","descripcion":"desc","facturable":true,"requierePrescripcionMedica":false,"requiereAutorizacion":false,"duracionSugeridaMinutos":45,"modalidades":["CONSULTORIO"],"activo":true,"precioReferencia":null,"codigosFinanciador":[]},
                    {"id":"TR002","codigoInterno":"REH001","nombre":"Rehabilitacion funcional","categoriaCodigo":"REHAB_GENERAL","tipo":"PRINCIPAL","descripcion":"desc","facturable":true,"requierePrescripcionMedica":false,"requiereAutorizacion":false,"duracionSugeridaMinutos":45,"modalidades":["CONSULTORIO"],"activo":true,"precioReferencia":null,"codigosFinanciador":[]}
                  ]
                }
                """;
    }

    private String validCatalogWithoutOneTreatment() {
        return """
                {
                  "consultorioId": "CONS-001",
                  "version": "1.0.0",
                  "monedaNomenclador": "ARS",
                  "pais": "AR",
                  "observaciones": ["obs"],
                  "tipos": ["PRINCIPAL", "TECNICA"],
                  "categorias": [
                    {"id":"CAT001","codigo":"REHAB_GENERAL","nombre":"Rehabilitacion general"},
                    {"id":"CAT002","codigo":"EJERCICIO_TERAPEUTICO","nombre":"Ejercicio terapeutico"}
                  ],
                  "tratamientos": [
                    {"id":"TR001","codigoInterno":"KIN001","nombre":"Kinesioterapia motora","categoriaCodigo":"REHAB_GENERAL","tipo":"PRINCIPAL","descripcion":"desc","facturable":true,"requierePrescripcionMedica":false,"requiereAutorizacion":false,"duracionSugeridaMinutos":45,"modalidades":["CONSULTORIO"],"activo":true,"precioReferencia":null,"codigosFinanciador":[]}
                  ]
                }
                """;
    }

    private String validCatalogMissingOneDefault() {
        return """
                {
                  "consultorioId": "CONS-001",
                  "version": "1.0.0",
                  "monedaNomenclador": "ARS",
                  "pais": "AR",
                  "observaciones": ["obs"],
                  "tipos": ["PRINCIPAL"],
                  "categorias": [
                    {"id":"CAT001","codigo":"REHAB_GENERAL","nombre":"Rehabilitacion general"}
                  ],
                  "tratamientos": [
                    {"id":"TR001","codigoInterno":"KIN001","nombre":"Kinesioterapia motora","categoriaCodigo":"REHAB_GENERAL","tipo":"PRINCIPAL","descripcion":"desc","facturable":true,"requierePrescripcionMedica":false,"requiereAutorizacion":false,"duracionSugeridaMinutos":45,"modalidades":["CONSULTORIO"],"activo":true,"precioReferencia":null,"codigosFinanciador":[]}
                  ]
                }
                """;
    }

    private String validCatalogWithInactiveTreatment() {
        return """
                {
                  "consultorioId": "CONS-001",
                  "version": "1.0.0",
                  "monedaNomenclador": "ARS",
                  "pais": "AR",
                  "observaciones": ["obs"],
                  "tipos": ["PRINCIPAL", "TECNICA"],
                  "categorias": [
                    {"id":"CAT001","codigo":"REHAB_GENERAL","nombre":"Rehabilitacion general"}
                  ],
                  "tratamientos": [
                    {"id":"TR001","codigoInterno":"KIN001","nombre":"Kinesioterapia motora","categoriaCodigo":"REHAB_GENERAL","tipo":"PRINCIPAL","descripcion":"desc","facturable":true,"requierePrescripcionMedica":false,"requiereAutorizacion":false,"duracionSugeridaMinutos":45,"modalidades":["CONSULTORIO"],"activo":false,"precioReferencia":null,"codigosFinanciador":[]}
                  ]
                }
                """;
    }
}
