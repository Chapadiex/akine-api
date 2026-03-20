CREATE TABLE IF NOT EXISTS billing_prestacion_arancelable (
    id UUID PRIMARY KEY,
    codigo_interno VARCHAR(50) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    unidad_facturacion VARCHAR(50) NOT NULL, -- SESION, PRACTICA, MODULO
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT uq_billing_prestacion_codigo UNIQUE (codigo_interno)
);

CREATE INDEX IF NOT EXISTS idx_billing_prestacion_activo ON billing_prestacion_arancelable(activo);
