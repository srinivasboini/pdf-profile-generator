# PDF Profile Generator with OpenAI Optimization

A complete Java Maven application that generates professional PDF resumes from HTML templates using iText 7 pdfHTML, with AI-powered profile optimization via OpenAI API.

## Features

- **HTML to PDF Conversion**: Uses iText 7 with pdfHTML for high-quality PDF generation
- **AI-Powered Optimization**: Leverages OpenAI to optimize candidate profiles for specific job descriptions
- **Template Engine**: Uses Thymeleaf for flexible HTML templating
- **Professional Formatting**: Beautiful, clean PDF output with professional styling
- **Easy Configuration**: Simple JSON-based profile input

## Project Structure

```
pdfhtml/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/pdfgen/
    │   ├── ProfilePdfGenerator.java          # Main application
    │   ├── model/
    │   │   ├── CandidateProfile.java         # Profile data model
    │   │   ├── Experience.java               # Experience data model
    │   │   └── Education.java                # Education data model
    │   └── service/
    │       ├── ProfileOptimizer.java         # OpenAI integration service
    │       ├── TemplateService.java          # Thymeleaf template processor
    │       └── PdfGeneratorService.java      # iText PDF generator
    └── resources/
        ├── templates/
        │   └── profile_template.html         # HTML template with placeholders
        └── input/
            ├── profile.json                  # Sample candidate profile
            └── job-description.txt           # Sample job description
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- OpenAI API key

## Dependencies

- **iText 7** (8.0.2): Core PDF functionality
- **iText pdfHTML** (5.0.2): HTML to PDF conversion
- **Thymeleaf** (3.1.2): Template processing
- **OpenAI Java Client** (0.18.2): OpenAI API integration
- **Gson** (2.10.1): JSON parsing

## Setup

1. **Clone or navigate to the project directory**

2. **Set your OpenAI API key**:
   ```bash
   export OPENAI_API_KEY=sk-your-api-key-here
   ```

3. **Build the project**:
   ```bash
   mvn clean package
   ```

## Usage

### Running the Application

```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.example.pdfgen.ProfilePdfGenerator" \
  -Dexec.args="src/main/resources/input/profile.json src/main/resources/input/job-description.txt"

# Or after building, using Java
java -cp target/pdfhtml-1.0-SNAPSHOT.jar com.example.pdfgen.ProfilePdfGenerator \
  src/main/resources/input/profile.json \
  src/main/resources/input/job-description.txt \
  custom-output.pdf
```

### Command Line Arguments

```
java -cp target/pdfhtml-1.0-SNAPSHOT.jar com.example.pdfgen.ProfilePdfGenerator \
  <profile.json> <job-description.txt> [output.pdf]
```

- **profile.json**: Path to candidate profile JSON file (required)
- **job-description.txt**: Path to job description text file (required)
- **output.pdf**: Output PDF path (optional, defaults to `optimized-profile.pdf`)

### Input File Format

#### Profile JSON (`profile.json`)

```json
{
  "name": "Sarah Johnson",
  "email": "sarah.johnson@email.com",
  "phone": "+1 (555) 123-4567",
  "location": "San Francisco, CA",
  "summary": "Experienced software engineer...",
  "skills": ["Java", "Spring Boot", "AWS", "Docker"],
  "experience": [
    {
      "title": "Senior Software Engineer",
      "company": "TechCorp Solutions",
      "duration": "Jan 2021 - Present",
      "description": "<ul><li>Achievement 1</li><li>Achievement 2</li></ul>"
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
```

#### Job Description (`job-description.txt`)

Plain text file containing the target job description.

## How It Works

1. **Input Reading**: Reads candidate profile (JSON) and job description (text)
2. **AI Optimization**: Sends both to OpenAI API to optimize the profile for the job
3. **Template Processing**: Uses Thymeleaf to inject optimized data into HTML template
4. **PDF Generation**: Converts the processed HTML to PDF using iText 7 pdfHTML
5. **Output**: Saves the professional PDF to the specified location

## Customization

### Modify the HTML Template

Edit `src/main/resources/templates/profile_template.html` to customize the PDF layout and styling.

### Change OpenAI Model

Modify `ProfileOptimizer.java` to use a different model:

```java
public ProfileOptimizer(String apiKey) {
    this(apiKey, "gpt-3.5-turbo", Duration.ofSeconds(60));
}
```

### Adjust PDF Settings

Modify `PdfGeneratorService.java` to customize PDF properties:

```java
ConverterProperties converterProperties = new ConverterProperties();
// Add custom configurations here
```

## Troubleshooting

### OpenAI API Key Not Set
```
Error: OPENAI_API_KEY environment variable is not set.
```
**Solution**: Export your API key: `export OPENAI_API_KEY=sk-your-key`

### File Not Found
```
Error: java.nio.file.NoSuchFileException
```
**Solution**: Verify file paths are correct and files exist

### PDF Not Generating
Check that:
- HTML template is valid
- All required fields are present in the profile JSON
- Output directory exists and is writable

## License

This project is provided as-is for educational and demonstration purposes.

## Dependencies Licenses

- iText 7: AGPL (Commercial license required for commercial use)
- Thymeleaf: Apache License 2.0
- OpenAI Java Client: MIT License
- Gson: Apache License 2.0