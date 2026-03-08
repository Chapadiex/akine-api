CREATE TABLE IF NOT EXISTS historia_clinica_atenciones_iniciales (
    id                          UUID         NOT NULL PRIMARY KEY,
    legajo_id                   UUID         NOT NULL REFERENCES historia_clinica_legajos(id) ON DELETE CASCADE,
    consultorio_id              UUID         NOT NULL REFERENCES consultorios(id),
    paciente_id                 UUID         NOT NULL REFERENCES pacientes(id),
    profesional_id              UUID         NOT NULL REFERENCES profesionales(id),
    fecha_hora                  TIMESTAMP    NOT NULL,
    tipo_ingreso                VARCHAR(40)  NOT NULL,
    motivo_consulta_breve       VARCHAR(500),
    sintomas_principales        VARCHAR(1000),
    tiempo_evolucion            VARCHAR(255),
    observaciones               VARCHAR(2000),
    especialidad_derivante      VARCHAR(255),
    profesional_derivante       VARCHAR(255),
    fecha_prescripcion          DATE,
    diagnostico_texto           VARCHAR(1000),
    observaciones_prescripcion  VARCHAR(1000),
    resumen_clinico_inicial     VARCHAR(2000),
    hallazgos_relevantes        VARCHAR(2000),
    created_by_user_id          UUID,
    updated_by_user_id          UUID,
    created_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hc_atencion_inicial_paciente_fecha
    ON historia_clinica_atenciones_iniciales (consultorio_id, paciente_id, fecha_hora DESC);

CREATE INDEX idx_hc_atencion_inicial_legajo
    ON historia_clinica_atenciones_iniciales (legajo_id, fecha_hora DESC);

CREATE TABLE IF NOT EXISTS historia_clinica_atencion_inicial_evaluacion (
    id                          UUID          NOT NULL PRIMARY KEY,
    atencion_inicial_id         UUID          NOT NULL REFERENCES historia_clinica_atenciones_iniciales(id) ON DELETE CASCADE,
    peso                        DECIMAL(10,2),
    altura                      DECIMAL(10,2),
    imc                         DECIMAL(10,2),
    presion_arterial            VARCHAR(30),
    frecuencia_cardiaca         INTEGER,
    saturacion                  INTEGER,
    temperatura                 DECIMAL(5,2),
    observaciones               VARCHAR(1000),
    created_at                  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_hc_atencion_eval UNIQUE (atencion_inicial_id)
);

CREATE TABLE IF NOT EXISTS historia_clinica_planes_terapeuticos (
    id                          UUID         NOT NULL PRIMARY KEY,
    atencion_inicial_id         UUID         NOT NULL REFERENCES historia_clinica_atenciones_iniciales(id) ON DELETE CASCADE,
    consultorio_id              UUID         NOT NULL REFERENCES consultorios(id),
    paciente_id                 UUID         NOT NULL REFERENCES pacientes(id),
    profesional_id              UUID         NOT NULL REFERENCES profesionales(id),
    estado                      VARCHAR(20)  NOT NULL,
    observaciones_generales     VARCHAR(2000),
    created_by_user_id          UUID,
    updated_by_user_id          UUID,
    created_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_hc_plan_atencion UNIQUE (atencion_inicial_id)
);

CREATE INDEX idx_hc_plan_paciente
    ON historia_clinica_planes_terapeuticos (consultorio_id, paciente_id, created_at DESC);

CREATE TABLE IF NOT EXISTS historia_clinica_plan_tratamientos_detalle (
    id                              UUID          NOT NULL PRIMARY KEY,
    plan_terapeutico_id             UUID          NOT NULL REFERENCES historia_clinica_planes_terapeuticos(id) ON DELETE CASCADE,
    tratamiento_id                  VARCHAR(100)  NOT NULL,
    tratamiento_nombre_snapshot     VARCHAR(255)  NOT NULL,
    cantidad_sesiones               INTEGER       NOT NULL,
    frecuencia_sugerida             VARCHAR(120),
    caracter_caso                   VARCHAR(30)   NOT NULL,
    fecha_estimada_inicio           DATE,
    requiere_autorizacion           BOOLEAN       NOT NULL DEFAULT FALSE,
    observaciones                   VARCHAR(1000),
    observaciones_administrativas   VARCHAR(1000),
    order_index                     INTEGER       NOT NULL DEFAULT 0,
    created_at                      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hc_plan_detalle_plan
    ON historia_clinica_plan_tratamientos_detalle (plan_terapeutico_id, order_index ASC);

CREATE TABLE IF NOT EXISTS consultorio_tratamiento_catalog (
    consultorio_id      UUID         NOT NULL PRIMARY KEY REFERENCES consultorios(id) ON DELETE CASCADE,
    version             VARCHAR(20)  NOT NULL,
    catalog_json        TEXT         NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(120) NOT NULL,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(120) NOT NULL
);

ALTER TABLE historia_clinica_adjuntos
    ADD COLUMN IF NOT EXISTS atencion_inicial_id UUID REFERENCES historia_clinica_atenciones_iniciales(id);

ALTER TABLE historia_clinica_adjuntos
    ALTER COLUMN sesion_id DROP NOT NULL;

CREATE INDEX IF NOT EXISTS idx_hc_adjuntos_atencion_inicial
    ON historia_clinica_adjuntos (atencion_inicial_id, created_at ASC);
