# Cover Letter PDF Generation API

## Overview
The application now supports **both resume and cover letter PDF generation** with matching template designs. Each resume template tier (Starter, Professional, Expert) has corresponding cover letter templates with the same visual style.

---

## 📋 Available Templates

### Starter Templates (Entry-level)
- `cover_letter_starter_001` - Classic Professional style
- `cover_letter_starter_002` - Clean Minimal style
- `cover_letter_starter_003` - Traditional format

### Professional Templates (Mid-level)
- `cover_letter_professional_001` - Modern Executive (sidebar design)
- `cover_letter_professional_002` - Corporate Professional (gradient header)
- `cover_letter_professional_003` - Business Professional (blue header)

### Expert Templates (Senior-level)
- `cover_letter_expert_001` - Premium Portfolio (two-column)
- `cover_letter_expert_002` - Executive Premium (gradient header)
- `cover_letter_expert_003` - Elite Professional (accent bar)

---

## 🚀 API Endpoints

### 1. Generate Cover Letter PDF
**POST** `/api/pdf/generate-cover-letter`

Generates a cover letter PDF from template ID and cover letter data.

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "templateId": "cover_letter_professional_001",
  "coverLetter": {
    "candidateName": "John Doe",
    "email": "john.doe@email.com",
    "phone": "(555) 123-4567",
    "address": "123 Main Street, San Francisco, CA 94102",
    "date": "January 15, 2024",
    "recipientName": "Sarah Johnson",
    "recipientTitle": "Senior Hiring Manager",
    "companyName": "Tech Innovations Inc.",
    "companyAddress": "456 Market Street, San Francisco, CA 94105",
    "salutation": "Dear Ms. Johnson,",
    "bodyParagraphs": [
      "First paragraph of the cover letter...",
      "Second paragraph...",
      "Third paragraph...",
      "Closing paragraph..."
    ],
    "closing": "Sincerely,",
    "signature": "John Doe"
  }
}
```

**Response:**
- **Status:** 200 OK
- **Content-Type:** application/pdf
- **Body:** PDF file (binary)
- **Filename:** `John_Doe_cover_letter.pdf`

**Error Responses:**
- **400 Bad Request** - Invalid input data
- **500 Internal Server Error** - PDF generation failed

---

### 2. Get Available Cover Letter Templates
**GET** `/api/pdf/cover-letter-templates`

Returns a list of all available cover letter template IDs.

**Response:**
```json
[
  "cover_letter_starter_001",
  "cover_letter_starter_002",
  "cover_letter_starter_003",
  "cover_letter_professional_001",
  "cover_letter_professional_002",
  "cover_letter_professional_003",
  "cover_letter_expert_001",
  "cover_letter_expert_002",
  "cover_letter_expert_003"
]
```

---

### 3. Get Available Resume Templates
**GET** `/api/pdf/templates`

Returns a list of all available resume template IDs.

**Response:**
```json
[
  "profile_template",
  "modern_profile_template",
  "minimalist_profile_template"
]
```

---

## 📝 Cover Letter Model

### CoverLetter Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `candidateName` | String | Yes | Full name of the candidate |
| `email` | String | Yes | Email address |
| `phone` | String | Yes | Phone number |
| `address` | String | Yes | Full mailing address |
| `date` | String | Yes | Letter date (e.g., "January 15, 2024") |
| `recipientName` | String | Yes | Name of the hiring manager/recipient |
| `recipientTitle` | String | Yes | Job title of the recipient |
| `companyName` | String | Yes | Company name |
| `companyAddress` | String | Yes | Company address |
| `salutation` | String | Yes | Letter greeting (e.g., "Dear Ms. Johnson,") |
| `bodyParagraphs` | List<String> | Yes | Array of body paragraphs |
| `closing` | String | Yes | Letter closing (e.g., "Sincerely,") |
| `signature` | String | Yes | Signature name |

---

## 🧪 Testing with cURL

### Generate Cover Letter PDF
```bash
curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d @src/main/resources/input/sample-cover-letter.json \
  -o john_doe_cover_letter.pdf
