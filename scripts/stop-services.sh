#!/bin/bash

# PDF Generator Platform - Stop Services Script
# This script stops all services for the PDF Generator Platform

set -e

echo "🛑 Stopping PDF Generator Platform Services..."

# Stop Docker Compose services
echo "📦 Stopping infrastructure services..."
docker-compose down

echo "✅ All services stopped successfully!"
echo ""
echo "💡 To remove all data volumes, run: docker-compose down -v"
echo "💡 To remove all images, run: docker system prune -a"