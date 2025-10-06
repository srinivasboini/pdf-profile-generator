# Generic Cover Letter Generation Guide

## 🎯 Overview

The cover letter API now supports **generic cover letters** when you don't know the hiring manager's name or details. The system automatically uses professional defaults like "Dear Hiring Manager," when recipient information is missing.

---

## ✨ What's New

### Smart Defaults
When recipient details are **unknown or not provided**, the system automatically:
- ✅ Uses **"Dear Hiring Manager,"** as salutation
- ✅ Uses **"Sincerely,"** as closing
- ✅ Uses candidate name as signature
- ✅ Shows only available recipient information in the letter

---

## 📋 Simplified Request Format

### **Minimal Required Fields** (No Recipient Details Needed)

```json
{
  "templateId": "cover_letter_professional_001",
  "coverLetter": {
    "candidateName": "John Doe",
    "email": "john.doe@email.com",
    "phone": "(555) 123-4567",
    "address": "123 Main Street, San Francisco, CA 94102",
    "date": "January 15, 2024",
    "companyName": "Tech Innovations Inc.",
    "bodyParagraphs": [
      "I am writing to express my interest in the Senior Software Engineer position at your company...",
      "My experience includes 8 years of full-stack development...",
      "I would welcome the opportunity to discuss this further. Thank you for your consideration."
    ]
  }
}
```

**Result:**
- Salutation: **"Dear Hiring Manager,"**
- Company: **"Tech Innovations Inc."**
- Closing: **"Sincerely,"**
- Signature: **"John Doe"**

---

## 📊 Field Requirements

### **Required Fields** ✅

| Field | Example | Description |
|-------|---------|-------------|
| `templateId` | `"cover_letter_professional_001"` | Template to use |
| `candidateName` | `"John Doe"` | Candidate's full name |
| `email` | `"john@email.com"` | Candidate's email |
| `phone` | `"555-1234"` | Candidate's phone |
| `address` | `"123 Main St, City, State"` | Candidate's address |
| `date` | `"January 15, 2024"` | Letter date |
| `bodyParagraphs` | `["First...", "Second..."]` | Letter content (array) |

### **Optional Fields** 🔧

| Field | Default Value | When to Use |
|-------|---------------|-------------|
| `recipientName` | `null` (hidden) | When you know the hiring manager's name |
| `recipientTitle` | `null` (hidden) | When you know their title |
| `companyName` | `"Hiring Team"` | Company name (if known) |
| `companyAddress` | `null` (hidden) | Company address (if known) |
| `salutation` | Auto-generated | Custom greeting (if needed) |
| `closing` | `"Sincerely,"` | Custom closing (if needed) |
| `signature` | Uses `candidateName` | Custom signature (if needed) |

---

## 🎨 Salutation Logic

The system intelligently generates salutations based on available information:

### Priority Order:

1. **If `salutation` is provided** → Use it as-is
   ```json
   "salutation": "Dear Dr. Smith,"
   ```

2. **If `recipientName` is provided** → Generate from name
   ```json
   "recipientName": "Sarah Johnson"
   ```
   **Result:** `"Dear Sarah Johnson,"`

3. **If `recipientTitle` is provided** → Generate from title
   ```json
   "recipientTitle": "Hiring Manager"
   ```
   **Result:** `"Dear Hiring Manager,"`

4. **If nothing is provided** → Use generic default
   **Result:** `"Dear Hiring Manager,"`

---

## 📝 Example Scenarios

### **Scenario 1: Unknown Recipient (Most Common)**

```json
{
  "templateId": "cover_letter_professional_001",
  "coverLetter": {
    "candidateName": "Jane Smith",
    "email": "jane@email.com",
    "phone": "555-9876",
    "address": "456 Oak Ave, New York, NY",
    "date": "February 1, 2024",
    "companyName": "ABC Corporation",
    "bodyParagraphs": [
      "I am excited to apply for the Software Engineer position...",
      "With 5 years of experience in cloud architecture...",
      "Thank you for considering my application."
    ]
  }
}
```

**PDF Output:**
```
Jane Smith
jane@email.com • 555-9876 • 456 Oak Ave, New York, NY

February 1, 2024

ABC Corporation

Dear Hiring Manager,

I am excited to apply for the Software Engineer position...
...

Sincerely,
Jane Smith
```

---

### **Scenario 2: Known Recipient Name**

```json
{
  "templateId": "cover_letter_expert_001",
  "coverLetter": {
    "candidateName": "Michael Chen",
    "email": "michael@email.com",
    "phone": "555-5555",
    "address": "789 Elm St, Boston, MA",
    "date": "March 1, 2024",
    "recipientName": "Dr. Emily Davis",
    "companyName": "Tech Innovations",
    "bodyParagraphs": [
      "I am writing to express my interest in the Senior Architect role...",
      "My 12 years of experience aligns perfectly...",
      "I look forward to discussing this opportunity."
    ]
  }
}
```

**PDF Output:**
```
...
Dr. Emily Davis
Tech Innovations

Dear Dr. Emily Davis,
...
```

---

### **Scenario 3: Known Title Only**

