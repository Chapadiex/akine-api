CREATE TABLE IF NOT EXISTS especialidad_catalogo (
    id UUID PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS consultorio_especialidad (
    id UUID PRIMARY KEY,
    consultorio_id UUID NOT NULL REFERENCES consultorios(id) ON DELETE CASCADE,
    especialidad_id UUID NOT NULL REFERENCES especialidad_catalogo(id),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_consultorio_especialidad UNIQUE (consultorio_id, especialidad_id)
);

CREATE INDEX IF NOT EXISTS idx_consultorio_especialidad_consultorio
    ON consultorio_especialidad (consultorio_id);

CREATE INDEX IF NOT EXISTS idx_consultorio_especialidad_consultorio_activo
    ON consultorio_especialidad (consultorio_id, activo);

CREATE INDEX IF NOT EXISTS idx_consultorio_especialidad_especialidad
    ON consultorio_especialidad (especialidad_id);

INSERT INTO especialidad_catalogo (id, nombre, slug, activo)
SELECT v.id, v.nombre, v.slug, v.activo
FROM (
    VALUES
        ('f16d20f5-2f20-4fd7-8c81-d34f2f4f7cd0', 'Kinesiología', 'kinesiologia', TRUE),
        ('176be768-b6ef-4e5f-b6ef-613f1cbca53d', 'Fisioterapia', 'fisioterapia', TRUE),
        ('4f8d18de-e26d-4864-9bc5-c8762c42a96d', 'Rehabilitación traumatológica', 'rehabilitacion-traumatologica', TRUE),
        ('d41cb174-6ee8-42f3-b159-e3f758ce9b4a', 'Rehabilitación deportiva', 'rehabilitacion-deportiva', TRUE),
        ('7725f260-ab04-496f-8f15-8cfca6faed77', 'Rehabilitación neurológica', 'rehabilitacion-neurologica', TRUE),
        ('8f1a6755-5e6c-45f4-9e2e-01a6abec95bf', 'Rehabilitación respiratoria', 'rehabilitacion-respiratoria', TRUE),
        ('3674e5ff-cfc8-4e3d-b3eb-2e57ccb8fd95', 'Rehabilitación pediátrica', 'rehabilitacion-pediatrica', TRUE),
        ('dc45c6f6-2f6a-4e5f-b906-8f5fb8f8fd8c', 'Fisiatría', 'fisiatria', TRUE),
        ('9d914026-fd03-4d97-89da-1ad01795b8e8', 'Medicina del deporte', 'medicina-del-deporte', TRUE),
        ('11e78395-6202-4bb6-83b2-22d5f7d3f28c', 'Traumatología', 'traumatologia', TRUE),
        ('f1f879b3-c3fe-4251-a0b8-056082923ba6', 'Ortopedia', 'ortopedia', TRUE),
        ('4fb0cc4c-f502-4c29-9558-82d715f52f11', 'Reumatología', 'reumatologia', TRUE),
        ('af8a20a3-1673-4bf7-a311-0cc65f06fca1', 'Terapia ocupacional', 'terapia-ocupacional', TRUE),
        ('1ae2e7b3-007e-4548-9dce-a80f4a304de4', 'Psicomotricidad', 'psicomotricidad', TRUE),
        ('799f10d7-c9cd-4c33-80d8-45452bbaf0cb', 'Ergonomía y reeducación postural', 'ergonomia-y-reeducacion-postural', TRUE)
) AS v(id, nombre, slug, activo)
WHERE NOT EXISTS (
    SELECT 1
    FROM especialidad_catalogo ec
    WHERE ec.slug = v.slug
);

-- Nota:
-- El backfill de consultorio_especialidad se ejecuta en bootstrap de aplicacion
-- (ConsultorioEspecialidadBootstrapService) para mantener compatibilidad H2/PostgreSQL.
