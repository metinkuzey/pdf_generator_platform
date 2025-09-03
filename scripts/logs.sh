#!/bin/bash

# PDF Generator Platform - Logs Script
# This script shows logs from various services

SERVICE=${1:-all}

case $SERVICE in
    "postgres"|"db")
        echo "📋 PostgreSQL Logs:"
        docker logs -f pdf-postgres
        ;;
    "redis")
        echo "📋 Redis Logs:"
        docker logs -f pdf-redis
        ;;
    "rabbitmq"|"mq")
        echo "📋 RabbitMQ Logs:"
        docker logs -f pdf-rabbitmq
        ;;
    "minio")
        echo "📋 MinIO Logs:"
        docker logs -f pdf-minio
        ;;
    "backend"|"app")
        echo "📋 Backend Application Logs:"
        if [ -f "backend/logs/pdf-generator.log" ]; then
            tail -f backend/logs/pdf-generator.log
        else
            echo "❌ Backend log file not found. Is the application running?"
        fi
        ;;
    "all")
        echo "📋 All Service Logs:"
        docker-compose logs -f
        ;;
    *)
        echo "Usage: $0 [postgres|redis|rabbitmq|minio|backend|all]"
        echo ""
        echo "Available services:"
        echo "  postgres  - PostgreSQL database logs"
        echo "  redis     - Redis cache logs"
        echo "  rabbitmq  - RabbitMQ message queue logs"
        echo "  minio     - MinIO object storage logs"
        echo "  backend   - Spring Boot application logs"
        echo "  all       - All Docker Compose service logs"
        ;;
esac