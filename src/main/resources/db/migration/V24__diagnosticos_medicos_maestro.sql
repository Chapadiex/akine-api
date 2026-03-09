CREATE TABLE IF NOT EXISTS consultorio_diagnosticos_medicos (
    consultorio_id      UUID         NOT NULL PRIMARY KEY REFERENCES consultorios(id) ON DELETE CASCADE,
    version             VARCHAR(20)  NOT NULL,
    maestro_json        TEXT         NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(120) NOT NULL,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(120) NOT NULL
);

ALTER TABLE historia_clinica_atenciones_iniciales
    ADD COLUMN IF NOT EXISTS diagnostico_codigo VARCHAR(100);

ALTER TABLE historia_clinica_atenciones_iniciales
    ADD COLUMN IF NOT EXISTS diagnostico_nombre VARCHAR(255);

ALTER TABLE historia_clinica_atenciones_iniciales
    ADD COLUMN IF NOT EXISTS diagnostico_tipo VARCHAR(40);

ALTER TABLE historia_clinica_atenciones_iniciales
    ADD COLUMN IF NOT EXISTS diagnostico_categoria_codigo VARCHAR(100);

ALTER TABLE historia_clinica_atenciones_iniciales
    ADD COLUMN IF NOT EXISTS diagnostico_categoria_nombre VARCHAR(120);

ALTER TABLE historia_clinica_atenciones_iniciales
    ADD COLUMN IF NOT EXISTS diagnostico_subcategoria VARCHAR(120);

ALTER TABLE historia_clinica_atenciones_iniciales
    ADD COLUMN IF NOT EXISTS diagnostico_region_anatomica VARCHAR(120);

ALTER TABLE historia_clinica_atenciones_iniciales
    ADD COLUMN IF NOT EXISTS diagnostico_observacion VARCHAR(1000);

ALTER TABLE historia_clinica_diagnosticos
    ADD COLUMN IF NOT EXISTS diagnostico_tipo VARCHAR(40);

ALTER TABLE historia_clinica_diagnosticos
    ADD COLUMN IF NOT EXISTS diagnostico_categoria_codigo VARCHAR(100);

ALTER TABLE historia_clinica_diagnosticos
    ADD COLUMN IF NOT EXISTS diagnostico_categoria_nombre VARCHAR(120);

ALTER TABLE historia_clinica_diagnosticos
    ADD COLUMN IF NOT EXISTS diagnostico_subcategoria VARCHAR(120);

ALTER TABLE historia_clinica_diagnosticos
    ADD COLUMN IF NOT EXISTS diagnostico_region_anatomica VARCHAR(120);

CREATE INDEX IF NOT EXISTS idx_hc_atencion_inicial_diagnostico_codigo
    ON historia_clinica_atenciones_iniciales (consultorio_id, diagnostico_codigo);

CREATE INDEX IF NOT EXISTS idx_hc_diagnostico_codigo
    ON historia_clinica_diagnosticos (consultorio_id, codigo);
