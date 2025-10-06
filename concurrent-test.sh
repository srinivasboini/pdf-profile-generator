#!/bin/bash

# Concurrent PDF Generation Test Script
# Tests thread-safety by running multiple concurrent requests

echo "========================================="
echo "PDF Generation Concurrent Test"
echo "========================================="
echo ""

# Configuration
API_URL="http://localhost:8080/api/pdf/generate"
CONCURRENT_REQUESTS=20
TEST_DATA_FILE="src/main/resources/input/senior-engineer-profile.json"

# Check if server is running
echo "Checking if server is running..."
if ! curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/pdf/health | grep -q "200"; then
    echo "❌ Error: Server is not running on port 8080"
    echo "Please start the server first: mvn spring-boot:run"
    exit 1
fi

echo "✅ Server is running"
echo ""

# Check if test data exists
if [ ! -f "$TEST_DATA_FILE" ]; then
    echo "❌ Error: Test data file not found: $TEST_DATA_FILE"
    exit 1
fi

echo "Test Configuration:"
echo "  - API URL: $API_URL"
echo "  - Concurrent Requests: $CONCURRENT_REQUESTS"
echo "  - Test Data: $TEST_DATA_FILE"
echo ""

# Create output directory
OUTPUT_DIR="target/concurrent-test-output"
mkdir -p "$OUTPUT_DIR"

echo "Starting concurrent PDF generation test..."
echo "Sending $CONCURRENT_REQUESTS concurrent requests..."
echo ""

# Start time
START_TIME=$(date +%s)

# Array to track PIDs
pids=()

# Launch concurrent requests
for i in $(seq 1 $CONCURRENT_REQUESTS); do
    (
        response=$(curl -s -w "\n%{http_code}" -X POST "$API_URL" \
            -H "Content-Type: application/json" \
            -d @"$TEST_DATA_FILE" \
            -o "$OUTPUT_DIR/output_$i.pdf" 2>&1)

        http_code=$(echo "$response" | tail -n 1)

        if [ "$http_code" = "200" ]; then
            echo "✅ Request $i: SUCCESS (HTTP $http_code)"
        else
            echo "❌ Request $i: FAILED (HTTP $http_code)"
        fi
    ) &
    pids+=($!)
done

# Wait for all requests to complete
echo "Waiting for all requests to complete..."
for pid in "${pids[@]}"; do
    wait $pid
done

# End time
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo ""
echo "========================================="
echo "Test Results"
echo "========================================="
echo "Total requests: $CONCURRENT_REQUESTS"
echo "Duration: ${DURATION}s"
echo "Average time per request: $(echo "scale=2; $DURATION / $CONCURRENT_REQUESTS" | bc)s"
echo ""

# Count successful PDFs
SUCCESS_COUNT=$(find "$OUTPUT_DIR" -name "output_*.pdf" -type f | wc -l | tr -d ' ')
echo "Successful PDFs generated: $SUCCESS_COUNT / $CONCURRENT_REQUESTS"

if [ "$SUCCESS_COUNT" -eq "$CONCURRENT_REQUESTS" ]; then
    echo "✅ All PDFs generated successfully!"
    echo ""
    echo "Output files located in: $OUTPUT_DIR"
else
    echo "⚠️  Some PDFs failed to generate"
fi

echo ""
echo "========================================="
echo "Thread-Safety Test Complete"
echo "========================================="
