#!/bin/bash

# PDF Generator Platform - Start Backend Script
# This script starts the Spring Boot backend application

set -e

echo "🚀 Starting PDF Generator Backend..."

# Check if services are running
echo "🔍 Checking if infrastructure services are running..."

if ! docker ps | grep -q pdf-postgres; then
    echo "❌ PostgreSQL is not running. Please run './scripts/start-services.sh' first."
    exit 1
fi

if ! docker ps | grep -q pdf-redis; then
    echo "❌ Redis is not running. Please run './scripts/start-services.sh' first."
    exit 1
fi

# Navigate to backend directory
cd backend

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

# Clean and compile
echo "🔧 Cleaning and compiling the project..."
mvn clean compile

# Run tests
echo "🧪 Running tests..."
mvn test

# Start the application
echo "🚀 Starting Spring Boot application..."
echo ""
echo "📋 Application will be available at:"
echo "   Backend API: http://localhost:8080/api"
echo "   Swagger UI: http://localhost:8080/api/swagger-ui.html"
echo "   Health Check: http://localhost:8080/api/actuator/health"
echo ""

mvn spring-boot:run