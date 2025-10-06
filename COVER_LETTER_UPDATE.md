# Cover Letter API Update - Generic Salutations

## ✅ What Changed

The cover letter API now supports **generic professional salutations** when hiring manager details are unknown.

---

## 🎯 Key Features

### **1. Smart Defaults**
When recipient information is not provided, the system automatically uses:
- **Salutation:** "Dear Hiring Manager,"
- **Closing:** "Sincerely,"
- **Signature:** Candidate's name
- **Company:** "Hiring Team" (if not provided)

### **2. Optional Recipient Fields**
All recipient fields are now **optional**:
- ✅ `recipientName` - Optional
- ✅ `recipientTitle` - Optional
- ✅ `companyAddress` - Optional
- ✅ `salutation` - Auto-generated if not provided
- ✅ `closing` - Defaults to "Sincerely,"
- ✅ `signature` - Defaults to candidate name

### **3. Intelligent Salutation Logic**

The system chooses salutation in this priority:

1. **Custom salutation provided** → Use it
2. **Recipient name provided** → "Dear [Name],"
3. **Recipient title provided** → "Dear [Title],"
4. **Nothing provided** → "Dear Hiring Manager,"

---

## 📋 Minimal Request Format

### **Before (Full Details Required):**
```json
{
  "templateId": "cover_letter_professional_001",
  "coverLetter": {
    "candidateName": "John Doe",
    "email": "john@email.com",
    "phone": "555-1234",
    "address": "123 Main St",
    "date": "Jan 15, 2024",
    "recipientName": "Sarah Johnson",        // Required
    "recipientTitle": "HR Manager",          // Required
    "companyName": "Tech Corp",              // Required
    "companyAddress": "456 Business Ave",    // Required
    "salutation": "Dear Ms. Johnson,",       // Required
    "bodyParagraphs": ["..."],
    "closing": "Sincerely,",                 // Required
    "signature": "John Doe"                  // Required
  }
}
```

### **After (Minimal - Generic):**
```json
{
  "templateId": "cover_letter_professional_001",
  "coverLetter": {
    "candidateName": "John Doe",
    "email": "john@email.com",
    "phone": "555-1234",
    "address": "123 Main St",
    "date": "Jan 15, 2024",
    "companyName": "Tech Corp",
    "bodyParagraphs": [
      "I am interested in the position...",
      "I have relevant experience...",
      "Thank you for your consideration."
    ]
  }
}
```

**Result:**
- Salutation: **"Dear Hiring Manager,"**
- Closing: **"Sincerely,"**
- Signature: **"John Doe"**

---

## 🔧 What Was Updated

### **1. CoverLetter Model**
```java
// Added smart defaults method
public void applyDefaults() {
    if (salutation == null || salutation.isEmpty()) {
        if (recipientName != null) {
            salutation = "Dear " + recipientName + ",";
        } else if (recipientTitle != null) {
            salutation = "Dear " + recipientTitle + ",";
        } else {
            salutation = "Dear Hiring Manager,";  // Generic default
        }
    }
    // ... other defaults
}
```

### **2. Controller**
```java
// Apply defaults before processing
coverLetter.applyDefaults();
```

### **3. Validation**
- Only **required fields** are validated
- Recipient fields are **optional** (no validation)
- Auto-defaults applied when missing

### **4. Templates**
Updated to conditionally show recipient fields:
```html
<div th:if="${recipientName != null and !recipientName.isEmpty()}"
     th:text="${recipientName}">
</div>
```

---

## 📚 Documentation

### **New Guides:**
1. **`GENERIC_COVER_LETTER_GUIDE.md`** - Complete guide for generic cover letters
2. **`generic-cover-letter.json`** - Example minimal request

### **Updated:**
- **`COVER_LETTER_API.md`** - Still valid for full format
- **`sample-cover-letter.json`** - Example with all fields

---

## 🧪 Testing

### **Test Generic Cover Letter:**
```bash
curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d @src/main/resources/input/generic-cover-letter.json \
  -o generic_cover_letter.pdf
```

