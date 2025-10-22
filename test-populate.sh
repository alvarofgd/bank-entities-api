#!/bin/bash

# Test script to verify the populate_database function from start-app.sh

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Function to populate the database with sample data (copied from start-app.sh)
populate_database() {
    print_message $BLUE "Populating database with sample data..."

    # Wait a bit more to ensure PostgreSQL is fully ready
    sleep 2

    # Run the database population using direct SQL execution
    docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -c "
INSERT INTO banks (swift_code, name, address, city, country, country_code, phone_number, email, website, bank_type, active, created_at, updated_at) VALUES
('SANDESMMXXX', 'Banco Santander', 'Paseo de la Castellana 83-85', 'Madrid', 'Spain', 'ES', '+34915123000', 'info@santander.es', 'https://www.santander.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BBVAESMM', 'Banco Bilbao Vizcaya Argentaria', 'Plaza de San Nicolás 4', 'Bilbao', 'Spain', 'ES', '+34944876000', 'info@bbva.es', 'https://www.bbva.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CAIXESBB', 'CaixaBank', 'Avenida Diagonal 621-629', 'Barcelona', 'Spain', 'ES', '+34935046000', 'info@caixabank.es', 'https://www.caixabank.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DEUTDEFF', 'Deutsche Bank', 'Taunusanlage 12', 'Frankfurt', 'Germany', 'DE', '+4969910000', 'info@db.com', 'https://www.db.com', 'INVESTMENT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BNPAFRPP', 'BNP Paribas', '16 Boulevard des Italiens', 'Paris', 'France', 'FR', '+33142980000', 'info@bnpparibas.fr', 'https://www.bnpparibas.fr', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (swift_code) DO NOTHING;"

    # Check if data was inserted successfully
    local bank_count=$(docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -t -c "SELECT COUNT(*) FROM banks;" | tr -d ' ')

    if [ "$bank_count" -gt 0 ]; then
        print_message $GREEN "Database populated with $bank_count banks."
    else
        print_message $YELLOW "Database population completed but no new records were added (may already exist)."
    fi
}

# Clear database first
print_message $YELLOW "Clearing existing data..."
docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -c "DELETE FROM banks;"

# Test the function
populate_database

# Final verification
FINAL_COUNT=$(docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -t -c "SELECT COUNT(*) FROM banks;" | tr -d ' ')
print_message $BLUE "Final verification: $FINAL_COUNT banks in database"

if [ "$FINAL_COUNT" -eq 5 ]; then
    print_message $GREEN "✅ start-app.sh populate function is working correctly!"
else
    print_message $RED "❌ start-app.sh populate function failed!"
fi
