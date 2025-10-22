#!/bin/bash

# Bank Entities API - Complete Application Startup Script
# This script orchestrates the startup of the entire application stack including:
# - PostgreSQL Database
# - Elasticsearch & Logstash for logging
# - Prometheus & Grafana for monitoring
# - Zipkin for distributed tracing
# - Bank Entities API application

set -e  # Exit on any error

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

# Function to check if Docker is running
check_docker() {
    print_message $BLUE "Checking Docker installation and status..."

    if ! command -v docker &> /dev/null; then
        print_message $RED "Docker is not installed. Please install Docker and try again."
        exit 1
    fi

    if ! docker info &> /dev/null; then
        print_message $RED "Docker is not running. Please start Docker and try again."
        exit 1
    fi

    print_message $GREEN "Docker is running."
}

# Function to check if docker-compose is available
check_docker_compose() {
    print_message $BLUE "Checking Docker Compose..."

    if command -v docker-compose &> /dev/null; then
        DOCKER_COMPOSE_CMD="docker-compose"
    elif docker compose version &> /dev/null; then
        DOCKER_COMPOSE_CMD="docker compose"
    else
        print_message $RED "Docker Compose is not available. Please install Docker Compose and try again."
        exit 1
    fi

    print_message $GREEN "Docker Compose is available: $DOCKER_COMPOSE_CMD"
}

# Function to reset database
reset_database() {
    print_message $YELLOW "Resetting PostgreSQL database..."

    # Stop postgres if running
    $DOCKER_COMPOSE_CMD stop postgres 2>/dev/null || true

    # Remove postgres volume
    docker volume rm bank-entities-api_postgres_data 2>/dev/null || true

    print_message $GREEN "Database volume reset. Initialization script will run on next start."
}

# Function to clean up existing containers and volumes
cleanup() {
    print_message $YELLOW "Cleaning up existing containers and volumes..."

    $DOCKER_COMPOSE_CMD down --volumes --remove-orphans 2>/dev/null || true
    docker system prune -f 2>/dev/null || true

    print_message $GREEN "Cleanup completed."
}

# Function to build the application
build_application() {
    print_message $BLUE "Building the Bank Entities API application..."

    if [ ! -f "pom.xml" ]; then
        print_message $RED "pom.xml not found. Please run this script from the project root directory."
        exit 1
    fi

    # Check if Maven is available
    if command -v mvn &> /dev/null; then
        print_message $BLUE "Building with Maven..."
        mvn clean package -DskipTests
    elif [ -f "./mvnw" ]; then
        print_message $BLUE "Building with Maven Wrapper..."
        chmod +x ./mvnw
        ./mvnw clean package -DskipTests
    else
        print_message $RED "Maven is not available. Please install Maven or use Maven Wrapper."
        exit 1
    fi

    print_message $GREEN "Application built successfully."
}

# Function to start the infrastructure services
start_infrastructure() {
    print_message $BLUE "Starting infrastructure services (PostgreSQL, Elasticsearch, Logstash, Prometheus, Grafana, Zipkin)..."

    # Start infrastructure services first
    $DOCKER_COMPOSE_CMD up -d postgres elasticsearch logstash kibana prometheus grafana zipkin

    print_message $YELLOW "Waiting for infrastructure services to be healthy..."

    # Wait for services to be healthy
    local max_attempts=30
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        # Check if all critical services are healthy
        local postgres_healthy=$($DOCKER_COMPOSE_CMD ps postgres | grep -c "healthy" || echo "0")
        local zipkin_healthy=$($DOCKER_COMPOSE_CMD ps zipkin | grep -c "healthy" || echo "0")
        local elasticsearch_healthy=$($DOCKER_COMPOSE_CMD ps elasticsearch | grep -c "healthy" || echo "0")
        local prometheus_healthy=$($DOCKER_COMPOSE_CMD ps prometheus | grep -c "healthy" || echo "0")

        if [ "$postgres_healthy" -eq 1 ] && [ "$zipkin_healthy" -eq 1 ] && [ "$elasticsearch_healthy" -eq 1 ] && [ "$prometheus_healthy" -eq 1 ]; then
            print_message $GREEN "All infrastructure services are healthy!"
            break
        fi

        print_message $YELLOW "Waiting for infrastructure services... (attempt $attempt/$max_attempts)"
        print_message $YELLOW "Status: PostgreSQL($postgres_healthy/1), Zipkin($zipkin_healthy/1), Elasticsearch($elasticsearch_healthy/1), Prometheus($prometheus_healthy/1)"
        sleep 10
        attempt=$((attempt + 1))
    done

    if [ $attempt -gt $max_attempts ]; then
        print_message $RED "Infrastructure services failed to start within the expected time."
        $DOCKER_COMPOSE_CMD logs
        exit 1
    fi

    print_message $GREEN "Infrastructure services are ready."
}

