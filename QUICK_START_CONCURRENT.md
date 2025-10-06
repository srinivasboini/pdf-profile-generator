# Quick Start: Concurrent PDF Generation

## 🚀 Running the Application

### 1. Build the Application
```bash
mvn clean package -DskipTests
```

### 2. Run with Production Settings
```bash
java -Xms512m -Xmx2048m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
     -jar target/pdfhtml-1.0-SNAPSHOT.jar
```

### 3. Verify Server is Running
```bash
curl http://localhost:8080/api/pdf/health
# Expected: "PDF Generator Service is running"
```

---

## 🧪 Test Concurrent Requests

### Option 1: Use the Test Script (Recommended)
```bash
./concurrent-test.sh
```

This will:
- Send 20 concurrent PDF generation requests
- Measure performance
- Save PDFs to `target/concurrent-test-output/`

### Option 2: Manual Test (10 concurrent requests)
```bash
# Create test-concurrent.sh
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/pdf/generate \
    -H "Content-Type: application/json" \
    -d @src/main/resources/input/senior-engineer-profile.json \
    -o output_$i.pdf &
done
wait
echo "Done! Check output_*.pdf files"
```

---

## 📊 Thread Pool Configuration

Current settings (in `application.properties`):

| Setting | Value | Description |
|---------|-------|-------------|
| Max Threads | 200 | Max concurrent requests |
| Min Spare Threads | 10 | Always-ready threads |
| Max Connections | 10,000 | Total simultaneous connections |
| Accept Queue | 100 | Requests queued when busy |
| Connection Timeout | 20s | Connection timeout |
| Request Timeout | 60s | Max request processing time |

### Adjust for Your Load

**Light Load (1-50 users):**
```properties
server.tomcat.threads.max=50
```

**Medium Load (50-100 users):**
```properties
server.tomcat.threads.max=100
```

**Heavy Load (100-200 users):**
```properties
server.tomcat.threads.max=200
```

**Very Heavy Load (200+ users):**
```properties
server.tomcat.threads.max=400
# Also increase JVM heap: -Xmx4096m
```

---

## 🔍 Monitoring

### Check Thread Pool Status
```bash
# If Spring Boot Actuator is enabled
curl http://localhost:8080/actuator/metrics/tomcat.threads.busy
curl http://localhost:8080/actuator/metrics/tomcat.threads.current
```

### Watch Logs
```bash
tail -f logs/application.log | grep "PDF Generation Request Received"
```

---

## 🐛 Troubleshooting

### Problem: "Connection Refused"
**Cause:** Accept queue is full

**Solution:**
```properties
server.tomcat.accept-count=200  # Increase queue size
```

### Problem: "OutOfMemoryError"
**Cause:** Not enough heap for concurrent PDFs

**Solution:**
```bash
# Increase max heap
java -Xmx4096m -jar target/pdfhtml-1.0-SNAPSHOT.jar
```

### Problem: Slow Response (>10s)
**Cause:** Thread pool exhausted

**Solution:**
```properties
server.tomcat.threads.max=400  # Increase thread pool
```

---

## ✅ Thread-Safety Checklist

- [x] Each request gets its own ByteArrayOutputStream
- [x] PdfWriter and PdfDocument created per request
- [x] Thymeleaf template caching enabled (thread-safe)
- [x] No shared mutable state
- [x] Proper resource cleanup (finally blocks)
- [x] Exception handling for concurrent errors
- [x] Thread pool configured for concurrent load

---

## 📚 Documentation

- **Detailed Guide:** `CONCURRENCY_GUIDE.md`
- **Summary:** `THREAD_SAFETY_SUMMARY.md`
- **This Guide:** `QUICK_START_CONCURRENT.md`

---

## 🎯 Expected Performance

With default settings (200 max threads, 2GB heap):

- **Throughput:** 20-50 PDFs/second (depends on PDF complexity)
- **Concurrent Users:** Up to 200 simultaneous requests
- **Response Time:** 1-3 seconds per PDF (typical)

---

## 🚢 Production Deployment

### Docker (Example)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/pdfhtml-1.0-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="-Xms512m -Xmx2048m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]
```

### Kubernetes (Resources)
```yaml
resources:
  requests:
    memory: "1Gi"
    cpu: "500m"
  limits:
    memory: "3Gi"
    cpu: "2000m"
```

---

## 🔐 Security Note

For production, consider adding:
- Rate limiting (prevent abuse)
- API authentication
- Request size validation
- CORS configuration

---

**Your app is now ready for concurrent production use! 🎉**
