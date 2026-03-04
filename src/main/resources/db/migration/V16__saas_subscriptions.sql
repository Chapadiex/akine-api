-- SaaS subscriptions: empresa + suscripcion + auditoria + enlace a consultorio/usuario

-- 1) Empresas
CREATE TABLE IF NOT EXISTS empresas (
    id          UUID         PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    cuit        VARCHAR(20)  NOT NULL,
    address     VARCHAR(500),
    city        VARCHAR(150),
    province    VARCHAR(150),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_empresas_cuit UNIQUE (cuit)
);

-- 2) Datos fiscales del dueño en users
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS documento_fiscal VARCHAR(30);

-- 3) Relacion de consultorio con empresa
ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS empresa_id UUID;

ALTER TABLE consultorios
    ADD CONSTRAINT fk_consultorios_empresa
        FOREIGN KEY (empresa_id) REFERENCES empresas(id);

CREATE INDEX IF NOT EXISTS idx_consultorios_empresa_id
    ON consultorios (empresa_id);

-- 4) Suscripciones SaaS
CREATE TABLE IF NOT EXISTS suscripciones (
    id                  UUID         PRIMARY KEY,
    owner_user_id       UUID         NOT NULL REFERENCES users(id),
    empresa_id          UUID         NOT NULL REFERENCES empresas(id),
    consultorio_base_id UUID         NOT NULL REFERENCES consultorios(id),
    status              VARCHAR(20)  NOT NULL,
    requested_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    start_date          DATE,
    end_date            DATE,
    reviewed_at         TIMESTAMP,
    reviewed_by_user_id UUID         REFERENCES users(id),
    rejection_reason    VARCHAR(500),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_suscripciones_status
        CHECK (status IN ('PENDING','ACTIVE','REJECTED','EXPIRED','SUSPENDED')),
    CONSTRAINT uq_suscripciones_consultorio_base UNIQUE (consultorio_base_id),
    CONSTRAINT chk_suscripciones_fechas CHECK (
        start_date IS NULL
        OR end_date IS NULL
        OR start_date <= end_date
    )
);

CREATE INDEX IF NOT EXISTS idx_suscripciones_status
    ON suscripciones (status);

CREATE INDEX IF NOT EXISTS idx_suscripciones_end_date
    ON suscripciones (end_date);

CREATE INDEX IF NOT EXISTS idx_suscripciones_owner_user_id
    ON suscripciones (owner_user_id);

-- 5) Auditoria de suscripciones
CREATE TABLE IF NOT EXISTS suscripcion_auditoria (
    id            UUID         PRIMARY KEY,
    suscripcion_id UUID        NOT NULL REFERENCES suscripciones(id) ON DELETE CASCADE,
    action        VARCHAR(40)  NOT NULL,
    from_status   VARCHAR(20),
    to_status     VARCHAR(20),
    actor_user_id UUID         REFERENCES users(id),
    reason        VARCHAR(500),
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_suscripcion_auditoria_suscripcion_id
    ON suscripcion_auditoria (suscripcion_id);
