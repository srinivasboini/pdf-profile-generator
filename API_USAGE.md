# PDF Generator API Documentation

## Overview
Spring Boot REST API for generating professional PDF resumes from profile data with optional OpenAI optimization.

## Base URL
```
http://localhost:8080/api/pdf
```

## Endpoints

### 1. Generate PDF
**POST** `/api/pdf/generate`

Generates a PDF from profile data using a specified template.

#### Request Headers
```
Content-Type: application/json
```

#### Request Body
```json
{
  "templateId": "modern_profile_template",
  "optimizeWithAI": true,
  "jobDescription": "Senior Backend Engineer role requiring Java and Spring Boot...",
  "profile": {
    "name": "Sarah Johnson",
    "email": "sarah.johnson@email.com",
    "phone": "+1 (555) 123-4567",
    "location": "San Francisco, CA",
    "summary": "Experienced software engineer with 5+ years...",
    "skills": ["Java", "Spring Boot", "AWS", "Docker"],
    "experience": [
      {
        "title": "Senior Software Engineer",
        "company": "TechCorp Solutions",
        "duration": "Jan 2021 - Present",
        "description": "<ul><li>Led development of microservices</li></ul>"
      }
    ],
    "education": [
      {
        "degree": "Bachelor of Science in Computer Science",
        "institution": "University of California, Berkeley",
        "year": "2018"
      }
    ],
    "certifications": [
      "AWS Certified Solutions Architect",
      "Oracle Certified Java Developer"
    ]
  }
}
```

#### Request Parameters

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| templateId | String | Yes | Template identifier: `profile_template`, `modern_profile_template`, or `minimalist_profile_template` |
| profile | Object | Yes | Candidate profile data |
| optimizeWithAI | Boolean | No | Enable OpenAI optimization (default: false) |
| jobDescription | String | No | Job description for AI optimization (required if optimizeWithAI is true) |

#### Response
- **Content-Type:** `application/pdf`
- **Status:** 200 OK
- **Body:** PDF file (binary)

#### Example cURL Request
```bash
curl -X POST http://localhost:8080/api/pdf/generate \
  -H "Content-Type: application/json" \
  -d '{
    "templateId": "modern_profile_template",
    "optimizeWithAI": false,
    "profile": {
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "+1 234-567-8900",
      "location": "New York, NY",
      "summary": "Skilled developer...",
      "skills": ["Java", "Python"],
      "experience": [],
      "education": [],
      "certifications": []
    }
  }' \
  --output resume.pdf
```

#### Example with AI Optimization
```bash
curl -X POST http://localhost:8080/api/pdf/generate \
  -H "Content-Type: application/json" \
  -d '{
    "templateId": "modern_profile_template",
    "optimizeWithAI": true,
    "jobDescription": "Looking for a Senior Java Developer with Spring Boot experience...",
    "profile": {
      "name": "Sarah Johnson",
      "email": "sarah@example.com",
      "phone": "+1 555-123-4567",
      "location": "San Francisco, CA",
      "summary": "Software engineer with expertise in Java",
      "skills": ["Java", "Spring Boot", "AWS"],
      "experience": [{
        "title": "Software Engineer",
        "company": "Tech Corp",
        "duration": "2020 - Present",
        "description": "<ul><li>Developed REST APIs</li></ul>"
      }],
      "education": [],
      "certifications": []
    }
  }' \
  --output optimized_resume.pdf
```

### 2. Get Available Templates
**GET** `/api/pdf/templates`

Returns list of available template IDs.

#### Response
```json
[
  "profile_template",
  "modern_profile_template",
  "minimalist_profile_template"
]
```

#### Example
```bash
curl http://localhost:8080/api/pdf/templates
```

### 3. Health Check
**GET** `/api/pdf/health`

Check if the service is running.

#### Response
```
PDF Generator Service is running
```

#### Example
```bash
curl http://localhost:8080/api/pdf/health
```

## Template Options

### 1. `profile_template`
Professional blue theme with single-column layout.

### 2. `modern_profile_template`
Two-column design with purple gradient sidebar.

### 3. `minimalist_profile_template`
Classic black & white design with elegant typography.

## Configuration

### Environment Variables
Set these in `application.properties` or as environment variables:

```properties
# OpenAI API Configuration
OPENAI_API_KEY=sk-your-api-key-here

# Optional: Override defaults
openai.api.model=gpt-5
openai.api.timeout=180
server.port=8080
```

## Running the Application

### Using Maven
```bash
# Set OpenAI API key
export OPENAI_API_KEY=sk-your-key

# Run the application
mvn spring-boot:run
```

### Using JAR
```bash
# Build
mvn clean package

# Run
java -jar target/pdfhtml-1.0-SNAPSHOT.jar
```

### Using IDE (IntelliJ)
1. Open `PdfGeneratorApplication.java`
2. Set environment variable: `OPENAI_API_KEY=sk-your-key`
3. Run the main method

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Template ID is required"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error"
}
```

## Testing with Postman

1. **Create a new POST request** to `http://localhost:8080/api/pdf/generate`
2. **Set Headers:** `Content-Type: application/json`
3. **Add Body (raw JSON):** Use the request body example above
4. **Send** and save response as PDF

## Notes

- **AI Optimization:** Requires valid OpenAI API key and costs apply per request
- **PDF Size:** Typical generated PDFs are 50-200KB
- **Timeout:** AI optimization may take 30-180 seconds depending on model
- **Rate Limits:** Subject to OpenAI API rate limits