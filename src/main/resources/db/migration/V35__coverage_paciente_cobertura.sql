CREATE TABLE IF NOT EXISTS coverage_paciente_cobertura (
    id UUID PRIMARY KEY,
    paciente_id UUID NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,
    financiador_id UUID NOT NULL REFERENCES coverage_financiador_salud(id),
    plan_id UUID REFERENCES coverage_plan_financiador(id),
    numero_afiliado VARCHAR(100),
    fecha_alta DATE,
    fecha_baja DATE,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_coverage_paciente_cobertura_paciente ON coverage_paciente_cobertura(paciente_id);
CREATE INDEX IF NOT EXISTS idx_coverage_paciente_cobertura_financiador ON coverage_paciente_cobertura(financiador_id);
CREATE INDEX IF NOT EXISTS idx_coverage_paciente_cobertura_activo ON coverage_paciente_cobertura(activo);
