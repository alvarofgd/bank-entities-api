#!/bin/bash

# Script to populate the banks database with sample data
# This can be run multiple times safely due to ON CONFLICT clause

echo "Populating banks database with sample data..."

# Test database connection first
echo "Testing database connection..."
if ! docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -c "SELECT 1;" > /dev/null 2>&1; then
    echo "ERROR: Cannot connect to database. Make sure PostgreSQL container is running."
    exit 1
fi
echo "Database connection successful."

# Check if banks table exists
echo "Checking if banks table exists..."
TABLE_EXISTS=$(docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -t -c "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'banks');" | tr -d ' ')
if [ "$TABLE_EXISTS" != "t" ]; then
    echo "ERROR: banks table does not exist. Please ensure the application has created the schema."
    exit 1
fi
echo "Banks table exists."

# Show current table structure
echo "Current table structure:"
docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -c "\d banks"

echo "Inserting sample data..."

# Insert the data using direct SQL execution instead of heredoc
docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -c "
INSERT INTO banks (swift_code, name, address, city, country, country_code, phone_number, email, website, bank_type, active, created_at, updated_at) VALUES
('SANDESMMXXX', 'Banco Santander', 'Paseo de la Castellana 83-85', 'Madrid', 'Spain', 'ES', '+34915123000', 'info@santander.es', 'https://www.santander.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BBVAESMM', 'Banco Bilbao Vizcaya Argentaria', 'Plaza de San Nicolás 4', 'Bilbao', 'Spain', 'ES', '+34944876000', 'info@bbva.es', 'https://www.bbva.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CAIXESBB', 'CaixaBank', 'Avenida Diagonal 621-629', 'Barcelona', 'Spain', 'ES', '+34935046000', 'info@caixabank.es', 'https://www.caixabank.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DEUTDEFF', 'Deutsche Bank', 'Taunusanlage 12', 'Frankfurt', 'Germany', 'DE', '+4969910000', 'info@db.com', 'https://www.db.com', 'INVESTMENT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BNPAFRPP', 'BNP Paribas', '16 Boulevard des Italiens', 'Paris', 'France', 'FR', '+33142980000', 'info@bnpparibas.fr', 'https://www.bnpparibas.fr', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (swift_code) DO NOTHING;"

echo "Showing current data..."
docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -c "SELECT COUNT(*) as \"Total Banks\" FROM banks;"
docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -c "SELECT name, swift_code, country FROM banks ORDER BY name;"

# Check final result
FINAL_COUNT=$(docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -t -c "SELECT COUNT(*) FROM banks;" | tr -d ' ')
echo "Final bank count: $FINAL_COUNT"

if [ "$FINAL_COUNT" -gt 0 ]; then
    echo "✅ Database population completed successfully!"
else
    echo "❌ Database population failed - no records were inserted."
    exit 1
fi
