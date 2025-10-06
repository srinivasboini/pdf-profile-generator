# Thread-Safety & Concurrency Guide

## Overview
This PDF generation application is designed to handle **multiple concurrent users** safely and efficiently. All components have been configured for thread-safe operation.

---

## Thread-Safety Implementation

### 1. **Controller Layer (PdfController)**
- ✅ **Stateless**: No shared mutable state
- ✅ **Request-scoped resources**: Each request gets its own `ByteArrayOutputStream`
- ✅ **Proper resource cleanup**: Finally blocks ensure streams are closed
- ✅ **Exception handling**: Granular error handling with proper HTTP status codes

### 2. **Service Layer**

#### TemplateService
- ✅ **Thread-safe template engine**: Thymeleaf `TemplateEngine` is thread-safe
- ✅ **Template caching enabled**: 1-hour TTL for better performance
- ✅ **Request-scoped context**: Each request creates a new `Context` object
- ✅ **No shared mutable state**

#### PdfGeneratorService
- ✅ **Request-scoped PDF resources**: New `PdfWriter` and `PdfDocument` per request
- ✅ **Proper resource cleanup**: Finally blocks ensure resources are closed
- ✅ **No shared state between requests**

#### ProfileOptimizer (OpenAI Service)
- ✅ **Thread-safe singleton**: OpenAI client handles concurrent requests
- ✅ **Request-scoped messages**: New message list per request
- ✅ **Configurable timeout**: 180 seconds default

### 3. **Server Configuration**

#### Tomcat Thread Pool (`application.properties`)
```properties
server.tomcat.threads.max=200              # Max concurrent requests
server.tomcat.threads.min-spare=10         # Minimum idle threads
server.tomcat.max-connections=10000        # Max simultaneous connections
server.tomcat.accept-count=100             # Queue size when at capacity
server.tomcat.connection-timeout=20000     # 20 second timeout
```

#### Request Timeout
```properties
spring.mvc.async.request-timeout=60000     # 60 second request timeout
```

#### Response Compression (for large PDFs)
```properties
server.compression.enabled=true
server.compression.mime-types=application/pdf,application/json
```

#### HTTP/2 Support
```properties
server.http2.enabled=true                  # Better concurrent performance
```

---

## Performance Tuning

### JVM Configuration
For production deployment, use these JVM flags:

```bash
java -Xms512m \
     -Xmx2048m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar pdfhtml.jar
```

**Explanation:**
- `-Xms512m`: Initial heap size (512 MB)
- `-Xmx2048m`: Maximum heap size (2 GB)
- `-XX:+UseG1GC`: Use G1 Garbage Collector (better for low latency)
- `-XX:MaxGCPauseMillis=200`: Target max GC pause time (200ms)

### Recommended Server Resources

| User Load | CPU Cores | RAM | Thread Pool Max |
|-----------|-----------|-----|-----------------|
| 1-50 concurrent | 2 cores | 2 GB | 50 |
| 50-100 concurrent | 4 cores | 4 GB | 100 |
| 100-200 concurrent | 8 cores | 8 GB | 200 |
| 200+ concurrent | 16+ cores | 16+ GB | 400 |

---

## Testing Concurrency

### Load Testing with Apache Bench
```bash
# Test with 100 concurrent users, 1000 total requests
ab -n 1000 -c 100 \
   -p profile.json \
   -T "application/json" \
   http://localhost:8080/api/pdf/generate
```

### Load Testing with curl (simple)
```bash
# Run 10 concurrent requests
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/pdf/generate \
    -H "Content-Type: application/json" \
    -d @profile.json \
    -o output_$i.pdf &
done
wait
```

---

## Monitoring & Observability

### Health Check Endpoint
```bash
curl http://localhost:8080/api/pdf/health
```

### Thread Pool Metrics (Spring Boot Actuator)
Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Then access metrics:
```bash
curl http://localhost:8080/actuator/metrics/tomcat.threads.busy
curl http://localhost:8080/actuator/metrics/tomcat.threads.current
```

---

## Common Concurrent Issues & Solutions

### Issue 1: OutOfMemoryError
**Cause**: Too many concurrent requests generating large PDFs

**Solution**:
1. Increase JVM heap size (`-Xmx`)
2. Reduce `server.tomcat.threads.max`
3. Implement rate limiting (see below)

### Issue 2: Slow Response Times
**Cause**: Thread pool exhaustion

**Solution**:
1. Increase `server.tomcat.threads.max`
2. Optimize PDF template size
3. Enable response compression

### Issue 3: Connection Refused
**Cause**: Accept queue full

**Solution**:
1. Increase `server.tomcat.accept-count`
2. Add load balancer with multiple instances

---

## Rate Limiting (Optional)

To prevent abuse, implement rate limiting with Bucket4j:

```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.0.0</version>
</dependency>
```

---

## Thread-Safety Guarantees

### ✅ What is Thread-Safe
- All service beans (singleton, but stateless)
- Template engine with caching
- PDF generation per request
- Request/response handling

### ❌ What is NOT Shared (Good!)
- `ByteArrayOutputStream` - request-scoped
- `PdfWriter` - request-scoped
- `PdfDocument` - request-scoped
- Thymeleaf `Context` - request-scoped

### 🔒 Concurrency Best Practices Implemented
1. **Immutable where possible**: Configuration values
2. **Request-scoped resources**: Streams, PDF objects
3. **Proper cleanup**: Finally blocks for all resources
4. **No static mutable state**: All state is in Spring beans or request scope
5. **Thread pool configuration**: Prevents resource exhaustion

---

## Deployment Checklist

- [x] Thread pool configured
- [x] Request timeout set
- [x] Template caching enabled
- [x] Resource cleanup implemented
- [x] Exception handling added
- [x] JVM flags documented
- [ ] Load testing completed
- [ ] Monitoring/alerting configured
- [ ] Rate limiting (if needed)

---

## Contact & Support

For questions about concurrency configuration, refer to:
- Spring Boot Tomcat docs: https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.server
- iText thread-safety: https://kb.itextpdf.com/home/it7kb/faq/thread-safety
- Thymeleaf concurrency: https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html#context-objects
