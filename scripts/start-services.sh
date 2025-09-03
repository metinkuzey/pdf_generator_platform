#!/bin/bash

# PDF Generator Platform - Start Services Script
# This script starts all required services for the PDF Generator Platform

set -e

echo "🚀 Starting PDF Generator Platform Services..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Start infrastructure services
echo "📦 Starting infrastructure services (PostgreSQL, Redis, RabbitMQ, MinIO)..."
docker-compose up -d postgres redis rabbitmq minio minio-client

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check PostgreSQL
echo "🔍 Checking PostgreSQL connection..."
until docker exec pdf-postgres pg_isready -U pdf_user -d pdf_generator > /dev/null 2>&1; do
    echo "Waiting for PostgreSQL..."
    sleep 2
done
echo "✅ PostgreSQL is ready"

# Check Redis
echo "🔍 Checking Redis connection..."
until docker exec pdf-redis redis-cli ping > /dev/null 2>&1; do
    echo "Waiting for Redis..."
    sleep 2
done
echo "✅ Redis is ready"

# Check RabbitMQ
echo "🔍 Checking RabbitMQ connection..."
until docker exec pdf-rabbitmq rabbitmqctl status > /dev/null 2>&1; do
    echo "Waiting for RabbitMQ..."
    sleep 2
done
echo "✅ RabbitMQ is ready"

# Check MinIO
echo "🔍 Checking MinIO connection..."
until curl -f http://localhost:9000/minio/health/live > /dev/null 2>&1; do
    echo "Waiting for MinIO..."
    sleep 2
done
echo "✅ MinIO is ready"

echo ""
echo "🎉 All services are ready!"
echo ""
echo "📋 Service URLs:"
echo "   PostgreSQL: localhost:5432 (user: pdf_user, db: pdf_generator)"
echo "   Redis: localhost:6379"
echo "   RabbitMQ Management: http://localhost:15672 (user: pdf_user, pass: pdf_password)"
echo "   MinIO Console: http://localhost:9001 (user: pdf_user, pass: pdf_password)"
echo ""
echo "🔧 Next steps:"
echo "   1. Run './scripts/start-backend.sh' to start the Spring Boot application"
echo "   2. Run './scripts/start-frontend.sh' to start the React application"
echo ""