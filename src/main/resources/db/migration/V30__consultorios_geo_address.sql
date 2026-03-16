ALTER TABLE consultorios
    ADD COLUMN IF NOT EXISTS geo_address VARCHAR(500);
