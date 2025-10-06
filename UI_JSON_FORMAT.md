# Cover Letter API - UI JSON Format

## ✅ Updated Format

The API now accepts the exact JSON structure from your UI with **nested objects**.

---

## 📋 Request Structure

### **Endpoint:**
```
POST /api/pdf/generate-cover-letter
```

### **Full Request Format:**

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
      "I am excited to apply for the Software Engineer position at TechCorp. With 5 years of experience in full-stack development, I am confident in my ability to contribute to your team.",
      "In my current role, I have successfully led multiple projects using React, Node.js, and AWS, delivering scalable solutions that serve thousands of users daily.",
      "I would welcome the opportunity to discuss how my skills align with TechCorp's needs.",
      "Thank you for your consideration."
    ],
    "closing": {
      "valediction": "Best regards",
      "name": "John Doe"
    }
  }
}
```

---

## 📊 Field Reference

### **Top Level**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `templateId` | String | ✅ Yes | Template ID (e.g., `"cover_letter_professional_001"`) |
| `coverLetter` | Object | ✅ Yes | Cover letter data |

### **coverLetter.header** (Required)

| Field | Type | Required | Example |
|-------|------|----------|---------|
| `name` | String | ✅ Yes | `"John Doe"` |
| `email` | String | ✅ Yes | `"john.doe@email.com"` |
| `phone` | String | ✅ Yes | `"(555) 123-4567"` |
| `date` | String | ✅ Yes | `"2025-01-15"` or `"January 15, 2025"` |

### **coverLetter.recipient** (Optional)

| Field | Type | Required | Example |
|-------|------|----------|---------|
| `name` | String | ❌ No | `"Hiring Manager"` or `"Sarah Johnson"` |
| `company` | String | ❌ No | `"TechCorp Inc."` |
| `position` | String | ❌ No | `"Software Engineer"` |

### **coverLetter.salutation** (Optional)

| Field | Type | Required | Example | Default |
|-------|------|----------|---------|---------|
| `salutation` | String | ❌ No | `"Dear Hiring Manager"` | Auto-generated |

**Auto-generation logic:**
- If `recipient.name` provided → `"Dear {name},"`
- Otherwise → `"Dear Hiring Manager,"`

### **coverLetter.content** (Required)

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `content` | Array of Strings | ✅ Yes | Body paragraphs (3-5 recommended) |

### **coverLetter.closing** (Optional)

| Field | Type | Required | Example | Default |
|-------|------|----------|---------|---------|
| `valediction` | String | ❌ No | `"Best regards"` | `"Sincerely"` |
| `name` | String | ❌ No | `"John Doe"` | Uses `header.name` |

---

## 🎯 Minimal Request (No Recipient)

When you don't know the hiring manager:

```json
{
  "templateId": "cover_letter_starter_001",
  "coverLetter": {
    "header": {
      "name": "Jane Smith",
      "email": "jane.smith@email.com",
      "phone": "(555) 987-6543",
      "date": "2025-01-15"
    },
    "content": [
      "I am writing to express my interest in the position at your company.",
      "With my background and skills, I believe I would be an excellent fit.",
      "Thank you for considering my application."
    ]
  }
}
```

**Result:**
- Salutation: **"Dear Hiring Manager,"**
- Company: **"Hiring Team"**
- Valediction: **"Sincerely,"**
- Signature: **"Jane Smith"**

---

## 📝 Example Requests

### **Example 1: Full Details (Known Recipient)**

```json
{
  "templateId": "cover_letter_expert_001",
  "coverLetter": {
    "header": {
      "name": "Michael Chen",
      "email": "michael.chen@email.com",
      "phone": "+1-555-234-5678",
      "date": "February 1, 2025"
    },
    "recipient": {
      "name": "Dr. Emily Davis",
      "company": "Innovation Labs",
      "position": "Senior Software Architect"
    },
    "salutation": "Dear Dr. Davis",
    "content": [
      "I am writing to express my strong interest in the Senior Software Architect position at Innovation Labs.",
      "With over 10 years of experience in distributed systems and cloud architecture, I have successfully designed and implemented solutions that scale to millions of users.",
      "I am particularly impressed by Innovation Labs' commitment to cutting-edge technology and would love to contribute to your team.",
      "I would welcome the opportunity to discuss how my expertise can help Innovation Labs achieve its technical goals."
    ],
    "closing": {
      "valediction": "Kind regards",
      "name": "Michael Chen"
    }
  }
}
```

### **Example 2: Generic (Unknown Recipient)**

```json
{
  "templateId": "cover_letter_professional_002",
  "coverLetter": {
    "header": {
      "name": "Alex Rodriguez",
      "email": "alex.r@email.com",
      "phone": "555-777-8888",
      "date": "2025-01-20"
    },
    "recipient": {
      "company": "StartupXYZ",
      "position": "Full Stack Developer"
    },
    "content": [
      "I am excited to apply for the Full Stack Developer position at StartupXYZ.",
      "My 3 years of experience with React, Node.js, and PostgreSQL make me well-suited for this role.",
      "I am passionate about building user-centric applications and would love to contribute to your growing team.",
      "Thank you for your time and consideration."
    ]
  }
}
```

**Result:**
- Salutation: **"Dear Hiring Manager,"** (auto-generated)
- Shows: **"StartupXYZ"** and **"Full Stack Developer Position"**
- Valediction: **"Sincerely,"** (default)
- Signature: **"Alex Rodriguez"** (from header.name)

---

## 🚀 API Usage

### **cURL Example:**

```bash
curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d @src/main/resources/input/ui-format-cover-letter.json \
  -o john_doe_cover_letter.pdf
