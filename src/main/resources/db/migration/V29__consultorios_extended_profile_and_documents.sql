ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS description VARCHAR(500);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS logo_url VARCHAR(500);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS legal_name VARCHAR(255);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS administrative_contact VARCHAR(255);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS internal_notes VARCHAR(1000);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS access_reference VARCHAR(255);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS floor_unit VARCHAR(120);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS document_display_name VARCHAR(255);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS document_subtitle VARCHAR(255);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS document_logo_url VARCHAR(500);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS document_footer VARCHAR(1000);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS document_show_address BOOLEAN;

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS document_show_phone BOOLEAN;

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS document_show_email BOOLEAN;

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS document_show_cuit BOOLEAN;

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS document_show_legal_name BOOLEAN;

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS document_show_logo BOOLEAN;

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS license_number VARCHAR(120);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS license_type VARCHAR(120);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS license_expiration_date DATE;

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS professional_director_name VARCHAR(255);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS professional_director_license VARCHAR(120);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS legal_document_summary VARCHAR(1000);

ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS legal_notes VARCHAR(1000);