### **Test Full Cover Letter:**
```bash
curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
  -H "Content-Type: application/json" \
  -d @src/main/resources/input/sample-cover-letter.json \
  -o full_cover_letter.pdf
```

---

## ✅ Required vs Optional Fields

### **Required (Must Provide):**
- ✅ `templateId`
- ✅ `candidateName`
- ✅ `email`
- ✅ `phone`
- ✅ `address`
- ✅ `date`
- ✅ `bodyParagraphs` (array with at least 1 paragraph)

### **Optional (Auto-defaults):**
- 🔧 `recipientName` - Hidden if not provided
- 🔧 `recipientTitle` - Hidden if not provided
- 🔧 `companyName` - Defaults to "Hiring Team"
- 🔧 `companyAddress` - Hidden if not provided
- 🔧 `salutation` - Auto-generated: "Dear Hiring Manager,"
- 🔧 `closing` - Defaults to "Sincerely,"
- 🔧 `signature` - Defaults to `candidateName`

---

## 🎯 Use Cases

### **Use Case 1: Job Board Application (No Contact Info)**
```json
{
  "templateId": "cover_letter_professional_001",
  "coverLetter": {
    "candidateName": "Jane Smith",
    "email": "jane@email.com",
    "phone": "555-9876",
    "address": "123 Oak Ave, City, State",
    "date": "February 1, 2024",
    "companyName": "ABC Corporation",
    "bodyParagraphs": ["I am applying for...", "My experience...", "Thank you..."]
  }
}
```
**Output:** "Dear Hiring Manager," salutation

### **Use Case 2: Known Company, Unknown Contact**
```json
{
  "templateId": "cover_letter_expert_002",
  "coverLetter": {
    "candidateName": "Mike Chen",
    "email": "mike@email.com",
    "phone": "555-5555",
    "address": "789 Elm St, City, State",
    "date": "March 1, 2024",
    "recipientTitle": "Engineering Manager",
    "companyName": "Tech Innovations",
    "bodyParagraphs": ["I am interested...", "My skills...", "Looking forward..."]
  }
}
```
**Output:** "Dear Engineering Manager," salutation

### **Use Case 3: Full Contact Details**
```json
{
  "templateId": "cover_letter_starter_003",
  "coverLetter": {
    "candidateName": "Alex Rodriguez",
    "email": "alex@email.com",
    "phone": "555-7777",
    "address": "321 Pine St, City, State",
    "date": "April 1, 2024",
    "recipientName": "Dr. Emily Davis",
    "recipientTitle": "VP of Engineering",
    "companyName": "StartupXYZ",
    "companyAddress": "999 Innovation Blvd, City, State",
    "bodyParagraphs": ["I am writing...", "My background...", "Thank you..."]
  }
}
```
**Output:** "Dear Dr. Emily Davis," salutation

---

## 🚀 Quick Start

1. **Generate generic cover letter:**
   ```bash
   mvn spring-boot:run

   curl -X POST http://localhost:8080/api/pdf/generate-cover-letter \
     -H "Content-Type: application/json" \
     -d @src/main/resources/input/generic-cover-letter.json \
     -o generic.pdf

   open generic.pdf
   ```

2. **Result:**
   - Professional "Dear Hiring Manager," salutation
   - Clean layout without missing recipient details
   - Ready to submit!

---

## 📊 Summary

| Feature | Before | After |
|---------|--------|-------|
| **Recipient fields** | Required | Optional |
| **Salutation** | Must provide | Auto-generated |
| **Closing** | Must provide | Defaults to "Sincerely," |
| **Signature** | Must provide | Defaults to candidate name |
| **Missing recipient** | Error | Uses "Dear Hiring Manager," |
| **Minimum fields** | 13 fields | 7 fields |

---

**Perfect for job applications where hiring manager details are unknown!** 🎉

See `GENERIC_COVER_LETTER_GUIDE.md` for complete documentation.