```json
{
  "templateId": "cover_letter_starter_001",
  "coverLetter": {
    "candidateName": "Alex Rodriguez",
    "email": "alex@email.com",
    "phone": "555-7777",
    "address": "321 Pine St, Seattle, WA",
    "date": "April 1, 2024",
    "recipientTitle": "Engineering Manager",
    "companyName": "StartupXYZ",
    "bodyParagraphs": [
      "I am applying for the Junior Developer position...",
      "As a recent graduate with internship experience...",
      "Thank you for your time and consideration."
    ]
  }
}
```

**PDF Output:**
```
...
Engineering Manager
StartupXYZ

Dear Engineering Manager,
...
```

---

## 🚀 API Usage Examples

### **cURL - Generic Cover Letter**

```bash
curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d '{
    "templateId": "cover_letter_professional_001",
    "coverLetter": {
      "candidateName": "John Doe",
      "email": "john@email.com",
      "phone": "555-1234",
      "address": "123 Main St, City, State",
      "date": "January 15, 2024",
      "companyName": "Tech Corp",
      "bodyParagraphs": [
        "I am interested in the position at your company.",
        "I have 5 years of relevant experience.",
        "Thank you for your consideration."
      ]
    }
  }' \
  -o cover_letter.pdf
```

### **JavaScript/Fetch - Generic Cover Letter**

```javascript
const generateGenericCoverLetter = async (templateId, candidateInfo, companyName, bodyContent) => {
  const request = {
    templateId: templateId,
    coverLetter: {
      candidateName: candidateInfo.name,
      email: candidateInfo.email,
      phone: candidateInfo.phone,
      address: candidateInfo.address,
      date: new Date().toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      }),
      companyName: companyName,
      bodyParagraphs: bodyContent  // Array of paragraph strings
      // No recipient details needed!
    }
  };

  const response = await fetch('/api/pdf/generate-cover-letter', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request)
  });

  const blob = await response.blob();
  // Download PDF
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `${candidateInfo.name}_cover_letter.pdf`;
  a.click();
};

// Usage
await generateGenericCoverLetter(
  'cover_letter_professional_001',
  {
    name: 'Jane Smith',
    email: 'jane@email.com',
    phone: '555-9876',
    address: '456 Oak Ave, City, State'
  },
  'Tech Innovations Inc.',
  [
    'I am excited to apply for the Software Engineer position at your company.',
    'With 8 years of experience in full-stack development, I have the skills needed.',
    'I would love to discuss how I can contribute to your team. Thank you!'
  ]
);
```

---

## 🔍 Professional Salutation Best Practices

Based on 2024 industry standards:

### ✅ **Recommended (Professional)**
- "Dear Hiring Manager,"
- "Dear [Department] Team," (e.g., "Dear Engineering Team,")
- "Dear [Job Title] Hiring Team,"
- "Dear Talent Acquisition Team,"

### ❌ **Avoid (Outdated)**
- "To Whom It May Concern," (too formal/generic)
- "Dear Sir or Madam," (assumes gender)
- "Dear Sir/Madam," (outdated)
- "Hey there," (too casual)

**Default Used:** `"Dear Hiring Manager,"` (Most professional and widely accepted)

---

## 📦 Test Files Provided

### **1. Generic Cover Letter (No Recipient)**
```bash
curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d @src/main/resources/input/generic-cover-letter.json \
  -o generic_cover_letter.pdf
```

### **2. Full Cover Letter (With Recipient)**
```bash
curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d @src/main/resources/input/sample-cover-letter.json \
  -o full_cover_letter.pdf
```

---

## ✅ Validation

The API validates **only required fields**:

**Required:**
- ✅ Template ID
- ✅ Candidate name, email, phone, address
- ✅ Date
- ✅ Body paragraphs (at least 1)

**Optional (No validation):**
- Recipient name, title, address
- Company name (defaults to "Hiring Team")
- Salutation, closing, signature (auto-generated)

**Error Response (400):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Candidate name is required"
}
```

---

## 🎯 UI Integration Tips

### **Form Fields for Generic Cover Letter**

**Required Fields:**
1. Candidate Name ✅
2. Email ✅
3. Phone ✅
4. Address ✅
5. Date ✅ (can auto-fill with current date)
6. Company Name (optional, defaults to "Hiring Team")
7. Body Content ✅ (3-5 paragraphs)

**Optional Fields (Show as "Advanced"):**
- Recipient Name
- Recipient Title
- Company Address
- Custom Salutation
- Custom Closing

### **Recommendation:**
- By default, show only required fields
- Add "Know the hiring manager?" toggle to show recipient fields
- Auto-generate date to current date
- Provide paragraph count guidance (3-5 paragraphs recommended)

---

## 🚀 Quick Start

1. **Start server:**
   ```bash
   mvn spring-boot:run
   ```

2. **Generate generic cover letter:**
   ```bash
   curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
     -H "Content-Type: application/json" \
     -d @src/main/resources/input/generic-cover-letter.json \
     -o test_generic.pdf

   open test_generic.pdf
   ```

---

**Perfect for when you're applying to job postings without specific contact information!** 🎉
