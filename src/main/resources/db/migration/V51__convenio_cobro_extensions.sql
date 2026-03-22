-- Fase 0: Extender convenios para soporte completo de liquidación
-- Agrega campos requeridos por operatoria de cobro (AKINE_operatoria_cobro.md)
-- Nota: H2 requiere ALTER TABLE separados por columna

-- billing_convenio_financiador: scope por consultorio y plan específico
ALTER TABLE billing_convenio_financiador ADD COLUMN consultorio_id UUID REFERENCES consultorios(id);
ALTER TABLE billing_convenio_financiador ADD COLUMN plan_id UUID REFERENCES coverage_plan_financiador(id);
ALTER TABLE billing_convenio_financiador ADD COLUMN requiere_autorizacion BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE billing_convenio_financiador ADD COLUMN requiere_orden BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE billing_convenio_financiador ADD COLUMN cantidad_sesiones_autorizadas INTEGER;
ALTER TABLE billing_convenio_financiador ADD COLUMN modo_facturacion VARCHAR(30) NOT NULL DEFAULT 'MENSUAL';

CREATE INDEX IF NOT EXISTS idx_convenio_consultorio ON billing_convenio_financiador(consultorio_id);
CREATE INDEX IF NOT EXISTS idx_convenio_financiador_plan ON billing_convenio_financiador(financiador_id, plan_id);

-- billing_convenio_prestacion_valor: campos para cálculo de copago y tope
ALTER TABLE billing_convenio_prestacion_valor ADD COLUMN copago_porcentaje DECIMAL(5,2);
ALTER TABLE billing_convenio_prestacion_valor ADD COLUMN coseguro_importe DECIMAL(19,4);
ALTER TABLE billing_convenio_prestacion_valor ADD COLUMN tope_cobertura DECIMAL(19,4);
