-- V48: nro_consultorio + slug en consultorios, tabla plan_definicion con seed
-- Fase 1 SaaS: identificador humano de tenant + definición de planes parametrizados

-- ============================================================
-- 1) Secuencia para nro_consultorio correlativo
-- ============================================================
CREATE SEQUENCE IF NOT EXISTS seq_consultorio_nro
    START WITH 1 INCREMENT BY 1 NO CYCLE;

-- ============================================================
-- 2) Columnas nro_consultorio y slug en consultorios
-- ============================================================
ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS nro_consultorio VARCHAR(20);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS slug VARCHAR(100);

-- Índices únicos sobre columnas nullable.
-- PostgreSQL permite múltiples NULL en UNIQUE INDEX y H2 no soporta
-- el predicado WHERE en este contexto de migración.
CREATE UNIQUE INDEX IF NOT EXISTS uq_consultorios_nro_consultorio
    ON consultorios (nro_consultorio);

CREATE UNIQUE INDEX IF NOT EXISTS uq_consultorios_slug
    ON consultorios (slug);

-- ============================================================
-- 3) Tabla plan_definicion
-- ============================================================
CREATE TABLE IF NOT EXISTS plan_definicion (
    codigo              VARCHAR(40)     PRIMARY KEY,
    nombre              VARCHAR(100)    NOT NULL,
    descripcion         TEXT,
    precio_mensual      NUMERIC(10,2),
    precio_anual        NUMERIC(10,2),
    -- Límites operativos (NULL = ilimitado)
    max_consultorios    INT,
    max_profesionales   INT,
    max_pacientes       INT,
    -- Feature flags
    modulo_facturacion      BOOLEAN NOT NULL DEFAULT FALSE,
    modulo_historia_clinica BOOLEAN NOT NULL DEFAULT FALSE,
    modulo_obras_sociales   BOOLEAN NOT NULL DEFAULT FALSE,
    modulo_colaboradores    BOOLEAN NOT NULL DEFAULT FALSE,
    -- Metadatos
    activo              BOOLEAN NOT NULL DEFAULT TRUE,
    orden               INT     NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 4) Seed de planes iniciales
-- ============================================================
INSERT INTO plan_definicion (
    codigo, nombre, descripcion,
    precio_mensual, precio_anual,
    max_consultorios, max_profesionales, max_pacientes,
    modulo_facturacion, modulo_historia_clinica, modulo_obras_sociales, modulo_colaboradores,
    activo, orden
) VALUES
    ('BASICO',        'Básico',
     'Ideal para consultorios unipersonales. Incluye Turnos y Agenda.',
     15000.00, 150000.00,
     1, 3, 500,
     FALSE, FALSE, FALSE, FALSE,
     TRUE, 1),
    ('PROFESIONAL',   'Profesional',
     'Para centros con múltiples profesionales. Incluye Historia Clínica, Obras Sociales y Colaboradores.',
     35000.00, 350000.00,
     3, 10, NULL,
     TRUE, TRUE, TRUE, TRUE,
     TRUE, 2),
    ('ENTERPRISE',    'Enterprise',
     'Sin límites operativos para grandes centros de kinesiología.',
     80000.00, 800000.00,
     NULL, NULL, NULL,
     TRUE, TRUE, TRUE, TRUE,
     TRUE, 3);

-- ============================================================
-- 5) Normalizar plan_code en suscripciones existentes
--    antes de agregar FK (STARTER y valores legacy → BASICO)
-- ============================================================
UPDATE suscripciones
   SET plan_code = 'BASICO'
 WHERE plan_code IS NULL
    OR plan_code NOT IN ('BASICO', 'PROFESIONAL', 'ENTERPRISE');

-- ============================================================
-- 6) FK suscripciones.plan_code → plan_definicion.codigo
-- ============================================================
ALTER TABLE suscripciones
    ADD CONSTRAINT fk_suscripciones_plan_definicion
        FOREIGN KEY (plan_code) REFERENCES plan_definicion(codigo);
