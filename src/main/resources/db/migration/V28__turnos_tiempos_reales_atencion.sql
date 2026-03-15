ALTER TABLE turnos ADD COLUMN IF NOT EXISTS fecha_hora_inicio_real TIMESTAMP NULL;
ALTER TABLE turnos ADD COLUMN IF NOT EXISTS fecha_hora_fin_real TIMESTAMP NULL;

CREATE INDEX IF NOT EXISTS idx_turnos_estado_fecha_fin
    ON turnos(consultorio_id, estado, fecha_hora_inicio);
