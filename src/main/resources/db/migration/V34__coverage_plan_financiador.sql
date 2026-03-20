CREATE TABLE IF NOT EXISTS coverage_plan_financiador (
    id UUID PRIMARY KEY,
    financiador_id UUID NOT NULL REFERENCES coverage_financiador_salud(id) ON DELETE CASCADE,
    nombre_plan VARCHAR(255) NOT NULL,
    tipo_plan VARCHAR(50) NOT NULL,
    requiere_autorizacion_default BOOLEAN NOT NULL DEFAULT FALSE,
    vigencia_desde DATE,
    vigencia_hasta DATE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT uq_coverage_plan_financiador_nombre UNIQUE (financiador_id, nombre_plan)
);

CREATE INDEX IF NOT EXISTS idx_coverage_plan_financiador ON coverage_plan_financiador(financiador_id);
CREATE INDEX IF NOT EXISTS idx_coverage_plan_activo ON coverage_plan_financiador(activo);
