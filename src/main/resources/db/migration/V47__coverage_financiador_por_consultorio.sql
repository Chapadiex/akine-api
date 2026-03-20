-- V47: Financiadores pasan a ser por consultorio
-- Agrega consultorio_id a coverage_financiador_salud
-- Reemplaza el UNIQUE global por (consultorio_id, nombre)
-- Elimina el UNIQUE (financiador_id, nombre_plan) de planes (reemplazado por lógica de solapamiento de vigencia)

ALTER TABLE coverage_financiador_salud
    ADD COLUMN consultorio_id UUID NOT NULL REFERENCES consultorios(id) ON DELETE CASCADE;

ALTER TABLE coverage_financiador_salud
    DROP CONSTRAINT IF EXISTS uq_coverage_financiador_nombre;

ALTER TABLE coverage_financiador_salud
    ADD CONSTRAINT uq_coverage_financiador_consultorio_nombre UNIQUE (consultorio_id, nombre);

CREATE INDEX IF NOT EXISTS idx_coverage_financiador_consultorio ON coverage_financiador_salud(consultorio_id);

ALTER TABLE coverage_plan_financiador
    DROP CONSTRAINT IF EXISTS uq_coverage_plan_financiador_nombre;
