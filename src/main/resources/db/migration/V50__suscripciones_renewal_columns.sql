-- Phase 4: Ciclo de vida de renovación
-- Agrega columnas para gestionar renovaciones y período de gracia

ALTER TABLE suscripciones ADD COLUMN next_renewal_date DATE;
ALTER TABLE suscripciones ADD COLUMN grace_period_end DATE;
