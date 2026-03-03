-- Permitir horarios cortados por dia en consultorio_horarios
ALTER TABLE consultorio_horarios DROP CONSTRAINT IF EXISTS uq_consultorio_dia;

CREATE INDEX IF NOT EXISTS idx_horarios_consultorio_dia
    ON consultorio_horarios(consultorio_id, dia_semana, hora_apertura);
