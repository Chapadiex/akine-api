CREATE TABLE IF NOT EXISTS historia_clinica_legajos (
    id                  UUID        NOT NULL PRIMARY KEY,
    consultorio_id      UUID        NOT NULL REFERENCES consultorios(id),
    paciente_id         UUID        NOT NULL REFERENCES pacientes(id),
    created_by_user_id  UUID,
    updated_by_user_id  UUID,
    created_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_hc_legajos_consultorio_paciente UNIQUE (consultorio_id, paciente_id)
);

CREATE INDEX idx_hc_legajos_consultorio
    ON historia_clinica_legajos (consultorio_id, updated_at DESC);

CREATE TABLE IF NOT EXISTS historia_clinica_antecedentes (
    id                  UUID         NOT NULL PRIMARY KEY,
    legajo_id           UUID         NOT NULL REFERENCES historia_clinica_legajos(id) ON DELETE CASCADE,
    consultorio_id      UUID         NOT NULL REFERENCES consultorios(id),
    paciente_id         UUID         NOT NULL REFERENCES pacientes(id),
    category_code       VARCHAR(100),
    catalog_item_code   VARCHAR(100),
    label               VARCHAR(255) NOT NULL,
    value_text          VARCHAR(2000),
    critical            BOOLEAN      NOT NULL DEFAULT FALSE,
    notes               VARCHAR(1000),
    created_by_user_id  UUID,
    updated_by_user_id  UUID,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hc_antecedentes_legajo
    ON historia_clinica_antecedentes (legajo_id, updated_at DESC);

CREATE INDEX idx_hc_antecedentes_paciente
    ON historia_clinica_antecedentes (consultorio_id, paciente_id, critical DESC, updated_at DESC);