```

### **JavaScript/React Example:**

```javascript
const generateCoverLetter = async (formData) => {
  const request = {
    templateId: formData.templateId || "cover_letter_professional_001",
    coverLetter: {
      header: {
        name: formData.candidateName,
        email: formData.email,
        phone: formData.phone,
        date: formData.date || new Date().toISOString().split('T')[0]
      },
      recipient: formData.recipient ? {
        name: formData.recipient.name,
        company: formData.recipient.company,
        position: formData.recipient.position
      } : undefined,
      salutation: formData.salutation,
      content: formData.contentParagraphs,
      closing: formData.closing ? {
        valediction: formData.closing.valediction,
        name: formData.closing.name
      } : undefined
    }
  };

  const response = await fetch('/api/pdf/generate-cover-letter', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request)
  });

  if (response.ok) {
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${formData.candidateName.replace(/\s+/g, '_')}_cover_letter.pdf`;
    a.click();
  }
};
```

---

## ✅ Validation Rules

### **Required Fields:**
- ✅ `templateId`
- ✅ `coverLetter.header.name`
- ✅ `coverLetter.header.email`
- ✅ `coverLetter.header.phone`
- ✅ `coverLetter.header.date`
- ✅ `coverLetter.content` (array with at least 1 paragraph)

### **Optional Fields (Auto-defaults applied):**
- `coverLetter.recipient.*` - All fields optional
- `coverLetter.salutation` - Auto-generated if missing
- `coverLetter.closing.*` - Defaults applied if missing

### **Error Response (400 Bad Request):**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Header information is required"
}
```

---

## 📦 Available Templates

### **Starter Templates:**
- `cover_letter_starter_001` - Classic Professional
- `cover_letter_starter_002` - Clean Minimal
- `cover_letter_starter_003` - Traditional

### **Professional Templates:**
- `cover_letter_professional_001` - Modern Executive
- `cover_letter_professional_002` - Corporate Professional
- `cover_letter_professional_003` - Business Professional

### **Expert Templates:**
- `cover_letter_expert_001` - Premium Portfolio
- `cover_letter_expert_002` - Executive Premium
- `cover_letter_expert_003` - Elite Professional

---

## 🔄 Smart Defaults

The API automatically handles missing fields:

1. **No recipient.name?** → Salutation becomes `"Dear Hiring Manager,"`
2. **No recipient.company?** → Shows `"Hiring Team"`
3. **No salutation?** → Auto-generated based on recipient.name
4. **No closing.valediction?** → Defaults to `"Sincerely,"`
5. **No closing.name?** → Uses `header.name`

---

## 🧪 Test Files

### **Full Format:**
```bash
curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d @src/main/resources/input/ui-format-cover-letter.json \
  -o full_cover_letter.pdf
```

### **Minimal Format:**
```bash
curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d @src/main/resources/input/ui-format-minimal.json \
  -o minimal_cover_letter.pdf
```

---

## 📤 Response

**Success (200 OK):**
- **Content-Type:** `application/pdf`
- **Content-Disposition:** `attachment; filename="{name}_cover_letter.pdf"`
- **Body:** PDF binary data

**Filename format:** `{header.name}_cover_letter.pdf` (spaces replaced with underscores)

Example: `John_Doe_cover_letter.pdf`

---

**The API now exactly matches your UI JSON structure!** 🎉
