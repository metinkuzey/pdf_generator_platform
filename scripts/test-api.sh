#!/bin/bash

# PDF Generator Platform - API Test Script
# This script tests the main API endpoints

set -e

BASE_URL="http://localhost:8080/api"

echo "🧪 Testing PDF Generator Platform API..."

# Test health endpoint
echo "🔍 Testing health endpoint..."
curl -f "$BASE_URL/actuator/health" || {
    echo "❌ Health check failed. Is the backend running?"
    exit 1
}
echo "✅ Health check passed"

# Test templates endpoint
echo "🔍 Testing templates endpoint..."
curl -f "$BASE_URL/templates" -H "Content-Type: application/json" || {
    echo "❌ Templates endpoint failed"
    exit 1
}
echo "✅ Templates endpoint working"

# Create a test template
echo "🔍 Creating test template..."
TEMPLATE_RESPONSE=$(curl -s -X POST "$BASE_URL/templates" \
    -H "Content-Type: application/json" \
    -d '{
        "name": "Test Template",
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
                        "text": "Test Document for {{customer_name}}",
                        "fontSize": 16,
                        "fontWeight": "bold",
                        "textAlign": "center"
                    }
                }
            ]
        }
    }')

TEMPLATE_ID=$(echo $TEMPLATE_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TEMPLATE_ID" ]; then
    echo "❌ Failed to create test template"
    exit 1
fi

echo "✅ Test template created with ID: $TEMPLATE_ID"

# Test PDF generation
echo "🔍 Testing PDF generation..."
curl -f "$BASE_URL/pdf/generate/$TEMPLATE_ID" \
    -H "Content-Type: application/json" \
    -d '{
        "customer_name": "Ahmet Yılmaz",
        "customer_id": "12345678901"
    }' \
    -o test-output.pdf || {
    echo "❌ PDF generation failed"
    exit 1
}

if [ -f "test-output.pdf" ] && [ -s "test-output.pdf" ]; then
    echo "✅ PDF generated successfully ($(wc -c < test-output.pdf) bytes)"
    rm test-output.pdf
else
    echo "❌ PDF file is empty or not created"
    exit 1
fi

# Test PDF preview
echo "🔍 Testing PDF preview..."
curl -f "$BASE_URL/pdf/preview/$TEMPLATE_ID" \
    -H "Content-Type: application/json" \
    -d '{}' \
    -o test-preview.pdf || {
    echo "❌ PDF preview failed"
    exit 1
}

if [ -f "test-preview.pdf" ] && [ -s "test-preview.pdf" ]; then
    echo "✅ PDF preview generated successfully ($(wc -c < test-preview.pdf) bytes)"
    rm test-preview.pdf
else
    echo "❌ PDF preview file is empty or not created"
    exit 1
fi

echo ""
echo "🎉 All API tests passed!"
echo ""
echo "📋 Available endpoints:"
echo "   Health: $BASE_URL/actuator/health"
echo "   Swagger: $BASE_URL/swagger-ui.html"
echo "   Templates: $BASE_URL/templates"
echo "   PDF Generation: $BASE_URL/pdf/generate/{templateId}"
echo "   PDF Preview: $BASE_URL/pdf/preview/{templateId}"