CREATE TABLE IF NOT EXISTS billing_convenio_actualizacion (
    id UUID PRIMARY KEY,
    convenio_id UUID NOT NULL REFERENCES billing_convenio_financiador(id) ON DELETE CASCADE,
    tipo_actualizacion VARCHAR(50) NOT NULL, -- MASIVA, INDIVIDUAL
    fuente_actualizacion VARCHAR(100),
    fecha_aplicacion DATE NOT NULL,
    observaciones TEXT,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_billing_actualizacion_convenio ON billing_convenio_actualizacion(convenio_id);
