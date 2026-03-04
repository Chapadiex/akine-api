package com.akine_api.service;

import com.akine_api.application.dto.result.AntecedenteCatalogResult;
import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.application.service.ConsultorioAntecedenteCatalogService;
import com.akine_api.domain.model.Consultorio;
import com.akine_api.infrastructure.persistence.entity.ConsultorioAntecedenteCatalogEntity;
import com.akine_api.infrastructure.persistence.repository.ConsultorioAntecedenteCatalogJpaRepository;
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
class ConsultorioAntecedenteCatalogServiceTest {

    @Mock
    ConsultorioAntecedenteCatalogJpaRepository catalogRepo;
    @Mock
    ConsultorioRepositoryPort consultorioRepo;
    @Mock
    UserRepositoryPort userRepo;

    private ConsultorioAntecedenteCatalogService service;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");

    @BeforeEach
    void setUp() {
        service = new ConsultorioAntecedenteCatalogService(catalogRepo, consultorioRepo, userRepo, mapper);
        when(consultorioRepo.findById(CONSULTORIO_ID))
                .thenReturn(Optional.of(new Consultorio(CONSULTORIO_ID, "C", null, null, null, null, "ACTIVE", Instant.now())));
    }

    @Test
    void upsert_asAdmin_succeeds() throws Exception {
        JsonNode categories = mapper.readTree(validCatalogCategories());
        when(catalogRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.empty());
        when(catalogRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AntecedenteCatalogResult result = service.upsert(
                CONSULTORIO_ID,
                "1.0.0",
                categories,
                "admin@test.com",
                ADMIN_ROLES
        );

        assertThat(result).isNotNull();
        assertThat(result.consultorioId()).isEqualTo(CONSULTORIO_ID);
        assertThat(result.categories().isArray()).isTrue();
    }

    @Test
    void upsert_whenDeletesExistingItem_throws() throws Exception {
        JsonNode incoming = mapper.readTree(validCatalogCategoriesWithoutAqxItems());
        ConsultorioAntecedenteCatalogEntity entity = new ConsultorioAntecedenteCatalogEntity();
        entity.setConsultorioId(CONSULTORIO_ID);
        entity.setVersion("1.0.0");
        entity.setCatalogJson(validCatalogCategories());
        entity.setCreatedAt(Instant.now());
        entity.setCreatedBy("admin@test.com");
        entity.setUpdatedAt(Instant.now());
        entity.setUpdatedBy("admin@test.com");

        when(catalogRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.upsert(
                CONSULTORIO_ID,
                "1.0.0",
                incoming,
                "admin@test.com",
                ADMIN_ROLES
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se permite borrar items fisicamente");
    }

    @Test
    void restoreDefaults_addMissing_keepsCustomAndAddsDefaults() throws Exception {
        ConsultorioAntecedenteCatalogEntity entity = new ConsultorioAntecedenteCatalogEntity();
        entity.setConsultorioId(CONSULTORIO_ID);
        entity.setVersion("1.0.0");
        entity.setCatalogJson(validCatalogWithMissingOption());
        entity.setCreatedAt(Instant.now());
        entity.setCreatedBy("admin@test.com");
        entity.setUpdatedAt(Instant.now());
        entity.setUpdatedBy("admin@test.com");

        when(catalogRepo.findById(CONSULTORIO_ID)).thenReturn(Optional.of(entity));
        when(catalogRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AntecedenteCatalogResult result = service.restoreDefaults(
                CONSULTORIO_ID,
                ConsultorioAntecedenteCatalogService.DefaultsMode.ADD_MISSING,
                "admin@test.com",
                ADMIN_ROLES
        );

        assertThat(result.categories().toString()).contains("TIPO_I");
        assertThat(result.categories().toString()).contains("TIPO_II");
    }

    private String validCatalogCategories() {
        return """
                [
                  {"code":"APP","name":"Personales patológicos","order":10,"active":true,"items":[
                    {"code":"APP_DIABETES","label":"Diabetes","valueType":"ENUM","active":true,"order":10,
                      "options":[
                        {"code":"TIPO_I","label":"Tipo I","active":true,"order":10},
                        {"code":"TIPO_II","label":"Tipo II","active":true,"order":20}
                      ]
                    }
                  ]},
                  {"code":"AQX","name":"Quirúrgicos","order":20,"active":true,"items":[
                    {"code":"AQX_TRAUMA","label":"Cirugías traumatológicas","valueType":"REPEATABLE","active":true,"order":10,
                      "fields":[{"code":"zona","label":"Zona","type":"TEXT"}]
                    }
                  ]},
                  {"code":"MED","name":"Medicación actual","order":30,"active":true,"items":[
                    {"code":"MED_X","label":"X","valueType":"TEXT","active":true,"order":10}
                  ]},
                  {"code":"TRAU","name":"Traumatológicos","order":40,"active":true,"items":[
                    {"code":"TRAU_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]},
                  {"code":"FAM","name":"Familiares","order":50,"active":true,"items":[
                    {"code":"FAM_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]},
                  {"code":"HAB","name":"Hábitos y estilo de vida","order":60,"active":true,"items":[
                    {"code":"HAB_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]},
                  {"code":"ALG","name":"Alergias","order":70,"active":true,"items":[
                    {"code":"ALG_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]}
                ]
                """;
    }

    private String validCatalogCategoriesWithoutAqxItems() {
        return """
                [
                  {"code":"APP","name":"Personales patológicos","order":10,"active":true,"items":[
                    {"code":"APP_DIABETES","label":"Diabetes","valueType":"ENUM","active":true,"order":10,
                      "options":[
                        {"code":"TIPO_I","label":"Tipo I","active":true,"order":10},
                        {"code":"TIPO_II","label":"Tipo II","active":true,"order":20}
                      ]
                    }
                  ]},
                  {"code":"AQX","name":"Quirúrgicos","order":20,"active":true,"items":[]},
                  {"code":"MED","name":"Medicación actual","order":30,"active":true,"items":[
                    {"code":"MED_X","label":"X","valueType":"TEXT","active":true,"order":10}
                  ]},
                  {"code":"TRAU","name":"Traumatológicos","order":40,"active":true,"items":[
                    {"code":"TRAU_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]},
                  {"code":"FAM","name":"Familiares","order":50,"active":true,"items":[
                    {"code":"FAM_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]},
                  {"code":"HAB","name":"Hábitos y estilo de vida","order":60,"active":true,"items":[
                    {"code":"HAB_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]},
                  {"code":"ALG","name":"Alergias","order":70,"active":true,"items":[
                    {"code":"ALG_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]}
                ]
                """;
    }

    private String validCatalogWithMissingOption() {
        return """
                [
                  {"code":"APP","name":"Personales patológicos","order":10,"active":true,"items":[
                    {"code":"APP_DIABETES","label":"Diabetes","valueType":"ENUM","active":true,"order":10,
                      "options":[{"code":"TIPO_I","label":"Tipo I","active":true,"order":10}]
                    }
                  ]},
                  {"code":"AQX","name":"Quirúrgicos","order":20,"active":true,"items":[
                    {"code":"AQX_TRAUMA","label":"Cirugías traumatológicas","valueType":"REPEATABLE","active":true,"order":10,
                      "fields":[{"code":"zona","label":"Zona","type":"TEXT"}]
                    }
                  ]},
                  {"code":"MED","name":"Medicación actual","order":30,"active":true,"items":[
                    {"code":"MED_X","label":"X","valueType":"TEXT","active":true,"order":10}
                  ]},
                  {"code":"TRAU","name":"Traumatológicos","order":40,"active":true,"items":[
                    {"code":"TRAU_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]},
                  {"code":"FAM","name":"Familiares","order":50,"active":true,"items":[
                    {"code":"FAM_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]},
                  {"code":"HAB","name":"Hábitos y estilo de vida","order":60,"active":true,"items":[
                    {"code":"HAB_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]},
                  {"code":"ALG","name":"Alergias","order":70,"active":true,"items":[
                    {"code":"ALG_X","label":"X","valueType":"BOOLEAN","active":true,"order":10}
                  ]}
                ]
                """;
    }
}

