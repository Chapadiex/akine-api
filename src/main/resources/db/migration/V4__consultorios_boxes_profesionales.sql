-- Campos nuevos en consultorios existente
ALTER TABLE consultorios ADD COLUMN IF NOT EXISTS cuit VARCHAR(13);
ALTER TABLE consultorios ADD COLUMN IF NOT EXISTS email VARCHAR(255);

-- Tabla boxes
CREATE TABLE IF NOT EXISTS boxes (
    id             UUID         NOT NULL PRIMARY KEY,
    consultorio_id UUID         NOT NULL REFERENCES consultorios(id),
    nombre         VARCHAR(100) NOT NULL,
    codigo         VARCHAR(50),
    tipo           VARCHAR(20)  NOT NULL,
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_box_codigo_consultorio UNIQUE (consultorio_id, codigo)
);

-- Tabla profesionales
CREATE TABLE IF NOT EXISTS profesionales (
    id              UUID         NOT NULL PRIMARY KEY,
    consultorio_id  UUID         NOT NULL REFERENCES consultorios(id),
    nombre          VARCHAR(100) NOT NULL,
    apellido        VARCHAR(100) NOT NULL,
    matricula       VARCHAR(50)  NOT NULL,
    especialidad    VARCHAR(150),
    email           VARCHAR(255),
    telefono        VARCHAR(30),
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_matricula_consultorio UNIQUE (consultorio_id, matricula)
);

CREATE INDEX IF NOT EXISTS idx_boxes_consultorio ON boxes(consultorio_id);
CREATE INDEX IF NOT EXISTS idx_profesionales_consultorio ON profesionales(consultorio_id);
