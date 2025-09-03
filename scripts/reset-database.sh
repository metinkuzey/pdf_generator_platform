#!/bin/bash

# PDF Generator Platform - Reset Database Script
# This script resets the database by recreating the PostgreSQL container

set -e

echo "🔄 Resetting PDF Generator Database..."

# Stop PostgreSQL container
echo "🛑 Stopping PostgreSQL container..."
docker-compose stop postgres

# Remove PostgreSQL container and volume
echo "🗑️ Removing PostgreSQL container and data..."
docker-compose rm -f postgres
docker volume rm pdf-template-creator_postgres_data 2>/dev/null || true

# Start PostgreSQL again
echo "🚀 Starting fresh PostgreSQL container..."
docker-compose up -d postgres

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 10

until docker exec pdf-postgres pg_isready -U pdf_user -d pdf_generator > /dev/null 2>&1; do
    echo "Waiting for PostgreSQL..."
    sleep 2
done

echo "✅ Database reset completed!"
echo ""
echo "📋 Database connection details:"
echo "   Host: localhost:5432"
echo "   Database: pdf_generator"
echo "   Username: pdf_user"
echo "   Password: pdf_password"