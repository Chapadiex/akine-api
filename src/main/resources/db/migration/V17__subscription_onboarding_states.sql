-- Subscription onboarding states and wizard metadata

ALTER TABLE suscripciones
    DROP CONSTRAINT IF EXISTS chk_suscripciones_status;

ALTER TABLE suscripciones
    ADD CONSTRAINT chk_suscripciones_status
        CHECK (status IN (
            'DRAFT',
            'EMAIL_PENDING',
            'PAYMENT_PENDING',
            'SETUP_PENDING',
            'PENDING_APPROVAL',
            'ACTIVE',
            'REJECTED',
            'EXPIRED',
            'SUSPENDED'
        ));

ALTER TABLE suscripciones
    ADD COLUMN IF NOT EXISTS plan_code VARCHAR(40);

ALTER TABLE suscripciones
    ADD COLUMN IF NOT EXISTS billing_cycle VARCHAR(20);

ALTER TABLE suscripciones
    ADD COLUMN IF NOT EXISTS onboarding_step VARCHAR(40);

ALTER TABLE suscripciones
    ADD COLUMN IF NOT EXISTS payment_reference VARCHAR(120);

ALTER TABLE suscripciones
    ADD COLUMN IF NOT EXISTS tracking_token VARCHAR(120);

ALTER TABLE suscripciones
    ADD COLUMN IF NOT EXISTS submitted_for_approval_at TIMESTAMP;

UPDATE suscripciones
   SET status = 'PENDING_APPROVAL',
       onboarding_step = COALESCE(onboarding_step, 'REVIEW')
 WHERE status = 'PENDING';

UPDATE suscripciones
   SET plan_code = COALESCE(plan_code, 'STARTER'),
       billing_cycle = COALESCE(billing_cycle, 'TRIAL'),
       tracking_token = COALESCE(tracking_token, CAST(id AS VARCHAR(120)));

CREATE INDEX IF NOT EXISTS idx_suscripciones_tracking_token
    ON suscripciones (tracking_token);

CREATE INDEX IF NOT EXISTS idx_suscripciones_status_requested_at
    ON suscripciones (status, requested_at DESC);

CREATE INDEX IF NOT EXISTS idx_suscripciones_owner_user_status
    ON suscripciones (owner_user_id, status);
