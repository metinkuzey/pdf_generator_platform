# PDF Generator Platform Makefile

.PHONY: help setup start stop clean build test lint

# Default target
help:
	@echo "PDF Generator Platform - Available commands:"
	@echo "  setup     - Setup development environment"
	@echo "  start     - Start all services"
	@echo "  stop      - Stop all services"
	@echo "  clean     - Clean up containers and volumes"
	@echo "  build     - Build all services"
	@echo "  test      - Run all tests"
	@echo "  lint      - Run linting"

# Setup development environment
setup:
	@echo "Setting up development environment..."
	docker-compose up -d postgres redis rabbitmq minio minio-client
	@echo "Waiting for services to be ready..."
	sleep 10
	@echo "Installing frontend dependencies..."
	cd frontend && npm install
	@echo "Setup complete!"

# Start all services
start:
	@echo "Starting all services..."
	docker-compose up -d
	@echo "Services started. Access points:"
	@echo "  - Frontend: http://localhost:3000"
	@echo "  - Backend API: http://localhost:8080"
	@echo "  - PostgreSQL: localhost:5432"
	@echo "  - Redis: localhost:6379"
	@echo "  - RabbitMQ Management: http://localhost:15672"
	@echo "  - MinIO Console: http://localhost:9001"

# Stop all services
stop:
	@echo "Stopping all services..."
	docker-compose down

# Clean up containers and volumes
clean:
	@echo "Cleaning up containers and volumes..."
	docker-compose down -v
	docker system prune -f

# Build all services
build:
	@echo "Building backend..."
	cd backend && mvn clean package -DskipTests
	@echo "Building frontend..."
	cd frontend && npm run build

# Run all tests
test:
	@echo "Running backend tests..."
	cd backend && mvn test
	@echo "Running frontend tests..."
	cd frontend && npm test -- --coverage --watchAll=false

# Run linting
lint:
	@echo "Running backend linting..."
	cd backend && mvn checkstyle:check
	@echo "Running frontend linting..."
	cd frontend && npm run lint

# Development shortcuts
dev-backend:
	@echo "Starting backend in development mode..."
	cd backend && mvn spring-boot:run

dev-frontend:
	@echo "Starting frontend in development mode..."
	cd frontend && npm start

# Database operations
db-reset:
	@echo "Resetting database..."
	docker-compose stop postgres
	docker-compose rm -f postgres
	docker volume rm pdf_generator_platform_postgres_data
	docker-compose up -d postgres
	sleep 5
	@echo "Database reset complete!"

# Logs
logs:
	docker-compose logs -f

logs-backend:
	docker-compose logs -f backend

logs-frontend:
	docker-compose logs -f frontend