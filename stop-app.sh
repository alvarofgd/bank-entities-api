#!/bin/bash

# Bank Entities API - Application Shutdown Script
# This script gracefully shuts down the entire application stack including:
# - Bank Entities API application
# - PostgreSQL Database
# - Elasticsearch & Logstash for logging
# - Prometheus & Grafana for monitoring
# - Zipkin for distributed tracing

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

# Function to stop services gracefully
stop_services() {
    print_message $BLUE "Stopping Bank Entities API application stack..."
    
    # Stop application first
    print_message $YELLOW "Stopping Bank Entities API..."
    $DOCKER_COMPOSE_CMD stop bank-entities-api 2>/dev/null || true
    
    # Then stop infrastructure services
    print_message $YELLOW "Stopping infrastructure services..."
    $DOCKER_COMPOSE_CMD stop zipkin grafana prometheus kibana logstash elasticsearch postgres 2>/dev/null || true
    
    print_message $GREEN "All services stopped."
}

# Function to remove containers
remove_containers() {
    print_message $BLUE "Removing containers..."
    $DOCKER_COMPOSE_CMD down --remove-orphans 2>/dev/null || true
    print_message $GREEN "Containers removed."
}

# Function to remove volumes (optional)
remove_volumes() {
    print_message $YELLOW "Removing volumes (this will delete all data)..."
    $DOCKER_COMPOSE_CMD down --volumes --remove-orphans 2>/dev/null || true
    print_message $GREEN "Volumes removed."
}

# Function to clean up Docker system
cleanup_docker() {
    print_message $BLUE "Cleaning up Docker system..."
    docker system prune -f 2>/dev/null || true
    print_message $GREEN "Docker system cleaned up."
}

# Function to display status
show_status() {
    print_message $BLUE "\n📊 Current container status:"
    $DOCKER_COMPOSE_CMD ps 2>/dev/null || print_message $YELLOW "No containers running."
}

# Main execution
main() {
    print_message $GREEN "🛑 Shutting down Bank Entities API Application Stack"
    print_message $BLUE "====================================================="
    
    # Parse command line arguments
    REMOVE_VOLUMES=false
    CLEANUP_DOCKER=false
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --remove-volumes)
                REMOVE_VOLUMES=true
                shift
                ;;
            --cleanup)
                CLEANUP_DOCKER=true
                shift
                ;;
            --help)
                echo "Usage: $0 [OPTIONS]"
                echo "Options:"
                echo "  --remove-volumes  Remove all volumes (WARNING: This will delete all data!)"
                echo "  --cleanup         Clean up Docker system (remove unused images, networks, etc.)"
                echo "  --help            Show this help message"
                echo ""
                echo "Examples:"
                echo "  $0                    # Stop services and remove containers"
                echo "  $0 --remove-volumes  # Stop services, remove containers and volumes"
                echo "  $0 --cleanup         # Stop services, remove containers and clean Docker"
                exit 0
                ;;
            *)
                print_message $RED "Unknown option: $1"
                print_message $YELLOW "Use --help for usage information"
                exit 1
                ;;
        esac
    done
    
    # Check prerequisites
    check_docker_compose
    
    # Stop services gracefully
    stop_services
    
    # Remove containers
    remove_containers
    
    # Remove volumes if requested
    if [ "$REMOVE_VOLUMES" = true ]; then
        remove_volumes
    fi
    
    # Clean up Docker if requested
    if [ "$CLEANUP_DOCKER" = true ]; then
        cleanup_docker
    fi
    
    # Show final status
    show_status
    
    print_message $GREEN "\n✅ Bank Entities API application stack has been shut down!"
    
    if [ "$REMOVE_VOLUMES" = true ]; then
        print_message $YELLOW "⚠️  All data has been removed (volumes deleted)."
    else
        print_message $BLUE "💾 Data has been preserved (volumes kept)."
        print_message $YELLOW "    Use '$0 --remove-volumes' to remove all data."
    fi
    
    print_message $BLUE "\n🚀 To start the application again, run: ./start-app.sh"
}

# Run main function with all arguments
main "$@"