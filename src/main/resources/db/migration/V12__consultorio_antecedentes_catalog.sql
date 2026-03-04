CREATE TABLE IF NOT EXISTS consultorio_antecedente_catalog (
    consultorio_id UUID PRIMARY KEY,
    version VARCHAR(20) NOT NULL,
    catalog_json TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(120) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    updated_by VARCHAR(120) NOT NULL
);

