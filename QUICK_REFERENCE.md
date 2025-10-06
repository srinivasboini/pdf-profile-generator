# Cover Letter API - Quick Reference

## 🚀 Your Exact UI Format

```json
{
  "templateId": "cover_letter_professional_001",
  "coverLetter": {
    "header": {
      "name": "John Doe",
      "email": "john.doe@email.com",
      "phone": "(555) 123-4567",
      "date": "2025-01-15"
    },
    "recipient": {
      "name": "Hiring Manager",
      "company": "TechCorp Inc.",
      "position": "Software Engineer"
    },
    "salutation": "Dear Hiring Manager",
    "content": [
      "Paragraph 1...",
      "Paragraph 2...",
      "Paragraph 3..."
    ],
    "closing": {
      "valediction": "Best regards",
      "name": "John Doe"
    }
  }
}
```

---

## ✅ Required Fields Only

```json
{
  "templateId": "cover_letter_starter_001",
  "coverLetter": {
    "header": {
      "name": "Jane Smith",
      "email": "jane@email.com",
      "phone": "555-1234",
      "date": "2025-01-15"
    },
    "content": [
      "I am applying for the position.",
      "I have relevant experience.",
      "Thank you for your consideration."
    ]
  }
}
```

**Auto-defaults:**
- Salutation: "Dear Hiring Manager,"
- Company: "Hiring Team"
- Valediction: "Sincerely,"
- Signature: "Jane Smith" (from header.name)

---

## 📋 Field Checklist

### ✅ Required
- [ ] `templateId`
- [ ] `header.name`
- [ ] `header.email`
- [ ] `header.phone`
- [ ] `header.date`
- [ ] `content` (array)

### 🔧 Optional
- [ ] `recipient.name`
- [ ] `recipient.company`
- [ ] `recipient.position`
- [ ] `salutation`
- [ ] `closing.valediction`
- [ ] `closing.name`

---

## 🎨 Available Templates

| ID | Style |
|----|-------|
| `cover_letter_starter_001` | Classic Professional |
| `cover_letter_starter_002` | Clean Minimal |
| `cover_letter_starter_003` | Traditional |
| `cover_letter_professional_001` | Modern Executive |
| `cover_letter_professional_002` | Corporate Professional |
| `cover_letter_professional_003` | Business Professional |
| `cover_letter_expert_001` | Premium Portfolio |
| `cover_letter_expert_002` | Executive Premium |
| `cover_letter_expert_003` | Elite Professional |

---

## 🧪 Quick Test

```bash
# Start server
mvn spring-boot:run

# Test (in another terminal)
curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d @src/main/resources/input/ui-format-cover-letter.json \
  -o test.pdf

# View PDF
open test.pdf
```

---

## ⚡ JavaScript Example

```javascript
const response = await fetch('/api/pdf/generate-cover-letter', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    templateId: "cover_letter_professional_001",
    coverLetter: {
      header: {
        name: formData.name,
        email: formData.email,
        phone: formData.phone,
        date: new Date().toISOString().split('T')[0]
      },
      recipient: {
        name: formData.recipientName,
        company: formData.company,
        position: formData.position
      },
      content: formData.paragraphs
    }
  })
});

const blob = await response.blob();
const url = URL.createObjectURL(blob);
const a = document.createElement('a');
a.href = url;
a.download = `${formData.name}_cover_letter.pdf`;
a.click();
```

---

## 📦 Response

**Success (200):**
- Content-Type: `application/pdf`
- Filename: `{name}_cover_letter.pdf`
- Body: PDF binary

**Error (400):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Header information is required"
}
```

---

## 📚 Full Docs

- **`UI_JSON_FORMAT.md`** - Complete API reference
- **`UPDATE_SUMMARY.md`** - What changed
- **`ui-format-cover-letter.json`** - Full example
- **`ui-format-minimal.json`** - Minimal example
