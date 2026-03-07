-- Cargos comunes para todos los consultorios (administrativos + operativos)
-- Idempotente: actualiza nombres base existentes e inserta faltantes.

UPDATE cargo_empleado_catalogo
SET nombre = 'Recepcionista', orden = 10, activo = TRUE
WHERE slug = 'recepcion';

UPDATE cargo_empleado_catalogo
SET nombre = 'Administrativo', orden = 20, activo = TRUE
WHERE slug = 'administrativo';

UPDATE cargo_empleado_catalogo
SET nombre = 'Coordinador administrativo', orden = 40, activo = TRUE
WHERE slug = 'coordinador';

UPDATE cargo_empleado_catalogo
SET nombre = 'Facturacion / Obras sociales', orden = 60, activo = TRUE
WHERE slug = 'facturacion';

UPDATE cargo_empleado_catalogo
SET nombre = 'Asistente de sala', orden = 90, activo = TRUE
WHERE slug = 'asistente';

INSERT INTO cargo_empleado_catalogo (id, nombre, slug, activo, orden)
SELECT CAST('d3b5364c-6a02-4f54-b456-9209a97f3f06' AS UUID), 'Secretaria / Secretario', 'secretaria-secretario', TRUE, 30
WHERE NOT EXISTS (SELECT 1 FROM cargo_empleado_catalogo WHERE slug = 'secretaria-secretario');

INSERT INTO cargo_empleado_catalogo (id, nombre, slug, activo, orden)
SELECT CAST('d3b5364c-6a02-4f54-b456-9209a97f3f07' AS UUID), 'Encargado de turnos', 'encargado-de-turnos', TRUE, 50
WHERE NOT EXISTS (SELECT 1 FROM cargo_empleado_catalogo WHERE slug = 'encargado-de-turnos');

INSERT INTO cargo_empleado_catalogo (id, nombre, slug, activo, orden)
SELECT CAST('d3b5364c-6a02-4f54-b456-9209a97f3f08' AS UUID), 'Personal de limpieza', 'personal-de-limpieza', TRUE, 70
WHERE NOT EXISTS (SELECT 1 FROM cargo_empleado_catalogo WHERE slug = 'personal-de-limpieza');

INSERT INTO cargo_empleado_catalogo (id, nombre, slug, activo, orden)
SELECT CAST('d3b5364c-6a02-4f54-b456-9209a97f3f09' AS UUID), 'Mantenimiento', 'mantenimiento', TRUE, 80
WHERE NOT EXISTS (SELECT 1 FROM cargo_empleado_catalogo WHERE slug = 'mantenimiento');

INSERT INTO cargo_empleado_catalogo (id, nombre, slug, activo, orden)
SELECT CAST('d3b5364c-6a02-4f54-b456-9209a97f3f10' AS UUID), 'Auxiliar', 'auxiliar', TRUE, 100
WHERE NOT EXISTS (SELECT 1 FROM cargo_empleado_catalogo WHERE slug = 'auxiliar');
