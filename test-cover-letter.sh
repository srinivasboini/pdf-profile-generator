#!/bin/bash

echo "========================================="
echo "Cover Letter PDF Generation Test"
echo "========================================="
echo ""

# Check if server is running
echo "Checking if server is running..."
if ! curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/pdf/health | grep -q "200"; then
    echo "❌ Error: Server is not running on port 8080"
    echo "Please start the server first: mvn spring-boot:run"
    exit 1
fi

echo "✅ Server is running"
echo ""

# Test 1: List available cover letter templates
echo "Test 1: Fetching available cover letter templates..."
TEMPLATES=$(curl -s http://localhost:8080/api/pdf/cover-letter-templates)
echo "Available templates:"
echo "$TEMPLATES" | jq '.'
echo ""

# Test 2: Generate cover letter PDF
echo "Test 2: Generating cover letter PDF..."
HTTP_CODE=$(curl -s -w "%{http_code}" -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d @src/main/resources/input/sample-cover-letter.json \
  -o test_cover_letter.pdf)

if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Cover letter PDF generated successfully!"
    echo "   File: test_cover_letter.pdf"
    echo "   Size: $(ls -lh test_cover_letter.pdf | awk '{print $5}')"
    echo ""
    echo "Opening PDF..."
    open test_cover_letter.pdf 2>/dev/null || echo "   (PDF saved, open manually)"
else
    echo "❌ Failed to generate PDF (HTTP $HTTP_CODE)"
fi

echo ""
echo "========================================="
echo "Test Complete"
echo "========================================="
