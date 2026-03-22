-- Fase 2: Campos para cierre clínico obligatorio de sesión
-- El cierre clínico requiere: tratamiento_realizado, resultado_clinico,
-- duracion_real_minutos, conducta_siguiente + confirmación del profesional

ALTER TABLE historia_clinica_sesiones ADD COLUMN IF NOT EXISTS duracion_real_minutos INTEGER;
ALTER TABLE historia_clinica_sesiones ADD COLUMN IF NOT EXISTS tratamiento_realizado TEXT;
ALTER TABLE historia_clinica_sesiones ADD COLUMN IF NOT EXISTS resultado_clinico TEXT;
ALTER TABLE historia_clinica_sesiones ADD COLUMN IF NOT EXISTS conducta_siguiente VARCHAR(500);
ALTER TABLE historia_clinica_sesiones ADD COLUMN IF NOT EXISTS requiere_seguimiento BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE historia_clinica_sesiones ADD COLUMN IF NOT EXISTS observaciones_clinicas TEXT;
ALTER TABLE historia_clinica_sesiones ADD COLUMN IF NOT EXISTS cerrada_clinicamente BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE historia_clinica_sesiones ADD COLUMN IF NOT EXISTS fecha_cierre_clinico TIMESTAMP;
ALTER TABLE historia_clinica_sesiones ADD COLUMN IF NOT EXISTS cierre_clinico_por UUID;
ALTER TABLE historia_clinica_sesiones ADD COLUMN IF NOT EXISTS es_grupal BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE historia_clinica_sesiones ADD COLUMN IF NOT EXISTS grupo_sesion_id UUID;
