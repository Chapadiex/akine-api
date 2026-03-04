CREATE TABLE IF NOT EXISTS obras_sociales (
    id                      UUID          NOT NULL PRIMARY KEY,
    consultorio_id          UUID          NOT NULL REFERENCES consultorios(id),
    acronimo                VARCHAR(20)   NOT NULL,
    nombre_completo         VARCHAR(120)  NOT NULL,
    cuit                    VARCHAR(13)   NOT NULL,
    email                   VARCHAR(255),
    telefono                VARCHAR(30),
    telefono_alternativo    VARCHAR(30),
    representante           VARCHAR(120),
    observaciones_internas  VARCHAR(1000),
    direccion_linea         VARCHAR(255),
    estado                  VARCHAR(20)   NOT NULL,
    created_at              TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_obras_sociales_consultorio_cuit UNIQUE (consultorio_id, cuit)
);

CREATE TABLE IF NOT EXISTS obra_social_planes (
    id                               UUID          NOT NULL PRIMARY KEY,
    obra_social_id                   UUID          NOT NULL REFERENCES obras_sociales(id) ON DELETE CASCADE,
    nombre_corto                     VARCHAR(60)   NOT NULL,
    nombre_corto_norm                VARCHAR(60)   NOT NULL,
    nombre_completo                  VARCHAR(120)  NOT NULL,
    tipo_cobertura                   VARCHAR(20)   NOT NULL,
    valor_cobertura                  DECIMAL(12,2) NOT NULL,
    tipo_coseguro                    VARCHAR(20)   NOT NULL,
    valor_coseguro                   DECIMAL(12,2) NOT NULL,
    prestaciones_sin_autorizacion    INTEGER       NOT NULL DEFAULT 0,
    observaciones                    VARCHAR(1000),
    activo                           BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at                       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_plan_nombre_corto_norm UNIQUE (obra_social_id, nombre_corto_norm)
);

CREATE INDEX IF NOT EXISTS idx_obras_sociales_consultorio_estado
    ON obras_sociales (consultorio_id, estado);

CREATE INDEX IF NOT EXISTS idx_obras_sociales_consultorio_nombre
    ON obras_sociales (consultorio_id, nombre_completo);

CREATE INDEX IF NOT EXISTS idx_planes_obra_social
    ON obra_social_planes (obra_social_id);

