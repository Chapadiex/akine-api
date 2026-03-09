ALTER TABLE historia_clinica_plan_tratamientos_detalle
    ADD COLUMN IF NOT EXISTS tratamiento_categoria_codigo_snapshot VARCHAR(100);

ALTER TABLE historia_clinica_plan_tratamientos_detalle
    ADD COLUMN IF NOT EXISTS tratamiento_categoria_nombre_snapshot VARCHAR(120);

ALTER TABLE historia_clinica_plan_tratamientos_detalle
    ADD COLUMN IF NOT EXISTS tratamiento_tipo_snapshot VARCHAR(30);

ALTER TABLE historia_clinica_plan_tratamientos_detalle
    ADD COLUMN IF NOT EXISTS tratamiento_requiere_autorizacion_snapshot BOOLEAN;

ALTER TABLE historia_clinica_plan_tratamientos_detalle
    ADD COLUMN IF NOT EXISTS tratamiento_requiere_prescripcion_medica_snapshot BOOLEAN;

ALTER TABLE historia_clinica_plan_tratamientos_detalle
    ADD COLUMN IF NOT EXISTS tratamiento_duracion_sugerida_minutos_snapshot INTEGER;

CREATE INDEX IF NOT EXISTS idx_hc_plan_detalle_tratamiento_id
    ON historia_clinica_plan_tratamientos_detalle (tratamiento_id);
