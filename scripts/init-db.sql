-- Initialize the Bank Entities database

-- Create the banks table with proper indexes
CREATE TABLE IF NOT EXISTS banks (
    id BIGSERIAL PRIMARY KEY,
    swift_code VARCHAR(11) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    city VARCHAR(100),
    country VARCHAR(100),
    country_code VARCHAR(2),
    phone_number VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(255),
    bank_type VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_banks_swift_code ON banks(swift_code);
CREATE INDEX IF NOT EXISTS idx_banks_country ON banks(country);
CREATE INDEX IF NOT EXISTS idx_banks_country_code ON banks(country_code);
CREATE INDEX IF NOT EXISTS idx_banks_active ON banks(active);
CREATE INDEX IF NOT EXISTS idx_banks_name ON banks(name);

-- Insert some sample data (with explicit timestamps since JPA doesn't set defaults)
INSERT INTO banks (swift_code, name, address, city, country, country_code, phone_number, email, website, bank_type, active, created_at, updated_at) VALUES
('SANDESMMXXX', 'Banco Santander', 'Paseo de la Castellana 83-85', 'Madrid', 'Spain', 'ES', '+34915123000', 'info@santander.es', 'https://www.santander.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BBVAESMM', 'Banco Bilbao Vizcaya Argentaria', 'Plaza de San Nicolás 4', 'Bilbao', 'Spain', 'ES', '+34944876000', 'info@bbva.es', 'https://www.bbva.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CAIXESBB', 'CaixaBank', 'Avenida Diagonal 621-629', 'Barcelona', 'Spain', 'ES', '+34935046000', 'info@caixabank.es', 'https://www.caixabank.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DEUTDEFF', 'Deutsche Bank', 'Taunusanlage 12', 'Frankfurt', 'Germany', 'DE', '+4969910000', 'info@db.com', 'https://www.db.com', 'INVESTMENT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BNPAFRPP', 'BNP Paribas', '16 Boulevard des Italiens', 'Paris', 'France', 'FR', '+33142980000', 'info@bnpparibas.fr', 'https://www.bnpparibas.fr', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (swift_code) DO NOTHING;