# Function to populate the database with sample data
populate_database() {
    print_message $BLUE "Populating database with sample data..."

    # Wait a bit more to ensure PostgreSQL is fully ready
    sleep 5

    # Run the database population script
    docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db << 'EOF'
INSERT INTO banks (swift_code, name, address, city, country, country_code, phone_number, email, website, bank_type, active, created_at, updated_at) VALUES
('SANDESMMXXX', 'Banco Santander', 'Paseo de la Castellana 83-85', 'Madrid', 'Spain', 'ES', '+34915123000', 'info@santander.es', 'https://www.santander.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BBVAESMM', 'Banco Bilbao Vizcaya Argentaria', 'Plaza de San Nicolás 4', 'Bilbao', 'Spain', 'ES', '+34944876000', 'info@bbva.es', 'https://www.bbva.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CAIXESBB', 'CaixaBank', 'Avenida Diagonal 621-629', 'Barcelona', 'Spain', 'ES', '+34935046000', 'info@caixabank.es', 'https://www.caixabank.es', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DEUTDEFF', 'Deutsche Bank', 'Taunusanlage 12', 'Frankfurt', 'Germany', 'DE', '+4969910000', 'info@db.com', 'https://www.db.com', 'INVESTMENT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('BNPAFRPP', 'BNP Paribas', '16 Boulevard des Italiens', 'Paris', 'France', 'FR', '+33142980000', 'info@bnpparibas.fr', 'https://www.bnpparibas.fr', 'COMMERCIAL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (swift_code) DO NOTHING;
EOF

    # Check if data was inserted successfully
    local bank_count=$(docker exec bank-entities-postgres psql -U bank_user -d bank_entities_db -t -c "SELECT COUNT(*) FROM banks;" | tr -d ' ')

    if [ "$bank_count" -gt 0 ]; then
        print_message $GREEN "Database populated with $bank_count banks."
    else
        print_message $YELLOW "Database population completed but no new records were added (may already exist)."
    fi
}

# Function to start the application
start_application() {
    print_message $BLUE "Starting Bank Entities API application..."

    $DOCKER_COMPOSE_CMD up -d bank-entities-api

    print_message $YELLOW "Waiting for Bank Entities API to be healthy..."

    local max_attempts=20
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        if curl -f http://localhost:8080/actuator/health &> /dev/null; then
            print_message $GREEN "Bank Entities API is healthy!"
            break
        fi

        print_message $YELLOW "Waiting for Bank Entities API... (attempt $attempt/$max_attempts)"
        sleep 10
        attempt=$((attempt + 1))
    done

    if [ $attempt -gt $max_attempts ]; then
        print_message $RED "Bank Entities API failed to start within the expected time."
        $DOCKER_COMPOSE_CMD logs bank-entities-api
        exit 1
    fi
}

# Function to display service URLs
display_service_urls() {
    print_message $GREEN "\n🎉 Bank Entities API is now running!"
    print_message $BLUE "\n📋 Service URLs:"
    echo "┌─────────────────────────────────────────────────────────────┐"
    echo "│ Service              │ URL                                   │"
    echo "├─────────────────────────────────────────────────────────────┤"
    echo "│ Bank Entities API    │ http://localhost:8080                 │"
    echo "│ API Documentation    │ http://localhost:8080/actuator        │"
    echo "│ Health Check         │ http://localhost:8080/actuator/health │"
    echo "│ Metrics (Prometheus) │ http://localhost:8080/actuator/prometheus │"
    echo "├─────────────────────────────────────────────────────────────┤"
    echo "│ PostgreSQL           │ localhost:5432                        │"
    echo "│ Prometheus           │ http://localhost:9090                 │"
    echo "│ Grafana              │ http://localhost:3000                 │"
    echo "│ Kibana (Logs)        │ http://localhost:5601                 │"
    echo "│ Zipkin (Tracing)     │ http://localhost:9411                 │"
    echo "└─────────────────────────────────────────────────────────────┘"

    print_message $YELLOW "\n🔐 Default Credentials:"
    echo "• Grafana: admin/admin"
    echo "• PostgreSQL: bank_user/bank_password"

    print_message $BLUE "\n📖 API Endpoints:"
    echo "• GET    /api/v1/banks           - Get all banks"
    echo "• POST   /api/v1/banks           - Create a new bank"
    echo "• GET    /api/v1/banks/{id}      - Get bank by ID"
    echo "• PUT    /api/v1/banks/{id}      - Update bank"
    echo "• DELETE /api/v1/banks/{id}      - Delete bank"
    echo "• GET    /api/v1/banks/swift/{swiftCode} - Get bank by SWIFT code"
    echo "• GET    /api/v1/banks/{id}/self-call    - Self-call to get bank by ID"
    echo "• GET    /api/v1/banks/swift/{swiftCode}/self-call - Self-call by SWIFT"

    print_message $GREEN "\n✅ All services are running successfully!"
    print_message $YELLOW "\nTo stop all services, run: ./stop-app.sh or $DOCKER_COMPOSE_CMD down"
    print_message $YELLOW "To view logs, run: $DOCKER_COMPOSE_CMD logs -f [service_name]"
}

# Function to run tests
run_tests() {
    print_message $BLUE "Running tests..."

    if command -v mvn &> /dev/null; then
        mvn test
    elif [ -f "./mvnw" ]; then
        ./mvnw test
    else
        print_message $YELLOW "Maven not available, skipping tests."
        return 0
    fi

    if [ $? -eq 0 ]; then
        print_message $GREEN "All tests passed!"
    else
        print_message $RED "Some tests failed. Check the output above."
        return 1
    fi
}

# Main execution
main() {
    print_message $GREEN "🚀 Starting Bank Entities API Complete Application Stack"
    print_message $BLUE "=================================================="

    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --clean)
                CLEAN=true
                shift
                ;;
            --skip-build)
                SKIP_BUILD=true
                shift
                ;;
            --skip-tests)
                SKIP_TESTS=true
                shift
                ;;
            --reset-db)
                RESET_DB=true
                shift
                ;;
            --help)
                echo "Usage: $0 [OPTIONS]"
                echo "Options:"
                echo "  --clean       Clean up existing containers and volumes before starting"
                echo "  --skip-build  Skip building the application"
                echo "  --skip-tests  Skip running tests"
                echo "  --reset-db    Reset PostgreSQL database (removes existing data)"
                echo "  --help        Show this help message"
                exit 0
                ;;
            *)
                print_message $RED "Unknown option: $1"
                print_message $YELLOW "Use --help for usage information"
                exit 1
                ;;
        esac
    done

    # Step 1: Check prerequisites
    check_docker
    check_docker_compose

    # Step 2: Cleanup if requested
    if [ "$CLEAN" = true ]; then
        cleanup
    fi

    # Step 2.5: Reset database if requested
    if [ "$RESET_DB" = true ]; then
        reset_database
    fi

    # Step 3: Build application
    if [ "$SKIP_BUILD" != true ]; then
        build_application
    fi

    # Step 4: Run tests
    if [ "$SKIP_TESTS" != true ]; then
        run_tests
    fi

    # Step 5: Start infrastructure services
    start_infrastructure

    # Step 5.5: Populate database with sample data
    populate_database

    # Step 6: Start the application
    start_application    # Step 7: Display information
    display_service_urls
}

# Run main function with all arguments
main "$@"

print_message $GREEN "\n🎯 Application stack is now running in the background!"
print_message $BLUE "Use './stop-app.sh' to shutdown all services."
