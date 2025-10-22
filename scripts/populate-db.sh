#!/bin/bash

# Script to populate the banks database with sample data
# This can be run multiple times safely due to ON CONFLICT clause

echo "Populating banks database with sample data..."

docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db << 'EOF'
-- Insert sample banks data (using ON CONFLICT to avoid duplicates)
INSERT INTO banks (swift_code, name, address, city, country, country_code, phone_number, email, website, bank_type, active, created_at, updated_at) VALUES
('SANDESMMXXX', 'Banco Santander', 'Paseo de la Castellana 83-85', 'Madrid', 'Spain', 'ES', '+34915123000', 'info@santander.es', 'https://www.santander.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BBVAESMM', 'Banco Bilbao Vizcaya Argentaria', 'Plaza de San Nicolás 4', 'Bilbao', 'Spain', 'ES', '+34944876000', 'info@bbva.es', 'https://www.bbva.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CAIXESBB', 'CaixaBank', 'Avenida Diagonal 621-629', 'Barcelona', 'Spain', 'ES', '+34935046000', 'info@caixabank.es', 'https://www.caixabank.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DEUTDEFF', 'Deutsche Bank', 'Taunusanlage 12', 'Frankfurt', 'Germany', 'DE', '+4969910000', 'info@db.com', 'https://www.db.com', 'INVESTMENT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BNPAFRPP', 'BNP Paribas', '16 Boulevard des Italiens', 'Paris', 'France', 'FR', '+33142980000', 'info@bnpparibas.fr', 'https://www.bnpparibas.fr', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (swift_code) DO NOTHING;

-- Show the results
SELECT COUNT(*) as "Total Banks" FROM banks;
SELECT name, swift_code, country FROM banks ORDER BY name;
EOF

echo "Database population completed!"
