-- V26__sesion_evaluacion_estructurada.sql
-- Implementación del rediseño de Sesión de Evaluación (Bloques B, C, D, E)

-- Bloque B y E: Evaluación Base y Resultado de la Sesión
CREATE TABLE IF NOT EXISTS historia_clinica_sesion_evaluaciones (
    id                          UUID         NOT NULL PRIMARY KEY,
    sesion_id                   UUID         NOT NULL REFERENCES historia_clinica_sesiones(id) ON DELETE CASCADE,
    
    -- Bloque B: Evaluación Clínica Base
    dolor_intensidad            INTEGER      CHECK (dolor_intensidad >= 0 AND dolor_intensidad <= 10),
    dolor_zona                  VARCHAR(255),
    dolor_lateralidad           VARCHAR(50),
    dolor_tipo                  VARCHAR(100),
    dolor_comportamiento        VARCHAR(100),
    evolución_estado            VARCHAR(30), -- MEJOR, IGUAL, PEOR
    evolucion_nota              VARCHAR(500),
    objetivo_sesion             VARCHAR(500),
    limitacion_funcional        VARCHAR(1000),
    
    -- Bloque E: Resultado de la Sesión
    respuesta_paciente          VARCHAR(50), -- FAVORABLE, SIN_CAMBIOS, REGULAR, EMPEORA, PARCIAL
    tolerancia                  VARCHAR(30), -- BUENA, REGULAR, MALA
    indicaciones_domiciliarias  VARCHAR(1000),
    proxima_conducta            VARCHAR(100), -- CONTINUAR, AJUSTAR, REEVALUAR, ALTA, DERIVAR,estudios, suspender
    
    created_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_hc_sesion_eval UNIQUE (sesion_id)
);

CREATE INDEX idx_hc_sesion_eval_sesion ON historia_clinica_sesion_evaluaciones (sesion_id);

-- Bloque C: Examen Físico (Opcional en seguimientos, completo en evaluaciones)
CREATE TABLE IF NOT EXISTS historia_clinica_sesion_examen_fisico (
    id                          UUID         NOT NULL PRIMARY KEY,
    sesion_id                   UUID         NOT NULL REFERENCES historia_clinica_sesiones(id) ON DELETE CASCADE,
    
    -- Rangos de Movimiento (JSON para flexibilidad de múltiples articulaciones)
    rango_movimiento_json       TEXT, 
    -- Fuerza (JSON para múltiples grupos musculares)
    fuerza_muscular_json        TEXT,
    -- Funcionalidad y Marcha
    funcionalidad_nota          VARCHAR(1000),
    marcha_balance_nota         VARCHAR(1000),
    -- Signos Relevantes
    signos_inflamatorios        VARCHAR(1000), -- edema, inflamación, espasmo, etc.
    observaciones_neuro_resp    VARCHAR(1000),
    -- Tests específicos (JSON para catálogo flexible)
    tests_medidas_json          TEXT,
    
    created_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_hc_sesion_examen UNIQUE (sesion_id)
);

-- Bloque D: Tratamiento Aplicado (Intervenciones reales en la sesión)
CREATE TABLE IF NOT EXISTS historia_clinica_sesion_intervenciones (
    id                          UUID         NOT NULL PRIMARY KEY,
    sesion_id                   UUID         NOT NULL REFERENCES historia_clinica_sesiones(id) ON DELETE CASCADE,
    tratamiento_id              VARCHAR(100) NOT NULL, -- Referencia al catálogo de tratamientos
    tratamiento_nombre          VARCHAR(255) NOT NULL,
    técnica                     VARCHAR(255),
    zona                        VARCHAR(255),
    -- Parámetros específicos (JSON para soportar diferentes técnicas: electro, ejercicio, manual)
    parametros_json             TEXT, 
    duracion_minutos            INTEGER,
    profesional_id              UUID         REFERENCES profesionales(id), -- Para co-atención
    observaciones               VARCHAR(500),
    order_index                 INTEGER      NOT NULL DEFAULT 0,
    created_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hc_sesion_intervenciones_sesion ON historia_clinica_sesion_intervenciones (sesion_id, order_index);

-- Actualizar la tabla de sesiones principal con metadatos administrativos mínimos si faltan
ALTER TABLE historia_clinica_sesiones 
    ADD COLUMN IF NOT EXISTS duracion_real_minutos INTEGER;
ALTER TABLE historia_clinica_sesiones 
    ADD COLUMN IF NOT EXISTS asistencia_validada BOOLEAN DEFAULT TRUE;
