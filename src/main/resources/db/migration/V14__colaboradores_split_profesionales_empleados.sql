-- Colaboradores split: profesionales + empleados administrativos

-- 1) Extender status de usuarios para contemplar rechazo de activación
ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_users_status;
ALTER TABLE users
    ADD CONSTRAINT chk_users_status
        CHECK (status IN ('PENDING','ACTIVE','SUSPENDED','REJECTED'));

-- 2) Vinculación opcional de profesional con cuenta de usuario
ALTER TABLE profesionales
    ADD COLUMN IF NOT EXISTS user_id UUID REFERENCES users(id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_profesionales_user_id
    ON profesionales(user_id);

-- 3) Empleados administrativos por consultorio
CREATE TABLE IF NOT EXISTS empleados (
    id             UUID         PRIMARY KEY,
    consultorio_id UUID         NOT NULL REFERENCES consultorios(id) ON DELETE CASCADE,
    user_id        UUID         REFERENCES users(id),
    nombre         VARCHAR(100) NOT NULL,
    apellido       VARCHAR(100) NOT NULL,
    dni            VARCHAR(20),
    cargo          VARCHAR(100) NOT NULL,
    nro_legajo     VARCHAR(50),
    email          VARCHAR(255) NOT NULL,
    telefono       VARCHAR(30),
    notas_internas VARCHAR(500),
    fecha_alta     DATE         NOT NULL DEFAULT CURRENT_DATE,
    fecha_baja     DATE,
    motivo_baja    VARCHAR(255),
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_empleados_consultorio_email UNIQUE (consultorio_id, email),
    CONSTRAINT uq_empleados_consultorio_dni UNIQUE (consultorio_id, dni),
    CONSTRAINT uq_empleados_user_id UNIQUE (user_id)
);

CREATE INDEX IF NOT EXISTS idx_empleados_consultorio
    ON empleados (consultorio_id);

CREATE INDEX IF NOT EXISTS idx_empleados_consultorio_cargo
    ON empleados (consultorio_id, cargo);

CREATE INDEX IF NOT EXISTS idx_empleados_user_id
    ON empleados (user_id);
