# PDF Generator Platform

A comprehensive PDF generation platform that allows users to create dynamic PDF templates and generate PDFs with data integration.

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 17+
- Maven 3.6+
- Node.js 16+ & npm

### 1. Start Infrastructure Services
```bash
./scripts/start-services.sh
```

### 2. Start Backend Application
```bash
./scripts/start-backend.sh
```

### 3. Start Frontend Application (Optional)
```bash
./scripts/start-frontend.sh
```

### 4. Test API Endpoints
```bash
./scripts/test-api.sh
```

## 📋 Available Services

| Service | URL | Credentials |
|---------|-----|-------------|
| Backend API | http://localhost:8080/api | - |
| Swagger UI | http://localhost:8080/api/swagger-ui.html | - |
| PostgreSQL | localhost:5432 | user: pdf_user, pass: pdf_password |
| Redis | localhost:6379 | - |
| RabbitMQ Management | http://localhost:15672 | user: pdf_user, pass: pdf_password |
| MinIO Console | http://localhost:9001 | user: pdf_user, pass: pdf_password |

## 🔧 Development Scripts

| Script | Description |
|--------|-------------|
| `./scripts/start-services.sh` | Start all infrastructure services |
| `./scripts/start-backend.sh` | Start Spring Boot backend |
| `./scripts/start-frontend.sh` | Start React frontend |
| `./scripts/stop-services.sh` | Stop all services |
| `./scripts/reset-database.sh` | Reset database |
| `./scripts/test-api.sh` | Test API endpoints |
| `./scripts/logs.sh [service]` | View service logs |

## 📡 API Endpoints

### Templates
- `GET /api/templates` - List all templates
- `POST /api/templates` - Create new template
- `GET /api/templates/{id}` - Get template by ID
- `PUT /api/templates/{id}` - Update template
- `DELETE /api/templates/{id}` - Delete template

### PDF Generation
- `POST /api/pdf/generate/{templateId}` - Generate PDF from template
- `POST /api/pdf/preview/{templateId}` - Generate PDF preview

### Health & Monitoring
- `GET /api/actuator/health` - Health check
- `GET /api/actuator/metrics` - Application metrics

## 🧪 Testing with Postman/cURL

### Create a Template
```bash
curl -X POST http://localhost:8080/api/templates \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sample Invoice",
    "category": "CREDIT_CARD_STATEMENT",
    "schema": {
      "layout": {
        "pageSize": "A4",
        "margins": {"top": 20, "right": 20, "bottom": 20, "left": 20}
      },
      "elements": [
        {
          "type": "TEXT",
          "properties": {
            "text": "Invoice for {{customer_name}}",
            "fontSize": 18,
            "fontWeight": "bold",
            "textAlign": "center"
          }
        }
      ]
    }
  }'
```

### Generate PDF
```bash
curl -X POST http://localhost:8080/api/pdf/generate/{templateId} \
  -H "Content-Type: application/json" \
  -d '{
    "customer_name": "John Doe",
    "amount": "1,250.00 TL"
  }' \
  -o generated.pdf
```

## 🏗️ Architecture

- **Backend**: Spring Boot 3.2+ with Java 17
- **Database**: PostgreSQL 15 with JSONB support
- **Cache**: Redis 7
- **Message Queue**: RabbitMQ 3
- **Object Storage**: MinIO
- **PDF Generation**: iText 7
- **Frontend**: React 18 with TypeScript (planned)

## 📊 Features Implemented

- ✅ Template CRUD operations
- ✅ Schema-based PDF generation
- ✅ Dynamic data binding with placeholders
- ✅ Multiple element types (TEXT, TABLE, IMAGE, CONTAINER)
- ✅ Error handling and validation
- ✅ Comprehensive test coverage
- ✅ Docker containerization
- ✅ API documentation with Swagger

## 🔄 Development Workflow

1. Make changes to the code
2. Run tests: `mvn test` (in backend directory)
3. Test API: `./scripts/test-api.sh`
4. View logs: `./scripts/logs.sh backend`

## 🐛 Troubleshooting

### Services not starting
```bash
# Check Docker status
docker ps

# Restart services
./scripts/stop-services.sh
./scripts/start-services.sh
```

### Database connection issues
```bash
# Reset database
./scripts/reset-database.sh
```

### View logs
```bash
# All services
./scripts/logs.sh

# Specific service
./scripts/logs.sh postgres
./scripts/logs.sh backend
```
