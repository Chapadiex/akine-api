-- Convenios nuevo stack (persistencia JPA paquete entity/convenio)
-- Necesario para H2 y Postgres cuando el módulo nuevo está activo.

CREATE TABLE IF NOT EXISTS prestacion (
    id UUID PRIMARY KEY,
    codigo_nomenclador VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    modalidad VARCHAR(50) NOT NULL,
    es_modulo BOOLEAN NOT NULL DEFAULT FALSE,
    codigos_incluidos VARCHAR(500),
    requiere_aut_base BOOLEAN NOT NULL DEFAULT FALSE,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS convenio (
    id UUID PRIMARY KEY,
    consultorio_id UUID NOT NULL,
    financiador_id UUID NOT NULL,
    plan VARCHAR(100),
    sigla_display VARCHAR(150),
    modalidad VARCHAR(50) NOT NULL,
    dia_cierre INTEGER,
    requiere_aut BOOLEAN NOT NULL DEFAULT FALSE,
    requiere_orden BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_convenio_financiador
        FOREIGN KEY (financiador_id) REFERENCES coverage_financiador_salud(id)
);

CREATE TABLE IF NOT EXISTS convenio_version (
    id UUID PRIMARY KEY,
    convenio_id UUID NOT NULL,
    version_num INTEGER NOT NULL,
    vigencia_desde DATE NOT NULL,
    vigencia_hasta DATE,
    estado VARCHAR(50) NOT NULL,
    motivo_cierre VARCHAR(500),
    creado_por UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_convenio_version_convenio
        FOREIGN KEY (convenio_id) REFERENCES convenio(id)
);

CREATE TABLE IF NOT EXISTS arancel (
    id UUID PRIMARY KEY,
    convenio_version_id UUID NOT NULL,
    prestacion_id UUID NOT NULL,
    prestacion_codigo VARCHAR(50),
    prestacion_nombre VARCHAR(255),
    importe_os DECIMAL(12,2) NOT NULL,
    coseguro_tipo VARCHAR(30) NOT NULL,
    coseguro_valor DECIMAL(10,2),
    importe_total DECIMAL(12,2),
    sesiones_mes_max INTEGER,
    sesiones_anio_max INTEGER,
    requiere_aut_override BOOLEAN,
    vigencia_desde DATE NOT NULL,
    vigencia_hasta DATE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_arancel_convenio_version
        FOREIGN KEY (convenio_version_id) REFERENCES convenio_version(id),
    CONSTRAINT fk_arancel_prestacion
        FOREIGN KEY (prestacion_id) REFERENCES prestacion(id)
);

CREATE INDEX IF NOT EXISTS idx_prestacion_codigo ON prestacion(codigo_nomenclador);
CREATE INDEX IF NOT EXISTS idx_convenio_consultorio ON convenio(consultorio_id);
CREATE INDEX IF NOT EXISTS idx_convenio_financiador ON convenio(financiador_id);
CREATE INDEX IF NOT EXISTS idx_convenio_version_convenio ON convenio_version(convenio_id);
CREATE INDEX IF NOT EXISTS idx_arancel_version ON arancel(convenio_version_id);
CREATE INDEX IF NOT EXISTS idx_arancel_prestacion ON arancel(prestacion_id);
