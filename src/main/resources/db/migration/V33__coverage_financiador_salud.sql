CREATE TABLE IF NOT EXISTS coverage_financiador_salud (
    id UUID PRIMARY KEY,
    codigo_externo VARCHAR(50),
    tipo_financiador VARCHAR(50) NOT NULL,
    subtipo_financiador VARCHAR(50),
    nombre VARCHAR(255) NOT NULL,
    nombre_corto VARCHAR(100),
    ambito_cobertura VARCHAR(50),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT uq_coverage_financiador_nombre UNIQUE (nombre)
);

CREATE INDEX IF NOT EXISTS idx_coverage_financiador_tipo ON coverage_financiador_salud(tipo_financiador);
CREATE INDEX IF NOT EXISTS idx_coverage_financiador_activo ON coverage_financiador_salud(activo);
