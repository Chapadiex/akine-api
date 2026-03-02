-- Eliminar placeholder inicial
DROP TABLE IF EXISTS clinics;

-- ==========================
-- USUARIOS
-- ==========================
CREATE TABLE users (
    id          UUID        PRIMARY KEY,
    email       VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(30),
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_status CHECK (status IN ('PENDING','ACTIVE','SUSPENDED'))
);

CREATE INDEX idx_users_email  ON users(email);
CREATE INDEX idx_users_status ON users(status);

-- ==========================
-- ROLES
-- ==========================
CREATE TABLE roles (
    id          UUID         PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(255),
    CONSTRAINT uq_roles_name UNIQUE (name)
);

-- ==========================
-- USER_ROLES (many-to-many)
-- ==========================
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);

-- ==========================
-- CONSULTORIOS
-- ==========================
CREATE TABLE consultorios (
    id         UUID         PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    address    VARCHAR(500),
    phone      VARCHAR(30),
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_consultorios_status CHECK (status IN ('ACTIVE','INACTIVE'))
);

-- ==========================
-- MEMBERSHIPS
-- ==========================
CREATE TABLE memberships (
    id                  UUID        PRIMARY KEY,
    user_id             UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    consultorio_id      UUID        NOT NULL REFERENCES consultorios(id) ON DELETE CASCADE,
    role_in_consultorio VARCHAR(50) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_memberships_user_consultorio UNIQUE (user_id, consultorio_id),
    CONSTRAINT chk_memberships_role  CHECK (role_in_consultorio IN ('PROFESIONAL_ADMIN','PROFESIONAL','ADMINISTRATIVO')),
    CONSTRAINT chk_memberships_status CHECK (status IN ('ACTIVE','INACTIVE'))
);

CREATE INDEX idx_memberships_user_id        ON memberships(user_id);
CREATE INDEX idx_memberships_consultorio_id ON memberships(consultorio_id);

-- ==========================
-- REFRESH TOKENS
-- ==========================
CREATE TABLE refresh_tokens (
    id         UUID         PRIMARY KEY,
    user_id    UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    revoked    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_refresh_tokens_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_tokens_user_id    ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);

-- ==========================
-- ACTIVATION TOKENS
-- ==========================
CREATE TABLE activation_tokens (
    id         UUID         PRIMARY KEY,
    user_id    UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_activation_tokens_hash UNIQUE (token_hash)
);

CREATE INDEX idx_activation_tokens_user_id    ON activation_tokens(user_id);
CREATE INDEX idx_activation_tokens_token_hash ON activation_tokens(token_hash);
