-- Empleados: normalizacion de formulario de alta + catalogo parametrico de cargos

CREATE TABLE IF NOT EXISTS cargo_empleado_catalogo (
    id         UUID         PRIMARY KEY,
    nombre     VARCHAR(80)  NOT NULL,
    slug       VARCHAR(120) NOT NULL,
    activo     BOOLEAN      NOT NULL DEFAULT TRUE,
    orden      INT          NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_cargo_empleado_catalogo_slug UNIQUE (slug)
);

CREATE INDEX IF NOT EXISTS idx_cargo_empleado_catalogo_activo_orden
    ON cargo_empleado_catalogo (activo, orden, nombre);

INSERT INTO cargo_empleado_catalogo (id, nombre, slug, activo, orden)
SELECT CAST('d3b5364c-6a02-4f54-b456-9209a97f3f01' AS UUID), 'Recepcion', 'recepcion', TRUE, 10
WHERE NOT EXISTS (SELECT 1 FROM cargo_empleado_catalogo WHERE slug = 'recepcion');

INSERT INTO cargo_empleado_catalogo (id, nombre, slug, activo, orden)
SELECT CAST('d3b5364c-6a02-4f54-b456-9209a97f3f02' AS UUID), 'Administrativo', 'administrativo', TRUE, 20
WHERE NOT EXISTS (SELECT 1 FROM cargo_empleado_catalogo WHERE slug = 'administrativo');

INSERT INTO cargo_empleado_catalogo (id, nombre, slug, activo, orden)
SELECT CAST('d3b5364c-6a02-4f54-b456-9209a97f3f03' AS UUID), 'Asistente', 'asistente', TRUE, 30
WHERE NOT EXISTS (SELECT 1 FROM cargo_empleado_catalogo WHERE slug = 'asistente');

INSERT INTO cargo_empleado_catalogo (id, nombre, slug, activo, orden)
SELECT CAST('d3b5364c-6a02-4f54-b456-9209a97f3f04' AS UUID), 'Coordinador', 'coordinador', TRUE, 40
WHERE NOT EXISTS (SELECT 1 FROM cargo_empleado_catalogo WHERE slug = 'coordinador');

INSERT INTO cargo_empleado_catalogo (id, nombre, slug, activo, orden)
SELECT CAST('d3b5364c-6a02-4f54-b456-9209a97f3f05' AS UUID), 'Facturacion', 'facturacion', TRUE, 50
WHERE NOT EXISTS (SELECT 1 FROM cargo_empleado_catalogo WHERE slug = 'facturacion');

ALTER TABLE empleados DROP COLUMN IF EXISTS nro_legajo;

ALTER TABLE empleados ADD COLUMN IF NOT EXISTS fecha_nacimiento DATE;
ALTER TABLE empleados ADD COLUMN IF NOT EXISTS direccion VARCHAR(255);

UPDATE empleados
SET fecha_nacimiento = COALESCE(fecha_nacimiento, fecha_alta, CURRENT_DATE);

UPDATE empleados
SET direccion = COALESCE(NULLIF(TRIM(direccion), ''), 'Sin direccion informada');

ALTER TABLE empleados ALTER COLUMN fecha_nacimiento SET NOT NULL;
ALTER TABLE empleados ALTER COLUMN direccion SET NOT NULL;
