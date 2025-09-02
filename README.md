# PDF Generator Platform

A comprehensive web application for creating dynamic PDF templates and generating PDFs through REST APIs.

## Project Structure

```
pdf-generator-platform/
├── frontend/                 # React TypeScript application
├── backend/                  # Spring Boot Java application
├── docker/                   # Docker configurations
├── docs/                     # Documentation
├── .kiro/                    # Kiro specifications
└── docker-compose.yml        # Development environment
```

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Node.js 18+ (for frontend development)
- Java 17+ (for backend development)
- Maven 3.8+ (for backend build)

### Development Setup

1. Clone the repository:
```bash
git clone https://github.com/metinkuzey/pdf_generator_platform.git
cd pdf_generator_platform
```

2. Start development environment:
```bash
docker-compose up -d
```

3. Start frontend development server:
```bash
cd frontend
npm install
npm start
```

4. Start backend development server:
```bash
cd backend
mvn spring-boot:run
```

## Services

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **PostgreSQL**: localhost:5432
- **Redis**: localhost:6379
- **RabbitMQ Management**: http://localhost:15672
- **MinIO Console**: http://localhost:9001

## Documentation

- [Requirements](.kiro/specs/pdf-generator-platform/requirements.md)
- [Design](.kiro/specs/pdf-generator-platform/design.md)
- [Implementation Tasks](.kiro/specs/pdf-generator-platform/tasks.md)