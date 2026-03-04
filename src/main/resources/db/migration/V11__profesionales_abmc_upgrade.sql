ALTER TABLE profesionales ADD COLUMN IF NOT EXISTS nro_documento VARCHAR(20);
ALTER TABLE profesionales ADD COLUMN IF NOT EXISTS especialidades VARCHAR(1000);
ALTER TABLE profesionales ADD COLUMN IF NOT EXISTS domicilio VARCHAR(255);
ALTER TABLE profesionales ADD COLUMN IF NOT EXISTS foto_perfil_url VARCHAR(500);
ALTER TABLE profesionales ADD COLUMN IF NOT EXISTS fecha_alta DATE NOT NULL DEFAULT CURRENT_DATE;
ALTER TABLE profesionales ADD COLUMN IF NOT EXISTS fecha_baja DATE;
ALTER TABLE profesionales ADD COLUMN IF NOT EXISTS motivo_baja VARCHAR(255);

UPDATE profesionales
SET especialidades = especialidad
WHERE especialidades IS NULL AND especialidad IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_profesionales_nro_documento
    ON profesionales(nro_documento);
