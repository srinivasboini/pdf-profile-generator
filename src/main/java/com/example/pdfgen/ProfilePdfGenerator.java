package com.example.pdfgen;

import com.example.pdfgen.model.CandidateProfile;
import com.example.pdfgen.service.PdfGeneratorService;
import com.example.pdfgen.service.ProfileOptimizer;
import com.example.pdfgen.service.TemplateService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Main application class for generating optimized candidate profile PDFs
 */
public class ProfilePdfGenerator {

    private static final String DEFAULT_TEMPLATE = "minimalist_profile_template";
    private static final String DEFAULT_OUTPUT = "optimized-profile-minimalist_profile_template.pdf";
    // For modern template
   /* private static final String DEFAULT_TEMPLATE
            = "modern_profile_template";

    // For minimalist template
    private static final String DEFAULT_TEMPLATE
            = "minimalist_profile_template";

    // For original template
    private static final String DEFAULT_TEMPLATE
            = "profile_template";*/


    public static void main(String[] args) {
        try {


            String profilePath = "src/main/resources/input/profile.json";
            String jobDescriptionPath = "src/main/resources/input/job-description.txt";
            String openAiApiKey = System.getenv("OPENAI_API_KEY");

            String outputPath =  DEFAULT_OUTPUT;

            // Validate OpenAI API key
            if (openAiApiKey == null || openAiApiKey.isEmpty()) {
                System.err.println("Error: OPENAI_API_KEY environment variable is not set.");
                System.err.println("Please set it using: export OPENAI_API_KEY=your-api-key");
                System.exit(1);
            }

            System.out.println("=== Profile PDF Generator ===");
            System.out.println("Profile: " + profilePath);
            System.out.println("Job Description: " + jobDescriptionPath);
            System.out.println("Output: " + outputPath);
            System.out.println();

            // Read input files
            String profileJson = readFile(profilePath);
            String jobDescription = readFile(jobDescriptionPath);

            // Parse the candidate profile from JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            CandidateProfile originalProfile = gson.fromJson(profileJson, CandidateProfile.class);

            System.out.println("Original Profile:");
            System.out.println("  Name: " + originalProfile.getName());
            System.out.println("  Email: " + originalProfile.getEmail());
            System.out.println();

            // Optimize the profile using OpenAI
            ProfileOptimizer optimizer = new ProfileOptimizer(openAiApiKey, "gpt-5", 180);
            String optimizedProfileJson = optimizer.optimizeProfile(profileJson, jobDescription);

            // Clean up the response to extract JSON if wrapped in markdown
            optimizedProfileJson = cleanJsonResponse(optimizedProfileJson);

            // Parse the optimized profile
            CandidateProfile optimizedProfile = gson.fromJson(optimizedProfileJson, CandidateProfile.class);

            System.out.println("Optimized Profile:");
            System.out.println("  Name: " + optimizedProfile.getName());
            System.out.println("  Summary length: " + optimizedProfile.getSummary().length() + " characters");
            System.out.println("  Skills count: " + optimizedProfile.getSkills().size());
            System.out.println();

            // Process the template with optimized profile data
            TemplateService templateService = new TemplateService();
            String processedHtml = templateService.processTemplate(DEFAULT_TEMPLATE, optimizedProfile);

            // Generate PDF from the processed HTML
            PdfGeneratorService pdfService = new PdfGeneratorService();
            pdfService.generatePdf(processedHtml, outputPath);

            System.out.println();
            System.out.println("✓ Success! Optimized profile PDF generated: " + outputPath);

            // Clean up
            optimizer.close();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Reads the entire content of a file
     */
    private static String readFile(String filePath) throws IOException {
        System.out.println("Reading file: " + filePath);
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Cleans JSON response by removing markdown code blocks if present
     */
    private static String cleanJsonResponse(String response) {
        // Remove markdown code blocks if present
        String cleaned = response.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

    /**
     * Prints usage instructions
     */
    private static void printUsage() {
        System.out.println("Usage: java -jar pdfhtml.jar <profile.json> <job-description.txt> [output.pdf]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  profile.json          - Path to candidate profile JSON file");
        System.out.println("  job-description.txt   - Path to job description text file");
        System.out.println("  output.pdf            - (Optional) Output PDF path (default: optimized-profile.pdf)");
        System.out.println();
        System.out.println("Environment Variables:");
        System.out.println("  OPENAI_API_KEY        - Your OpenAI API key (required)");
        System.out.println();
        System.out.println("Example:");
        System.out.println("  export OPENAI_API_KEY=sk-your-api-key");
        System.out.println("  java -jar pdfhtml.jar src/main/resources/input/profile.json src/main/resources/input/job-description.txt");
    }
}