```

### List Cover Letter Templates
```bash
curl http://localhost:8080/api/pdf/cover-letter-templates
```

---

## 🎨 Template Matching Guide

Use matching template IDs for resume and cover letter:

| Resume Template | Cover Letter Template | Style |
|----------------|----------------------|-------|
| `starter_template_001` | `cover_letter_starter_001` | Classic Professional |
| `starter_template_002` | `cover_letter_starter_002` | Clean Minimal |
| `starter_template_003` | `cover_letter_starter_003` | Traditional |
| `professional_template_001` | `cover_letter_professional_001` | Modern Executive |
| `professional_template_002` | `cover_letter_professional_002` | Corporate |
| `professional_template_003` | `cover_letter_professional_003` | Business |
| `expert_template_001` | `cover_letter_expert_001` | Premium Portfolio |
| `expert_template_002` | `cover_letter_expert_002` | Executive Premium |
| `expert_template_003` | `cover_letter_expert_003` | Elite Professional |

---

## 🔒 Thread-Safety

All cover letter endpoints are **fully thread-safe**:
- ✅ Request-scoped `ByteArrayOutputStream` (no sharing)
- ✅ Request-scoped Thymeleaf `Context` (new per request)
- ✅ Template caching enabled (1-hour TTL, thread-safe)
- ✅ Proper resource cleanup (finally blocks)
- ✅ No shared mutable state

Same concurrent handling as resume generation:
- Max 200 concurrent requests
- 10,000 max connections
- Thread pool configured

---

## 📦 Sample Request Files

### Test Cover Letter
```bash
# Located at: src/main/resources/input/sample-cover-letter.json
curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d @src/main/resources/input/sample-cover-letter.json \
  -o test_cover_letter.pdf
```

---

## 🚀 Usage Flow (UI Integration)

1. **User selects template tier** (Starter/Professional/Expert)
2. **UI fetches matching templates:**
   - Resume: `GET /api/pdf/templates`
   - Cover Letter: `GET /api/pdf/cover-letter-templates`
3. **User fills in cover letter content** (candidate info, recipient, paragraphs)
4. **UI sends request** to `POST /api/pdf/generate-cover-letter`
5. **User downloads PDF** with filename: `{candidateName}_cover_letter.pdf`

---

## 📊 Performance

Same performance characteristics as resume generation:
- **Concurrent requests:** Up to 200 simultaneous
- **Response time:** 1-3 seconds per PDF (typical)
- **Template caching:** Enabled for faster processing
- **Compression:** Enabled for large PDFs

---

## 🔍 Validation

All fields are validated using Jakarta Validation:
- `templateId` - @NotBlank
- `coverLetter` - @NotNull
- Missing or invalid fields return **400 Bad Request**

---

## 📝 Example: Full Cover Letter Request

```json
{
  "templateId": "cover_letter_expert_002",
  "coverLetter": {
    "candidateName": "Jane Smith",
    "email": "jane.smith@email.com",
    "phone": "+1-555-987-6543",
    "address": "789 Oak Avenue, New York, NY 10001",
    "date": "February 1, 2024",
    "recipientName": "Michael Chen",
    "recipientTitle": "VP of Engineering",
    "companyName": "InnovateTech Corp",
    "companyAddress": "1000 Tech Boulevard, New York, NY 10002",
    "salutation": "Dear Mr. Chen,",
    "bodyParagraphs": [
      "I am excited to apply for the Lead Software Architect position at InnovateTech Corp. With 12 years of experience architecting scalable systems and leading high-performing engineering teams, I believe I would be an excellent fit for this role.",
      "At my current position with CloudScale Solutions, I designed and implemented a distributed microservices platform that handles 10M+ transactions daily with 99.99% uptime. This experience directly aligns with your need for someone who can scale InnovateTech's infrastructure.",
      "I am particularly impressed by InnovateTech's commitment to open-source contributions and technical excellence. I would be thrilled to bring my expertise in system design, cloud architecture, and team leadership to your organization.",
      "I would welcome the opportunity to discuss how my experience can help InnovateTech achieve its ambitious technical goals. Thank you for your consideration."
    ],
    "closing": "Best regards,",
    "signature": "Jane Smith"
  }
}
```

---

## 🎯 Quick Start

1. **Start the server:**
   ```bash
   mvn spring-boot:run
   ```

2. **Test cover letter generation:**
   ```bash
   curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
     -H "Content-Type: application/json" \
     -d @src/main/resources/input/sample-cover-letter.json \
     -o cover_letter.pdf
   ```

3. **View generated PDF:**
   ```bash
   open cover_letter.pdf
   ```

---

**Both resume and cover letter generation are now production-ready and thread-safe!** 🎉
