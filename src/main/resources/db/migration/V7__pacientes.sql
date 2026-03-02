CREATE TABLE IF NOT EXISTS pacientes (
    id                          UUID PRIMARY KEY,
    dni                         VARCHAR(20)  NOT NULL,
    nombre                      VARCHAR(100) NOT NULL,
    apellido                    VARCHAR(100) NOT NULL,
    telefono                    VARCHAR(30)  NOT NULL,
    email                       VARCHAR(255),
    fecha_nacimiento            DATE,
    sexo                        VARCHAR(30),
    domicilio                   VARCHAR(255),
    nacionalidad                VARCHAR(100),
    estado_civil                VARCHAR(50),
    profesion                   VARCHAR(100),
    obra_social_nombre          VARCHAR(150),
    obra_social_plan            VARCHAR(100),
    obra_social_nro_afiliado    VARCHAR(100),
    user_id                     UUID UNIQUE REFERENCES users(id),
    activo                      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by_user_id          UUID REFERENCES users(id),
    created_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_pacientes_dni UNIQUE (dni)
);

CREATE TABLE IF NOT EXISTS paciente_consultorios (
    id                  UUID PRIMARY KEY,
    paciente_id         UUID NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,
    consultorio_id      UUID NOT NULL REFERENCES consultorios(id) ON DELETE CASCADE,
    created_by_user_id  UUID REFERENCES users(id),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_paciente_consultorio UNIQUE (paciente_id, consultorio_id)
);

CREATE INDEX IF NOT EXISTS idx_pacientes_apellido_nombre
    ON pacientes(apellido, nombre);
CREATE INDEX IF NOT EXISTS idx_paciente_consultorios_consultorio
    ON paciente_consultorios(consultorio_id);
CREATE INDEX IF NOT EXISTS idx_paciente_consultorios_paciente
    ON paciente_consultorios(paciente_id);
